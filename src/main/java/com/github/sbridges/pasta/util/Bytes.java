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

package com.github.sbridges.pasta.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

import com.github.sbridges.pasta.io.InMemoryPstIo;
import com.github.sbridges.pasta.io.PstIo;

/**
 * Wrapper around a byte[] that supports equals/hashcode 
 */
public class Bytes {
    
    private final byte[] contents;

    public Bytes(byte[] contents) {
        this.contents = contents;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(contents);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Bytes other = (Bytes) obj;
        if (!Arrays.equals(contents, other.contents)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Bytes[size=" + contents.length + " ," + ByteUtils.bytesToHex(contents) + "]";
    }
    
    public UUID toUUID() {
        if(contents.length != 16) {
            throw new IllegalStateException("wrong size:" + this);
        }
        
        return new UUID(
                ByteUtils.bytesToLong(contents, 0),
                ByteUtils.bytesToLong(contents, 8)
                );
        
        
    }
    
    public PstIo asIo() {
        return new InMemoryPstIo(null, contents);
    }

    public String asString() {
        return new String(contents, StandardCharsets.UTF_16LE);
    }
    
}
