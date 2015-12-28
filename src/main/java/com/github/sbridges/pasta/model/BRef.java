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

import com.github.sbridges.pasta.io.PstIo;

public class BRef {

    //Every block allocated in the PST file is identified using the BID structure. 
    //This structure varies in size according the format of the file. 
    //In the case of ANSI files, the structure is a 32-bit unsigned value, 
    //while in Unicode files it is a 64-bit unsigned long. In addition, there are two types of BIDs:
    //
    // 1. Page BIDs use all of the bits of the structure and are incremented by 1.
    //
    // 2. Data Block BIDs reserve the two least significant bits for internal use; 
    // as a result these increment by 4 each time a new one is assigned. 
    // Third-party implementations MUST ignore these bits and assign them a value of 0.
    private final BID bid;
    
    //The IB (Byte Index) is used to represent an absolute offset within the PST file 
    //with respect to the beginning of the file. The IB is a simple unsigned integer
    //value and is 64 bits in Unicode versions and 32 bits in ANSI versions.
    private final long ib;
    
    public BRef(PstIo slice) {
        if(slice.size() != 16) {
            throw new IllegalArgumentException("length must be 16, not:" +  slice.size());
        }
        
        bid = new BID(slice.readLong());
        ib = slice.readLong();
    }

    public BID getBid() {
        return bid;
    }

    public long getIb() {
        return ib;
    }

    @Override
    public String toString() {
        return "BRef [bid=" + bid + ", ib=" + ib + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bid == null) ? 0 : bid.hashCode());
        result = prime * result + (int) (ib ^ (ib >>> 32));
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
        BRef other = (BRef) obj;
        if (bid == null) {
            if (other.bid != null) {
                return false;
            }
        } else if (!bid.equals(other.bid)) {
            return false;
        }
        if (ib != other.ib) {
            return false;
        }
        return true;
    }
    
    
    
    
    
}
