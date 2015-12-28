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

import java.util.HashMap;
import java.util.Map;

import com.github.sbridges.pasta.util.ByteUtils;

/**
 * bClientSig (1 byte): Client signature. 
 * This value describes the higher-level structure that is implemented on top of the HN. 
 * This value is intended as a hint for a higher-level structure and has 
 * no meaning for structures defined at the HN level.  
 * 
 * All other values not described in the following table are reserved and MUST NOT be assigned or used.
 */
public enum BClientSig {
    bTypeReserved1((byte) 0x6C, "Reserved"),
    bTypeTC((byte) 0x7C, "Table Context (TC/HN)"),
    bTypeReserved2((byte) 0x8C, "Reserved"),
    bTypeReserved3((byte) 0x9C, "Reserved"),
    bTypeReserved4((byte) 0xA5, "Reserved"),
    bTypeReserved5((byte) 0xAC, "Reserved"),
    bTypeBTH((byte) 0xB5, "BTree-on-Heap (BTH)"),
    bTypePC((byte) 0xBC, "Property Context (PC/BTH)"),
    bTypeReserved6((byte) 0xCC, "Reserved");

    private final byte code;
    private final String description;
    
    private static final Map<Byte, BClientSig> lookup = new HashMap<>();
    static {
        for(BClientSig sig : BClientSig.values()) {
            lookup.put(sig.code, sig);
        }
    }
    
    public static BClientSig fromCode(byte code) {
        BClientSig answer = lookup.get(code);
        if(answer == null) {
            throw new IllegalStateException("invalid code:" + ByteUtils.byteToHex((byte) 0));
        }
        if(answer.description.equals("Reserved")) {
            throw new IllegalStateException("Trying to use reserved BClientSig:" + answer);
        }
        return answer;
    }
    
    
    private BClientSig(byte code, String description) {
        this.code = code;
        this.description = description;
    }
    
    

}
