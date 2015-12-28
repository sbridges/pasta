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

package com.github.sbridges.pasta.model.ltp.hn;

import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.BID;
import com.github.sbridges.pasta.model.ndb.BBT;
import com.github.sbridges.pasta.model.ndb.DataBlock;
import com.github.sbridges.pasta.model.ndb.XBlockUtil;
import com.github.sbridges.pasta.util.ByteUtils;

/**
 * 2.3.1 HN (Heap-on-Node)
 * The Heap-on-Node defines a standard heap over a node's data stream. Taking advantage of the flexible structure of the node, the organization of the heap data can take on several forms, depending on how much data is stored in the heap.
 * For heaps whose size exceed the amount of data that can fit in one data block, the first data block in the HN contains a full header record and a trailer record. With the exception of blocks that require an HNBITMAPHDR structure, subsequent data blocks only have an abridged header and a trailer. This is explained in more detail in the following sections. Because the heap is a structure that is defined at a higher layer than the NDB, the heap structures are written to the external data sections of data blocks and do not use any information from the data block's NDB structure.
 */
public class HN {

    private final BBT bbt;
    private final BID bid;
    private final HNHDR hnhdr;
    
    public HNHDR getHnhdr() {
        return hnhdr;
    }

    public HN(BBT bbt, BID bid) {
        this.bbt = bbt;
        this.bid = bid;
       
        PstIo slice = getDataBlockSlice(0);
        hnhdr = new HNHDR(slice.slice(0, 12));
        if(hnhdr.getHidUserRoot().getHidIndex() != 1 && hnhdr.getHidUserRoot().getHidBlockIndex() != 0) {
            throw new IllegalStateException("invalid first hid:" + hnhdr);
        }
        
        //make sure we can load the initial root
        load(hnhdr.getHidUserRoot()).size();
    }
    
    public HID getHidUserRoot() {
        return hnhdr.getHidUserRoot();
    }
    
    public PstIo load(HID hid) {
        PstIo slice = getDataBlockSlice(hid.getHidBlockIndex());
        HNPAGEMAP pageMap = getPageMap(hid.getHidBlockIndex(), slice);
        return pageMap.slice(hid.getHidIndex(), slice);
        
    }
    
    public String debugString() {
        StringBuilder sb = new StringBuilder();
        sb.append("hnhdr:" + hnhdr);
        if(bid.isInternal()) {
            //multilevel, handle this
            throw new IllegalStateException();
        }
        
        PstIo io = getDataBlockSlice(0);
        HNPAGEMAP pgMap = getPageMap(0, getDataBlockSlice(0));
        sb.append(", HNPAGEMAP=").append(pgMap);
        
        int[] rgibAlloc = pgMap.getRgibAlloc();
        for(int i = 0; i + 1 < rgibAlloc.length; i++) {
            int size = (rgibAlloc[i+1] - rgibAlloc[i]);
            sb.append("\n  block_" + (i+1) +"[size=" +  size + ", contents=" + ByteUtils.bytesToHex(io.read(rgibAlloc[i], size)) +"]");
        }
        return sb.toString();
    }
    
    private HNPAGEMAP getPageMap(int hidBlockIndex, PstIo slice) {
        slice.seek(0);
        int ibHnpm = slice.readW();
        
        if(hidBlockIndex == 0) {
            return new HNPAGEMAP(
                    slice.slice(ibHnpm, (int) slice.size() - ibHnpm)
                    );
        } else {
            
            return new HNPAGEMAP(
                    slice.slice(ibHnpm, (int) slice.size() - ibHnpm)
                    );
        }
    }
    
    private PstIo getDataBlockSlice(int hidBlockIndex) {
        DataBlock db = XBlockUtil.getDataBlock(hidBlockIndex, bid, bbt);
        return db.getDataDecrypted();
    }


    @Override
    public String toString() {
        return "HeapOnNode [bbt=" + bbt + ", bid=" + bid + "]";
    }
}
