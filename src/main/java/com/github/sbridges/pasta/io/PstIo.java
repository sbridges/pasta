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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.UUID;

import com.github.sbridges.pasta.util.ByteUtils;

public interface PstIo {

    /**
     * If this is a slice, get the io that all slice's have been created from
     */
    PstIo getRoot();
    
    void close() throws IOException;

    void seek(long position);

    byte[] read(int length);

    byte[] read(long position, int length);
    
    boolean isExhausted();
    
    default long remaining() {
        return size() - getPosition();
    }
    
    default void assertExhausted() {
        if(!isExhausted()) {
            throw new IllegalStateException("not exhausted:" + size());
        }
    }
    
    long getPosition();
    
    
    default Iterator<PstIo> chunkAll(int chunkSize) {
        if(size() % chunkSize != 0) {
            throw new IllegalStateException("not an integral number of chunks, chunkSize:" + chunkSize + " size:" + size());
        }
        
        return chunk(0, ((int) size()) / chunkSize, chunkSize);
    }
    
    default Iterator<PstIo> chunk(int position, int chunkCount, int chunkSize) {
        return new Iterator<PstIo>() {
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                return index < chunkCount;
            }

            @Override
            public PstIo next() {
                PstIo answer = slice(position + (index * chunkSize),  chunkSize);
                index++;
                return answer;
            }
        };
    }
    
    default long readLong() {
        byte[] bytes = read(8);
        return ByteUtils.bytesToLong(bytes, 0);
    }
    
    
    default void skip(int count) {
        seek(count + getPosition());
    }

    /**
     * this returns an int, but this is signed 
     */
    default int readDw() {
        //TODO - something more efficient
        byte[] bytes = read(4);
        int answer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
        return answer;
    }
    
    /**
     * this returns a short, but since shorts are signed in java, return an int 
     */
    default int readW() {
        byte[] bytes = read(2);
        short signed = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
        return signed & 0x00FFFF;
    }
    
    default byte readByte(long position) {
        return read(position, 1)[0];
    }
    
    default byte readByte() {
        return read(1)[0];
    }
    
    default public PstIo slice(long position, int length) {
        return new InMemoryPstIo(getRoot(), read(position, length));
    }
    
    default public PstIo sliceAndSkip(int length) {
        InMemoryPstIo answer = new InMemoryPstIo(getRoot(), read(getPosition(), length));
        skip(length);
        return answer;
    }

    long size();

    default String debugString() {
        return "size:" + size() + " contents:" + ByteUtils.bytesToHex(read(0, (int) size()));
    }

    default UUID readUUID() {
        
        //seems like an odd encoding for a uuid
        //the endiness changes half way through
        //https://msdn.microsoft.com/en-us/library/system.guid.tobytearray.aspx
        
        byte[] bytes = read(16);
        
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        long msb = 0;
        msb |= ((long) bb.getInt()) << 32;
        msb |= ((long) bb.getShort()) << 16;
        msb |= bb.getShort();
        
        bb.order(ByteOrder.BIG_ENDIAN);
        long lsb = bb.getLong();
   
        return new UUID(msb, lsb);
        
    }
    

}