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

package com.github.sbridges.pasta.io;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;

import com.github.sbridges.pasta.io.InMemoryPstIo;

public class PstIoTest {

    @Test
    public void testReadUUID() {
        byte[] bytes = new byte[] {
                (byte) 0xC9,
                (byte) 0x8B,
                (byte) 0x91,
                (byte) 0x35,
                (byte) 0x6D,
                (byte) 0x19,
                (byte) 0xEA,
                (byte) 0x40,
                (byte) 0x97,
                (byte) 0x79,
                (byte) 0x88,
                (byte) 0x9D,
                (byte) 0x79,
                (byte) 0xB7,
                (byte) 0x53,
                (byte) 0xF0,

        };
        
        InMemoryPstIo io = new InMemoryPstIo(null, bytes);
        
        assertEquals(
                UUID.fromString("35918bc9-196d-40ea-9779-889d79b753f0").toString(),
                io.readUUID().toString());
    }

}
