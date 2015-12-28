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

import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.BID;

/**
 * 2.2.2.8.3.3.1.1 SLENTRY (Leaf Block Entry)
 * SLENTRY are records that refer to internal subnodes of a node. 
 */
public class SLEntry {

    //nid (Unicode: 8 bytes; ANSI: 4 bytes): 
    //Local NID of the subnode. 
    //This NID is guaranteed to be unique only 
    //within the parent node.
    private final NID nid;
    
    //bidData (Unicode: 8 bytes; ANSI: 4 bytes): 
    //The BID of the data block associated with the subnode.
    private final BID bidData;
    
    //bidSub (Unicode: 8 bytes; ANSI: 4 bytes): 
    //If nonzero, the BID of the subnode of this subnode.
    private BID bidSub;
    
    
    public SLEntry(PstIo slice) {
        nid = new NID(slice.readLong());
        bidData = new BID(slice.readLong());
        long bidSubLong = slice.readLong();
        if(bidSubLong  != 0) {
            bidSub = new BID(bidSubLong);
        } else {
            bidSub = null;
        }
        
        if(bidData.getBid() == 0 || nid.getIndex() == 0) {
            throw new IllegalStateException("invalid SLEntry:" + this);
        }
        
        slice.assertExhausted();
    }


    @Override
    public String toString() {
        return "SLEntry [nid=" + nid + ", bidData=" + bidData + ", bidSub=" + bidSub
                + "]";
    }


    public NID getNid() {
        return nid;
    }


    public BID getBidData() {
        return bidData;
    }


    public Optional<BID> getBidSub() {
        return Optional.ofNullable(bidSub);
    }
    
    
    
}
