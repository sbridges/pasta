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

import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.BID;

/**
 * 
 * 2.2.2.8.3.3.2.1 SIENTRY (Intermediate Block Entry)
 * SIENTRY are intermediate records that point to SLBLOCKs.
 *
 */
public class SIEntry {
    //nid (Unicode: 8 bytes; ANSI: 4 bytes): The key NID value to 
    //the next-level child block. This NID is only unique 
    //within the parent node. The NID is extended to 8 bytes in 
    //order for Unicode PST files to follow the general convention 
    //of 8-byte indices (see section 2.2.2.7.7.4 for details).
    NID nid;
    
    //bid (Unicode: 8 bytes; ANSI: 4 bytes): The BID of the SLBLOCK.
    BID bid;

    public SIEntry(PstIo slice) {
        this.nid = new NID(slice.readLong());
        this.bid = new BID(slice.readLong());
        
        if(nid.getIndex() == 0 || bid.getBid() == 0) {
            throw new IllegalStateException("Invalid SIEntry:" + this);
        }
    }

    @Override
    public String toString() {
        return "SIEntry [nid=" + nid + ", bid=" + bid + "]";
    }

    public NID getNid() {
        return nid;
    }

    public void setNid(NID nid) {
        this.nid = nid;
    }

    public BID getBid() {
        return bid;
    }

    public void setBid(BID bid) {
        this.bid = bid;
    }
    
    
    
    
}
