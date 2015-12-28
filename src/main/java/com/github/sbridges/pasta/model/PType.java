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

import java.util.HashMap;
import java.util.Map;

import com.github.sbridges.pasta.util.ByteUtils;

public enum PType {
    ptypeBBT((byte) 0x80, "ptypeBBT", true), //Block BTree page.  
    ptypeNBT((byte) 0x81, "ptypeNBT", true), //Node BTree page. 
    ptypeFMap((byte) 0x82, "ptypeFMap", false), //Free Map page. 
    ptypePMap((byte) 0x83, "ptypePMap", false), //Allocation Page Map page.
    ptypeAMap((byte) 0x84, "ptypeAMap", false), //Allocation Map page.  
    ptypeFPMap((byte) 0x85, "ptypeFPMap", false), //Free Page Map page. 
    ptypeDL((byte) 0x86, "ptypeDL", true); //Density List page.  

    private final byte code;
    private final String name;
    private final boolean wSigIsBlockOrPageSignature;
    
    private static final Map<Byte, PType> lookup = new HashMap<>();
    static {
        for(PType p : PType.values()) {
            lookup.merge(p.code, p, (a,b) -> {throw new IllegalStateException();});
        }
    }
    
    public static PType fromCode(byte code) {
        PType answer = lookup.get(code);
        if(answer == null) {
            throw new IllegalStateException("invalid code:" + ByteUtils.byteToHex(code));
        }
        return answer;
    }
    
    PType(byte code, String name, boolean wSigIsBlockOrPageSignature) {
        this.code = code;
        this.name = name;
        this.wSigIsBlockOrPageSignature = wSigIsBlockOrPageSignature;
    }

    public byte getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public boolean iswSigIsBlockOrPageSignature() {
        return wSigIsBlockOrPageSignature;
    }
    
    @Override
    public String toString() {
        return name() + "(" + ByteUtils.byteToHex(code) + ")";
    }
}
