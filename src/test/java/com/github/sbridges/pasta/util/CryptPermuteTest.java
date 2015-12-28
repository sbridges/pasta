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

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import com.github.sbridges.pasta.util.CryptPermute;

public class CryptPermuteTest {

    @Test
    public void testRoundTripSingle() {
        for(byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b++) {
            byte[] orig = new byte[] {b};
            assertRoundTrip(orig);
        }
    }
    
    @Test
    public void testRoundTripRandom() {
        Random r = new Random();
        byte[] orig = new byte[1025];
        r.nextBytes(orig);
        assertRoundTrip(orig);
    }
    
    @Test
    public void testEncrypt() {
        
        byte[] plain = new byte[] {1,17, Byte.MIN_VALUE, Byte.MAX_VALUE};
        byte[] enc = CryptPermute.encrypt(plain);
        assertArrayEquals(enc, new byte[] {54,-14,-30,-51});
        
    }
    
    
    private void assertRoundTrip(byte[] orig) {
        byte[] dec = CryptPermute.decrypt(orig);
        byte[] enc = CryptPermute.encrypt(dec);
        
        assertArrayEquals(orig, enc);
    }
    

}
