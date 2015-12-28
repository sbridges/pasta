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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteUtils {

    
    public static boolean equals(byte[] l, int lOff, byte[] r, int rOff, int len) {
        for(int i =0; i < len; i++) {
            if(l[i + lOff] != r[i + rOff]) {
                return false;
            }
        }
        return true;
    }
    
    //http://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[2 + bytes.length * 2];
        hexChars[0] = '0';
        hexChars[1] = 'X';
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[2 + j * 2] = hexArray[v >>> 4];
            hexChars[2 + j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    public static String byteToHex(byte b) {
        return String.format("0x%02X", b);
    }
    
    public static String shortToHex(short s) {
        return String.format("0x%04X", s);
    }
    
    public static String intToHex(int i) {
        return String.format("0x%08X", i);
    }
    
    public static final byte[] intToBytesLE(int value) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
    }
    
    public static final byte[] shortToBytesLE(short value) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array();
    }
    
    public static final byte[] longToBytesLE(long value) {
        return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(value).array();
    }
    
    public static short bytesToShort(byte b1, byte b2) {
        return (short) ((b2 << 8) | (0xFF & b1));
    }
   
    public static int bytesToInt(byte[] b) {
        return bytesToInt(b[0], b[1], b[2], b[3]);
        
    }
    
    public static int bytesToInt(byte b1, byte b2, byte b3, byte b4) {
        int answer = 0xFF & b4;
        answer = answer << 8 | (0xFF & b3);
        answer = answer << 8 | (0xFF & b2);
        answer = answer << 8 | (0xFF & b1);
        return answer;
        
    }
    
    public static long bytesToLong(byte[] bytes, int index) {
        return bytesToLong(
                bytes[index++],
                bytes[index++],
                bytes[index++],
                bytes[index++],
                bytes[index++],
                bytes[index++],
                bytes[index++],
                bytes[index++]
                );
    }
    
    public static long bytesToLong(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8) {
        long answer = 0xFFL & b8;
        answer = answer << 8 | (0xFF & b7);
        answer = answer << 8 | (0xFF & b6);
        answer = answer << 8 | (0xFF & b5);
        answer = answer << 8 | (0xFF & b4);
        answer = answer << 8 | (0xFF & b3);
        answer = answer << 8 | (0xFF & b2);
        answer = answer << 8 | (0xFF & b1);
        return answer;
        
    }
    

}
