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
import com.github.sbridges.pasta.model.BRef;

/**
 * 2.2.2.7.7.3 BBTENTRY (Leaf BBT Entry)
 * 
 * BBTENTRY records contain information about blocks and
 * are found in BTPAGES with cLevel equal to 0, with the 
 * ptype of "ptypeBBT". These are the leaf entries of the BBT. 
 * As noted in section 2.2.2.7.7.1, these structures MAY NOT 
 * be tightly packed and the cbEnt field of the BTPAGE SHOULD
 * be used to iterate over the entries.
 *
 */
public class BBTEntry {

    private final BRef bRef;
    //cb (2 bytes): The count of bytes of the raw data contained in 
    //the block referenced by BREF excluding the block trailer and alignment padding, if any.
    private final int cb;
    //cRef (2 bytes): Reference count indicating the count of
    //references to this block. See section 2.2.2.7.7.3.1 regarding how reference counts work.
    private final int cRef;
    
    public BBTEntry(PstIo slice) {
        bRef = new BRef(slice.sliceAndSkip(16));
        cb = slice.readW();
        cRef = slice.readW();
        
        if(cb <= 0 || cRef <= 0) {
            throw new IllegalStateException("invalid:" + this);
        }
        
        int dwPadding = slice.readDw();
        if(dwPadding != 0) {
            throw new IllegalStateException("invalid dwPadding:" + dwPadding);
        }
        
        slice.assertExhausted();
    }

    public BRef getBRef() {
        return bRef;
    }

    public int getCb() {
        return cb;
    }

    public int getcRef() {
        return cRef;
    }
    
    public int getBlockSizeInclusive() {
        //add 16 bits for trailer, then round up to nearest
        //multiple of 64
        return ((int) Math.ceil((cb + 16f) / 64)) * 64; 
        
    }

    @Override
    public String toString() {
        return "BBTEntry [bRef=" + bRef + ", cb=" + cb + ", cRef=" + cRef + "]";
    }
    
        
    
}
