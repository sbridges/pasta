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
import com.github.sbridges.pasta.util.BlockSignature;
import com.github.sbridges.pasta.util.CRC;

/**
 * A PAGETRAILER structure contain s information about 
 * the page in which it is contained. PAGETRAILER structure 
 * is present at the very end of each page in a PST file.
 * 
 */
public class PageTrailer {

    private static final int TRAILER_LENGTH = 16;
    
    
    //This value indicates the type of data contained within 
    //the page. This field MUST contain one of the following values.
    private final PType pType;
    private final int wSig;
    private final long bid;
    
    public PageTrailer(
            PstIo pageSlice, 
            //the position of the page
            long ib) {
      
        PstIo trailerSlice = pageSlice.slice(512 - TRAILER_LENGTH, TRAILER_LENGTH);
        pType = PType.fromCode(trailerSlice.readByte());
        PType ptypeRepeat = PType.fromCode(trailerSlice.readByte());
        if(pType != ptypeRepeat) {
            throw new IllegalStateException("pType:" + pType + " must equals pTypeRepeat:" + ptypeRepeat);
        }
        
        this.wSig = trailerSlice.readW();
        
        int dwCRC = trailerSlice.readDw();
        int computedCrc = CRC.computeCRC(pageSlice.read(0, 512 - TRAILER_LENGTH));
        if(dwCRC != computedCrc) {
            throw new IllegalStateException("invalid crc read:" + dwCRC + " calculated:" + computedCrc);
        }
        
        bid = trailerSlice.readLong();
        
        if(pType.iswSigIsBlockOrPageSignature()) {
            short calculatedBlockSignature = BlockSignature.compute(ib, bid);
            if((calculatedBlockSignature & 0xffff) != wSig) {
                throw new IllegalStateException("invalid wsig, read:" + wSig + " calculated:" + calculatedBlockSignature);
            }
        } else {
            if(wSig != 0) {
                throw new IllegalStateException("invalid wsig:" + wSig + " for pType:" + pType);
            }
        }
    }

    public PType getpType() {
        return pType;
    }

    public int getwSig() {
        return wSig;
    }

    public long getBid() {
        return bid;
    }

    @Override
    public String toString() {
        return "PageTrailer [pType=" + pType + ", bid=" + bid + "]";
    }
    
    
    
    
}
