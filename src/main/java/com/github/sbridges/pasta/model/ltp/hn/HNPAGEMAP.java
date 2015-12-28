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

import java.util.Arrays;

import com.github.sbridges.pasta.io.PstIo;

/**
 * 2.3.1.5 HNPAGEMAP
 * 
 * The HNPAGEMAP record is located at the end of each HN block 
 * immediately before the block trailer. It contains the
 * information about the allocations in the page. 
 * The HNPAGEMAP is located using the ibHnpm field in the HNHDR, 
 * HNPAGEHDR and HNBITMAPHDR records. 
 *
 */
public class HNPAGEMAP {

    //cAlloc (2 bytes): Allocation count. This represents the number of items (allocations) in the HN. 
    private final int cAlloc;
    //cFree (2 bytes): Free count. This represents the number of freed items in the HN.
    private final int cFree;
    
    //rgibAlloc (variable): Allocation table. This contains cAlloc + 1 entries
    //Each entry is a WORD value that is the byte offset to
    //the beginning of the allocation. An extra entry exists at the cAlloc + 1st
    //position to mark the offset of the next available slot. 
    //Therefore, the nth allocation starts at offset rgibAlloc[n] 
    //(from the beginning of the HN header), and its size is calculated 
    //as: rgibAlloc[n + 1] – rgibAlloc[n] bytes.
    private final int[] rgibAlloc;
    
    public HNPAGEMAP(PstIo slice) {
        cAlloc = slice.readW();
        cFree = slice.readW();
        
        rgibAlloc = new int[cAlloc + 1];
        for(int i = 0; i < rgibAlloc.length; i++) {
            rgibAlloc[i] = slice.readW();
        }
        
        // There can be anywhere from 0 to 63 bytes of padding between the HNPAGEMAP and the block trailer
        if(slice.remaining() > 63) {
            throw new IllegalStateException("too much remaining, slice:" + slice);
        }
    }
    
    public PstIo slice(int hidIndex, PstIo blockSlice) {
        //NOTE - hidIndex is 1 based
        int startInclusive = rgibAlloc[hidIndex -1];
        int endExclusive = rgibAlloc[hidIndex ];
        return blockSlice.slice(startInclusive, endExclusive - startInclusive);
    }
    
    public int[] getRgibAlloc() {
        return rgibAlloc;
    }
    
    @Override
    public String toString() {
        return "HNPAGEMAP [cAlloc=" + cAlloc + ", cFree=" + cFree
                + ", rgibAlloc=" + Arrays.toString(rgibAlloc) + "]";
    }
    
    
}
