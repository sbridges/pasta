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
import java.util.Arrays;

import com.github.sbridges.pasta.util.ByteUtils;

public class InMemoryPstIo implements PstIo {

    private final PstIo root;
    //the index in contents the next read will occur at
    private int position;
    private final byte[] contents;
    private boolean closed = false;
    
    public InMemoryPstIo(PstIo root, byte[] contents) {
        this.contents = contents;
        this.root = root;
    }

    @Override
    public void close() throws IOException {
        closed = true;
    }

    @Override
    public void seek(long position) {
        assertNotClosed();
        if(position < 0 || position > contents.length) {
            throw new IllegalArgumentException("invalid positin:" + position + " length:" + contents.length);
        }
        this.position = (int) position; 
        
    }

    @Override
    public byte[] read(int length) {
        assertNotClosed();
        if(position + length > contents.length) {
            throw new IllegalStateException("reading past end");
        }
        byte[] answer = Arrays.copyOfRange(contents, position, position + length);
        position += length;
        return answer;
    }

    @Override
    public byte[] read(long position, int length) {
        assertNotClosed();
        if(position + length > contents.length) {
            throw new IllegalArgumentException("position:" + position + " this.position:" + this.position);
        }
        return Arrays.copyOfRange(contents, (int) position, (int) position + length);
    }
    
    @Override
    public boolean isExhausted() {
        return position == contents.length;
    }

    @Override
    public long getPosition() {
        return position;
    }
    
    @Override
    public long size() {
        return contents.length;
    }
    
    @Override
    public PstIo getRoot() {
        return root;
    }

    private void assertNotClosed() {
        if(closed) {
            throw new IllegalStateException("already closed");
        }
        
    }

    @Override
    public String toString() {
        return "InMemoryPstIo [root=" + root + ", position=" + position + ", size=" + size() 
                + ", contents=" + ByteUtils.bytesToHex(contents) + ", closed="
                + closed + "]";
    }
    
    



    
}
