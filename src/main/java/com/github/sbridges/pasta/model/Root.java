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
import com.github.sbridges.pasta.model.ndb.BBT;
import com.github.sbridges.pasta.model.ndb.NBT;

/**
 * 2.2.2.5 ROOT
 * 
 * The ROOT structure contains current file state.
 */
public class Root {

    
    private final long ibFileEof;
    private final long ibAMapLast;
    private final long cbAMapFree;
    private final NBT nbt;
    private final BBT bbt;
    
    public long getIbFileEof() {
        return ibFileEof;
    }

    public long getIbAMapLast() {
        return ibAMapLast;
    }

    public long getCbAMapFree() {
        return cbAMapFree;
    }

    public NBT getNBT() {
        return nbt;
    }

    public BBT getBBT() {
        return bbt;
    }

    public Root(BCryptMethod bCryptMethod, PstIo slice, long fileSize) {
        
        //dwReserved (4 bytes): Implementations SHOULD ignore this value and SHOULD NOT modify it. 
        //Creators of a new PST file MUST initialize this value to zero
        int dwReserved = slice.readDw();
        if(dwReserved != 0) {
            throw new IllegalStateException("dwReserved should be 0, not:" + dwReserved);
        }
        
        //ibFileEof (Unicode: 8 bytes; ANSI 4 bytes): The size of the PST file, in bytes.
        //TODO - when writing we need to update this
        ibFileEof = slice.readLong();
        if(ibFileEof != fileSize) {
            throw new IllegalStateException("wrong ibFileEof, expected:" + fileSize + " read:" + ibFileEof);
        }
        
        //ibAMapLast (Unicode: 8 bytes; ANSI 4 bytes): An IB structure (section 2.2.2.3) that contains the
        //absolute file offset to the last AMap page of the PST file.
        //TODO - when writing we need to update this
        ibAMapLast = slice.readLong();
        
        //cbAMapFree (Unicode: 8 bytes; ANSI 4 bytes): The total free space in all AMaps, combined.
        //TODO - when writing we need to update this
        cbAMapFree = slice.readLong();
        
        //cbPMapFree (Unicode: 8 bytes; ANSI 4 bytes): The total free space in all PMaps, combined. 
        //Because the PMap is deprecated, this value SHOULD be zero. 
        //Creators of new PST files MUST initialize this value to zero.
        long cbPMapFree = slice.readLong();
        if(cbPMapFree != 0) {
            throw new IllegalStateException("cbPMapFree should be 0, not:" + cbPMapFree);
        }
        
        //BREFNBT (Unicode: 16 bytes; ANSI: 8 bytes): A BREF structure (section 2.2.2.4) 
        //that references the root page of the Node BTree (NBT).
        BRef BREFNBT = new BRef(slice.sliceAndSkip(16));
        nbt = new NBT(BREFNBT, slice.getRoot());
        
        //BREFBBT (Unicode: 16 bytes; ANSI: 8 bytes): A BREF structure that references the root page of the Block BTree (BBT).
        BRef BREFBBT = new BRef(slice.sliceAndSkip(16));
        bbt = new BBT(bCryptMethod, BREFBBT, slice.getRoot());

        int fAMapValid = slice.readByte();
        if(fAMapValid != 0 && fAMapValid != 1 && fAMapValid != 2) {
            throw new IllegalStateException("invalid fAMapValid:" + fAMapValid);
        }
        
        int bReserved = slice.readByte();
        if(bReserved != 0) {
            throw new IllegalStateException("invalid bReserved:" + bReserved);
        }
        
        int wReserved = slice.readW();
        if(wReserved != 0) {
            throw new IllegalStateException("invalid wReserved:" + wReserved);
        }
        
        slice.assertExhausted();
        
        
        
    }
}
