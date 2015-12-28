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

import java.util.Optional;
import java.util.function.Consumer;

import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.BRef;
import com.github.sbridges.pasta.model.Page;

/**
 *  Node BTree (NBT)
 */
public class NBT {
    private final BRef BREFNBT;
    private final PstIo io;
    
    
    public NBT(BRef bREFBBT, PstIo io) {
        BREFNBT = bREFBBT;
        this.io = io;
    }

    public BRef getBREFNBT() {
        return BREFNBT;
    }

    public PstIo getIo() {
        return io;
    }


    @Override
    public String toString() {
        return "NBT [BREFNBT=" + BREFNBT + ", io=" + io + "]";
    }
    
    private BTPage getRoot() {
        return new BTPage(new Page(BREFNBT, io));
    }

    public Optional<NBTEntry> load(NID nid) {
        return find(nid, getRoot());
    }


    private Optional<NBTEntry> find(NID nid, BTPage page) {
        Optional<BTPage> leaf = BTreeUtil.findLeaf(nid.getNid(), page, io);
        if(!leaf.isPresent()) {
            return Optional.empty();
        }
        
        
        if(leaf.get().isLeaf()) {
            for(NBTEntry e : leaf.get().getNBTEntries()) {
                if(e.getNid().equals(nid)) {
                    return Optional.of(e);
                }
            }
            return Optional.empty();
        } else {
            throw new IllegalStateException("not leaf:" + leaf.get());
        }
    }
    
    public void walkDepthFirst(Consumer<BTPage> consumer) {
        BTreeUtil.walkDepthFirst(getRoot(), io, consumer);
    }
    
    public String debugString() {
        return BTreeUtil.debugString(getRoot(), io);
    }
    
}
