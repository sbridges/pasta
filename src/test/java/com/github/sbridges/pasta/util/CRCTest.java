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

import org.junit.Test;

import com.github.sbridges.pasta.util.CRC;

public class CRCTest {

    //Tested against a c version compiled with clang
    
    @Test
    public void testExample() {
    
        byte[] data = new byte[] {
                83,77,23,0,19,0,1,1,0,0,0,0,0,0,0,0,4,0,0,0,1,0,0,0,-4,44,4,0,0,0,0,0,-101,
                4,0,0,0,4,0,0,0,4,0,0,9,4,0,0,0,64,0,0,-118,4,1,0,9,4,0,0,0,4,0,0,0,4,0,0,0,
                -128,0,0,0,4,0,0,0,4,0,0,0,4,0,0,0,4,0,0,9,4,0,0,9,4,0,0,9,4,0,0,0,4,0,0,0,4,
                0,0,0,4,0,0,0,4,0,0,0,4,0,0,0,4,0,0,0,4,0,0,0,4,0,0,0,4,0,0,0,4,0,0,0,4,0,0,0,4
                ,0,0,0,4,0,0,0,4,0,0,0,4,0,0,39,22,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-28,26,2,0,0
                ,0,0,0,4,23,2,0,0,0,0,0,-126,28,0,0,0,0,0,0,0,0,0,0,0,0,0,-30,44,4,0,0,0,0,
                0,0,22,25,2,0,0,0,0,-6,44,4,0,0,0,0,0,0,44,25,2,0,0,0,0,2,0,0,0,0,0,0,0,-1,
                -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1
                ,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1
                ,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
                -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1
                ,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
                -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1
                ,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
                -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
        
        assertEquals(177113382,
                CRC.computeCRC(data));
    }
    
    @Test
    public void testExampleRemoveFirstByte() {
    
        byte[] data = new byte[] {
                77,23,0,19,0,1,1,0,0,0,0,0,0,0,0,4,0,0,0,1,0,0,0,-4,44,4,0,0,0,0,0,-101,
                4,0,0,0,4,0,0,0,4,0,0,9,4,0,0,0,64,0,0,-118,4,1,0,9,4,0,0,0,4,0,0,0,4,0,0,0,
                -128,0,0,0,4,0,0,0,4,0,0,0,4,0,0,0,4,0,0,9,4,0,0,9,4,0,0,9,4,0,0,0,4,0,0,0,4,
                0,0,0,4,0,0,0,4,0,0,0,4,0,0,0,4,0,0,0,4,0,0,0,4,0,0,0,4,0,0,0,4,0,0,0,4,0,0,0,4
                ,0,0,0,4,0,0,0,4,0,0,0,4,0,0,39,22,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-28,26,2,0,0
                ,0,0,0,4,23,2,0,0,0,0,0,-126,28,0,0,0,0,0,0,0,0,0,0,0,0,0,-30,44,4,0,0,0,0,
                0,0,22,25,2,0,0,0,0,-6,44,4,0,0,0,0,0,0,44,25,2,0,0,0,0,2,0,0,0,0,0,0,0,-1,
                -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1
                ,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1
                ,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
                -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1
                ,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
                -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1
                ,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
                -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
        
        assertEquals(-797569306,
                CRC.computeCRC(data));
    }
    
    @Test
    public void testExampleSingleByte() {
    
        byte[] data = new byte[] {77};
        
        assertEquals(141376813,
                CRC.computeCRC(data));
    }

    @Test
    public void testExampleFiveBytes() {
    
        byte[] data = new byte[] {
                77,23,0,19,0};
        
        assertEquals(205697612,
                CRC.computeCRC(data));
    }
    
    @Test
    public void testExample9Bytes() {
    
        byte[] data = new byte[] {
                77,23,0,19,0,1,1,0,0};
        
        assertEquals(1704960261,
                CRC.computeCRC(data));
    }
    

}
