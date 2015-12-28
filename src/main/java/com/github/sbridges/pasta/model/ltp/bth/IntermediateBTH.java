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

import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.ltp.hn.HID;
import com.github.sbridges.pasta.util.ByteUtils;

/**
 * 2.3.2.2 Intermediate BTH (Index) Records
 * 
 * Index records do not contain actual data, but point
 *  to other index records or leaf records. The format 
 *  of the intermediate index record is as follows. 
 *  The number of index records can be determined based on the size of the heap allocation. 
 *
 */
public class IntermediateBTH {

    
    //key (variable): This is the key of the first record 
    //in the next level index record array.
    //The size of the key is specified in the cbKey field in 
    //the corresponding BTHHEADER structure (section 2.3.2.1).
    //The size and contents of the key are specific to the 
    //higher level structure that implements this BTH.
    private final byte[] key;
    
    //hidNextLevel (4 bytes): HID of the next level index record array. 
    //This contains the HID of the heap item that contains the next level index record array.
    private final HID hid;
    
    public IntermediateBTH(PstIo slice, int cbKey) {
        key = slice.read(cbKey);
        hid = new HID(slice.readDw());
        
        slice.assertExhausted();
    }

    public byte[] getKey() {
        return key;
    }

    public HID getHid() {
        return hid;
    }

    @Override
    public String toString() {
        return "IntermediateBTH [key=" + ByteUtils.bytesToHex(key) + ", hid=" + hid
                + "]";
    }
    
    
}
