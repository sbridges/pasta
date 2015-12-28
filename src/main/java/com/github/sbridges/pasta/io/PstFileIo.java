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

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Methods to read from a PST file        
 */
public class PstFileIo implements Closeable, PstIo {
    
    private final FileChannel input;
    private final Path path;

    public PstFileIo(Path path) throws IOException {
        this.path = path;
        this.input = FileChannel.open(path, StandardOpenOption.READ);
    }

    @Override
    public void close() throws IOException {
        input.close();
    }
    
    @Override
    public void seek(long position) {
        try {
            input.position(position);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public byte[] read(int length) {
        byte[] answer = new byte[length];
        ByteBuffer b = ByteBuffer.wrap(answer);
        try {
            int remaining = length;
            while(remaining > 0) {
                int read = input.read(b);
                if(read == -1) {
                    throw new EOFException();
                }
                if(read == 0) {
                    throw new IOException("no bytes read");
                }
                remaining -= read;
            }
            return answer;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public byte[] read(long position, int length) {
        byte[] answer = new byte[length];
        ByteBuffer b = ByteBuffer.wrap(answer);
        try {
            int remaining = length;
            while(remaining > 0) {
                int read = input.read(b, position + length - remaining);
                if(read == -1) {
                    throw new EOFException();
                }
                if(read == 0) {
                    throw new IOException("no bytes read");
                }
                remaining -= read;
            }
            return answer;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean isExhausted() {
        try {
            return input.size() == input.position();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public long getPosition() {
        try {
            return input.position();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public long size() {
        try {
            return input.size();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public PstIo getRoot() {
        return this;
    }

    @Override
    public String toString() {
        return "PstFileIo [path=" + path + "]";
    }

    
    
}
