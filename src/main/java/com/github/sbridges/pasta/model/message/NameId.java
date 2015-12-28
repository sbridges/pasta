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

package com.github.sbridges.pasta.model.message;

import com.github.sbridges.pasta.io.PstIo;

/**
 * 2.4.7.1 NAMEID
 * 
 * Each NAMEID record corresponds to a named property. 
 * The contents of the NAMEID record can be interpreted in two ways, 
 * depending on the value of the N bit.
 */
public class NameId {

    //dwPropertyID (4 bytes): If the N field is 1, this 
    //value is the byte offset into the String stream in 
    //which the string name of the property is stored. 
    //If the N field is 0, this value contains the value of numerical name.
    private final long dwPropertyID;
    
    //N (1 bit): Named property identifier type. If this value is 
    //1, the named property identifier is a string. If this value is 0,
    //the named property identifier is a 16-bit numerical value.
    private final byte N;
    
    //wGuid (15 bits): GUID index. If this value is 1 or 2, 
    //the named property's GUID is one of 2 well- known GUIDs.
    //If this value is greater than 2, this value is the index plus 
    //3 into the GUID Stream where the GUID associated with this 
    //named property is located. The following table explains how the wGuid value works.
    private final int wGuid;
    
    //wPropIdx (2 bytes): Property index. This is the
    //ordinal number of the named property, which is
    //used to calculate the NPID of this named property. 
    //The NPID of this named property is calculated by adding 0x8000 to wPropIndex.
    private final int wPropIdx;
    
    //2.4.7.5 Hash Table
    private final int bucketNoMod;
    
    public NameId(PstIo io) {
        dwPropertyID = io.readDw();
        int next = io.readDw();
        
        bucketNoMod = ((int) dwPropertyID) ^ (next & 0xFFFF);
        
        N = (byte) (next & 0x01);
        
        wGuid = 0xFF & (next >>> 1);
        wPropIdx = next >>> 16;
        
        
        io.assertExhausted();
    }

    public long getDwPropertyID() {
        return dwPropertyID;
    }

    public byte getN() {
        return N;
    }

    public int getwGuid() {
        return wGuid;
    }

    public int getwPropIdx() {
        return wPropIdx;
    }
    
    public int getBucketNoMod() {
        return bucketNoMod;
    }

    @Override
    public String toString() {
        return "NameId [dwPropertyID=" + dwPropertyID + ", N=" + N + ", wGuid="
                + wGuid + ", wPropIdx=" + wPropIdx + "]";
    }
    
    

}
