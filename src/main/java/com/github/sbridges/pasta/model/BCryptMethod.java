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

/**
 * bCryptMethod (1 byte): Indicates how the data within the PST file is encoded. MUST be set to one
 * of the pre-defined values described in the following table. 
 */
public enum BCryptMethod {

    NDB_CRYPT_NONE(0), //Data blocks are not encoded.
    NDB_CRYPT_PERMUTE(1), //Encoded with the Permutation algorithm (section 5.1).
    NDB_CRYPT_CYCLIC(2); //Encoded with the Cyclic algorithm (section 5.2).
    
    private final byte code;
    
    private BCryptMethod(int code) {
        this.code = (byte) code;
    }
   
    public static BCryptMethod fromCode(byte code) {
        for(BCryptMethod m : values()) {
            if(m.code == code) {
                return m;
            }
        }
        throw new IllegalStateException("unrecognized code:" + code);
    }
    
    @Override
    public String toString() {
        return name() + "(" + code + ")";
    }
    
}
