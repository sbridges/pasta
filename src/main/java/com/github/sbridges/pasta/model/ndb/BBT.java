/*
 * Copyright 2015 Sean Bridges. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 * 
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Sean Bridges ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * 
 * 
 */

package com.github.sbridges.pasta.model.ndb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.BCryptMethod;
import com.github.sbridges.pasta.model.BID;
import com.github.sbridges.pasta.model.BRef;
import com.github.sbridges.pasta.model.Page;
import com.github.sbridges.pasta.util.CollectionUtils;

/**
 *  Block BTree (BBT)
 */
public class BBT {
    private final BCryptMethod bCryptMethod;
    private final BRef BREFBBT;
    private final PstIo io;
    
    
    public BBT(BCryptMethod bCryptMethod, BRef bREFBBT, PstIo io) {
        this.bCryptMethod = bCryptMethod;
        BREFBBT = bREFBBT;
        this.io = io;
    }

    public BRef getBREFBBT() {
        return BREFBBT;
    }

    public PstIo getIo() {
        return io;
    }


    @Override
    public String toString() {
        return "BBT [BREFBBT=" + BREFBBT + ", io=" + io + "]";
    }
    
    private BTPage getRoot() {
        return new BTPage(new Page(BREFBBT, io));
    }

    public Optional<BBTEntry> find(BID bid) {
        return find(bid, getRoot());
    }
    
    public DataBlock load(BID bid) {
        Optional<BBTEntry> entry = find(bid);
        if(entry.isPresent()) {
            return load(entry.get());
        }
        throw new IllegalStateException("not found:" + bid);
    }
    
    public DataBlock load(BBTEntry bbtEntry) {
        return new DataBlock(
                bCryptMethod,
                bbtEntry.getBRef(), bbtEntry.getCb(),
                io.slice(
                        bbtEntry.getBRef().getIb(), bbtEntry.getBlockSizeInclusive())
                );

    }

    private Optional<BBTEntry> find(BID bid, BTPage page) {
        
        Optional<BTPage> leaf = BTreeUtil.findLeaf(bid.getBid(), page, io);
        if(!leaf.isPresent()) {
            return Optional.empty();
        }
        
        
        if(leaf.get().isLeaf()) {
            for(BBTEntry e : leaf.get().getBBTEntries()) {
                if(e.getBRef().getBid().equals(bid)) {
                    return Optional.of(e);
                }
            }
            return Optional.empty();
        } else {
            throw new IllegalStateException("not leaf:" + leaf.get());
        }
    }
    
    public Iterable<DataBlock> getBlocks() {
        List<BBTEntry> entries = new ArrayList<>();
        walkDepthFirst(btPage -> {
            if(btPage.isLeaf()) {
                for(BBTEntry e : btPage.getBBTEntries()) {
                    entries.add(e);
                }
            }
        });
        
        return new Iterable<DataBlock>() {
            @Override
            public Iterator<DataBlock> iterator() {
                return CollectionUtils.transform(entries.iterator(), e -> new DataBlock(
                        bCryptMethod, e.getBRef(), e.getCb(), io));
            }};
    }
    
    public void walkDepthFirst(Consumer<BTPage> consumer) {
        BTreeUtil.walkDepthFirst(getRoot(), io, consumer);
    }
    
    public String debugString() {
        return BTreeUtil.debugString(getRoot(), io);
    }
    
}
