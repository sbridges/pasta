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
import com.github.sbridges.pasta.model.BRef;
import com.github.sbridges.pasta.util.BlockSignature;

/**
 * 
 * 2.2.2.8.1 BLOCKTRAILER
 *
 */
public class BlockTrailer {

    public static final int SIZE = 16;

    //cb (2 bytes): The amount of data, in bytes, 
    //contained within the data section of the block. 
    //This value does not include the block trailer 
    //or any unused bytes that can exist after the end of 
    //the data and before the start of the block trailer.
    private final int cb;
    
    
    //wSig (2 bytes): Block signature. See section 5.5 for 
    //the algorithm to calculate the block signature.
    private final int wSig;
    
    
    //dwCRC (4 bytes): 32-bit CRC of the cb bytes of raw data, 
    //see section 5.3 for the algorithm to calculate the CRC. 
    //Note the locations of the dwCRC and bid are differs 
    //between the Unicode and ANSI version of this structure.
    private final int dwCRC;
    
    
    //bid (Unicode: 8 bytes; ANSI 4 bytes): The BID (section 2.2.2.2) of the 
    //data block.
    private final BID bid;

    
    public BlockTrailer(BRef bref, int blockCrc, PstIo slice) {
        this.cb = slice.readW();
        this.wSig = slice.readW();
        this.dwCRC = slice.readDw();
        this.bid = new BID(slice.readLong());
        
        if(cb < 0) {
            throw new IllegalStateException("invalid cb:" + this);
        }
        if(bid.getBid() < 0) {
            throw new IllegalStateException("invalid bid:" + bid);
        }
        if(wSig != (0x00FFFF & BlockSignature.compute(bref.getIb(), bid.getBid()))) {
            throw new IllegalStateException("invalid wsig:" + this);
        }
        if(dwCRC != blockCrc) {
            throw new IllegalStateException("invalid crc:" + blockCrc);
        }
        
        if(!bid.equals(bref.getBid())) {
            throw new IllegalStateException();
        }
        
        
        
    }

    @Override
    public String toString() {
        return "BlockTrailer [cb=" + cb + ", wSig=" + wSig + ", dwCRC=" + dwCRC
                + ", bid=" + bid + "]";
    }

    public int getCb() {
        return cb;
    }

    public int getwSig() {
        return wSig;
    }

    public int getDwCRC() {
        return dwCRC;
    }

    public BID getBid() {
        return bid;
    }
    
    
    
}
