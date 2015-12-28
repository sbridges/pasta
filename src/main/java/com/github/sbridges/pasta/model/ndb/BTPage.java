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
import java.util.TreeSet;

import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.PType;
import com.github.sbridges.pasta.model.Page;
import com.github.sbridges.pasta.util.CollectionUtils;

/**
 * 2.2.2.7.7.1 BTPAGE
 * 
 * A BTPAGE structure implements a generic BTree using 512-byte pages.
 */
public class BTPage {
    
    //8192 - 16 byte for the block trailer
    public static final int BLOCK_MAX_SIZE_BYTES = 8176;
    
    private static final int META_OFFSET = 488;
    
    //cEnt (1 byte): The number of BTree entries stored in the page data.
    private final byte cEnt;
    //cEntMax (1 byte): The maximum number of entries that can fit inside the page data.
    private final byte cEntMax;
    //cbEnt (1 byte): The size of each BTree entry, in bytes. 
    //Note that in some cases, cbEnt can be greater than the 
    //corresponding size of the corresponding rgentries structure
    //because of alignment or other considerations. 
    //Implementations MUST use the size specified in cbEnt to advance to the next entry.
    private final byte cbEnt;
    //cLevel (1 byte): The depth level of this page. Leaf pages 
    //have a level of zero, whereas intermediate pages have a level greater 
    //than 0. This value determines the     
    private final byte cLevel;
    
    private final Page page;

    public BTPage(Page page) {
        this.page = page;
        this.cEnt = page.getSlice().readByte(META_OFFSET);
        this.cEntMax = page.getSlice().readByte(META_OFFSET + 1);
        this.cbEnt = page.getSlice().readByte(META_OFFSET + 2);
        this.cLevel = page.getSlice().readByte(META_OFFSET + 3);
        if(cLevel < 0) {
            throw new IllegalStateException("invalid cLevel:" + cLevel + " BTPage:" + this);
        }
        
        if(isNBT() && isLeaf()) {
            if(cbEnt != 32) {
                throw new IllegalStateException("invalid cbEnt:" + cbEnt + " BTPage:" + this);
            }
        } else if(cbEnt != 24) {
            throw new IllegalStateException("invalid cbEnt:" + cbEnt + " BTPage:" + this);
        }
        
        if(cEntMax != META_OFFSET / cbEnt) {
            throw new IllegalStateException("invalid cEntMax:" + cEntMax  + " cbEnt:" + cbEnt + " BTPage:" + this);
        }
        if(cEnt > cEntMax) {
            throw new IllegalStateException("invalid cEnt:" + cEnt  + " cEntMax:" + cEntMax + " BTPage:" + this);
        }
        if(cbEnt > BLOCK_MAX_SIZE_BYTES) {
            throw new IllegalStateException("cbEnt too large:" + this);
        }
        
        //dwPadding (Unicode: 4 bytes): Padding; MUST be set to zero. 
        //Note there is no padding in the ANSI version of this structure.
        for(int i =0; i < 4; i++) {
            if(page.getSlice().readByte(META_OFFSET + 4 + i) != 0) {
                throw new IllegalStateException("invalid dwPadding  BTPage:" + this);
            }
        }  
        
        if(!isBBT() && !isNBT()) {
            throw new IllegalStateException("invalid type:" + this);
        }
        
        
        if(!isLeaf()) {
            TreeSet<Long> sortedOrder = new TreeSet<>();
            List<Long> actualOrder = new ArrayList<>();
            for(BTEntry entry : getBTEntries()) {
                sortedOrder.add(entry.getBtkey());
                actualOrder.add(entry.getBtkey());
            }
            
            if(!actualOrder.equals(new ArrayList<>(sortedOrder))) {
                throw new IllegalStateException(
                        "BTPage not sorted, actual:" + actualOrder +
                        " sorted:" + sortedOrder +
                        " this:" + this);
            }
        }
        
    }

    public byte getCLevel() {
        return cLevel;
    }

    public byte getCEnt() {
        return cEnt;
    }

    public byte getCEntMax() {
        return cEntMax;
    }

    public byte getCbEnt() {
        return cbEnt;
    }
    
    public boolean isLeaf() {
        return cLevel == 0;
    }
    
    public boolean isNBT() {
        return page.getPageTrailer().getpType() == PType.ptypeNBT;
    }
    
    public boolean isBBT() {
        return page.getPageTrailer().getpType() == PType.ptypeBBT;
    }
    
    public Iterable<BTEntry> getBTEntries() {
        if(isLeaf()) {
            throw new IllegalStateException("not a leaf:" + this);
        }
        return new Iterable<BTEntry>() {
            
            @Override
            public Iterator<BTEntry> iterator() {
                return CollectionUtils.transform(getChunks(), b -> new BTEntry(b));
            }
        };
    }
    
    public Iterable<BBTEntry> getBBTEntries() {
        if(!isLeaf() || !isBBT()) {
            throw new IllegalStateException("not a leaf BBT:" + this);
        }
        return new Iterable<BBTEntry>() {
            
            @Override
            public Iterator<BBTEntry> iterator() {
                return CollectionUtils.transform(getChunks(), b -> new BBTEntry(b));
            }
        };
    }
    
    public Iterable<NBTEntry> getNBTEntries() {
        if(!isLeaf() || !isNBT()) {
            throw new IllegalStateException("not a leaf BBT:" + this);
        }
        return new Iterable<NBTEntry>() {
            
            @Override
            public Iterator<NBTEntry> iterator() {
                return CollectionUtils.transform(getChunks(), b -> new NBTEntry(b));
            }
        };
    }

    @Override
    public String toString() {
        return "BTPage [page=" + page + ", cEnt=" + cEnt + ", cEntMax=" + cEntMax + ", cbEnt="
                + cbEnt + ", cLevel=" + cLevel + "]";
    }
    
    private Iterator<PstIo> getChunks() {
        return page.getSlice().chunk(0, cEnt, cbEnt);
    } 
    
    
}
