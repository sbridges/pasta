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

package com.github.sbridges.pasta.model;

/**
 * 2.2.2.2 BID (Block ID)
 * Each block is uniquely identified in the PST file using its BID value. 
 * The indexes of BIDs are assigned in a monotonically increasing fashion 
 * so that it is possible to establish the order in which blocks were
 *  created by examining the BIDs.
 */
public class BID implements Comparable<BID>{

    private final long bid;
   
    public BID(long bid) {
        this.bid = bid;
        
        //NOTE - this text is absent in the 4.0 version of the PST spec, but is present in
        //version 1.04
        //r (1 bit): Reserved bit. Readers MUST ignore this bit and treat it as zero (0) before looking up the
        //BID from the BBT. Writers MUST set this bit to zero (0).
        if((bid  & 0x01) != 0) {
            throw new IllegalStateException("r bit not 0:" + this);
        }
    }
    
    public boolean isInternal() {
        //NOTE - this text is absent in the 4.0 version of the PST spec, but is present in
        //version 1.04
        //2.2.2.2
        //i (1 bit): MUST set to "1" when the block is "Internal", or "0" when the block is not "Internal". 
        //An internal block is an intermediate block that, instead of containing actual data, contains metadata 
        //about how to locate other data blocks that contain the desired information. For more details 
        //about technical details regarding blocks, see section 2.2.2.8.
        return (bid  & 0x02) != 0;
    }

    @Override
    public String toString() {
        return "BID [bid=" + bid + ", internal=" + isInternal() + "]";
    }
    
    public long getBid() {
        return bid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (bid ^ (bid >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BID other = (BID) obj;
        if (bid != other.bid) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(BID o) {
        return Long.compare(this.bid, o.bid);
    }
    
    
    
}
