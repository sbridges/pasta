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

package com.github.sbridges.pasta.model.ltp.bth;

import java.util.Optional;

import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.ltp.hn.BClientSig;
import com.github.sbridges.pasta.model.ltp.hn.HID;

/**
 * 2.3.2.1 BTHHEADER
 * 
 * The BTHHEADER contains the BTH metadata, which instructs the 
 * reader how to access the other objects of the BTH structure.
 */
public class BTHHEADER {

    //bType (1 byte): MUST be bTypeBTH.
    private final BClientSig bTypeBTH;
    
    //cbKey (1 byte): Size of the BTree Key value, in bytes. This value MUST be set to 2, 4, 8, or 16.
    private final byte cbKey;
    
    
    //cbEnt (1 byte): Size of the data value, in bytes. 
    //This MUST be greater than zero (0) and less than or equal to 32.
    private final byte cbEnt;
    
    //bIdxLevels (1 byte): Index depth. This number indicates
    //how many levels of intermediate indices exist in the BTH. 
    //Note that this number is zero-based, meaning that a value of zero (0)
    //actually means that the BTH has one level of indices. 
    //If this value is greater than zero (0), then its value
    //indicates how many intermediate index levels are present.
    private final byte bIdxLevels;
    
    //hidRoot (4 bytes): This is the HID that points to the BTH entries for this BTHHEADER. 
    //The data consists of an array of BTH Records. This value is set to zero (0) if the BTH is empty.
    private final Optional<HID> hidRoot;
    
    public BTHHEADER(PstIo slice) {
        
        bTypeBTH = BClientSig.fromCode(slice.readByte());
        if(bTypeBTH != BClientSig.bTypeBTH) {
            throw new IllegalStateException("wrong type:" + bTypeBTH + " this:" + this);
        }
        
        cbKey = slice.readByte();
        if(cbKey != 2 && cbKey != 4 && cbKey != 8 && cbKey != 16) {
            throw new IllegalStateException("invalid cbKey:" + cbKey + " this:" + this);
        }
        
        cbEnt = slice.readByte();
        if(cbEnt < 0 || cbEnt > 32) {
            throw new IllegalStateException("invalid cbEnt:" + cbEnt + " this:" + this);
        }
        
        bIdxLevels = slice.readByte();
        
        int hidRootDw = slice.readDw();
        if(hidRootDw != 0) {
            hidRoot = Optional.of(new HID(hidRootDw));
        } else {
            hidRoot = Optional.empty();
        }
        
        slice.assertExhausted();
    }
    
    

    @Override
    public String toString() {
        return "BTHHEADER [bTypeBTH=" + bTypeBTH + ", cbKey=" + cbKey
                + ", cbEnt=" + cbEnt + ", bIdxLevels=" + bIdxLevels
                + ", hidRoot=" + hidRoot + "]";
    }



    public BClientSig getbTypeBTH() {
        return bTypeBTH;
    }

    public byte getCbKey() {
        return cbKey;
    }

    public byte getCbEnt() {
        return cbEnt;
    }

    public int getbIdxLevels() {
        return 0xFF & bIdxLevels;
    }

    public Optional<HID> getHidRoot() {
        return hidRoot;
    }
    
    
}
