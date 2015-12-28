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

import com.github.sbridges.pasta.io.PstIo;

/**
 * 2.3.1.1 HID
 * A HID is a 4-byte value that identifies an item allocated from the heap.
 *  The value is unique only within the heap itself. 
 *  The following is the structure of a HID.
 */
public class HNHDR {

    
    //ibHnpm (2 bytes): The byte offset to the HN page Map record
    //(section 2.3.1.5), with respect to the beginning of the HNHDR structure.
    private final int ibHnpm;
    
    //bSig (1 byte): Block signature; MUST be set to 0xEC to indicate a HN.
    private final byte bSig;
    
    //bClientSig (1 byte): Client signature. 
    //This value describes the higher-level structure that is implemented on top of the HN. 
    //This value is intended as a hint for a higher-level structure and has 
    //no meaning for structures defined at the HN level. 
    private final BClientSig bClientSig;
    
    //hidUserRoot (4 bytes): HID that points to the User Root record. 
    //The User Root record contains data that is specific to the higher level.
    private final HID hidUserRoot;
    
    //rgbFillLevel (4 bytes): Per-block Fill Level Map. 
    //This array consists of eight 4-bit values that indicate 
    //the fill level for each of the first 8 data blocks
    //(including this header block). If the HN has fewer than 8 data blocks,
    //then the values corresponding to the non-existent data blocks MUST be set to zero (0).
    private final int rgbFillLevel;
    
    public HNHDR(PstIo slice) {
        ibHnpm = slice.readW();
        
        //bSig (1 byte): Block signature; MUST be set to 0xEC to indicate a HN.
        bSig = slice.readByte();
        bClientSig = BClientSig.fromCode(slice.readByte());
        hidUserRoot = new HID(slice.readDw());
        rgbFillLevel = slice.readDw();
        slice.assertExhausted();
        
        if(bSig != (byte) 0xEc) {
            throw new IllegalStateException("bSig != 0xEX, bsig:" + bSig + " this:" + this);
        }
    }

    @Override
    public String toString() {
        return "HNHDR [ibHnpm=" + ibHnpm + ", bClientSig=" + bClientSig
                + ", hidUserRoot=" + hidUserRoot + ", rgbFillLevel="
                + rgbFillLevel + "]";
    }

    public int getIbHnpm() {
        return ibHnpm;
    }

    public BClientSig getbClientSig() {
        return bClientSig;
    }

    public HID getHidUserRoot() {
        return hidUserRoot;
    }

    public int getRgbFillLevel() {
        return rgbFillLevel;
    }
    
    
}
