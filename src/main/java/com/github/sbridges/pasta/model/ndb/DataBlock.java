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

package com.github.sbridges.pasta.model.ndb;

import com.github.sbridges.pasta.io.InMemoryPstIo;
import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.BCryptMethod;
import com.github.sbridges.pasta.model.BRef;
import com.github.sbridges.pasta.util.CRC;
import com.github.sbridges.pasta.util.CryptPermute;

/**
 * 2.2.2.8.3.1 Data Blocks
 * A data block is a block that is "External" 
 * (that is, not marked "Internal") and contains data 
 * streamed from higher layer structures. 
 * The data contained in data blocks have no meaning to 
 * the structures defined at the NDB Layer.
 */
public class DataBlock {
    private final BCryptMethod bCryptMethod;
    private final BlockTrailer blockTrailer;
    private final PstIo data;
    
    public DataBlock(BCryptMethod bCryptMethod, BRef bref, int cb, PstIo slice) {
        this.bCryptMethod = bCryptMethod;
        data = slice.slice(0, cb);
        int dataCrc = CRC.computeCRC(data.read(cb));
        
        slice.seek(slice.size() - 16);
        blockTrailer = new BlockTrailer(bref, dataCrc, slice.sliceAndSkip(16));
        
        if(blockTrailer.getCb() != cb) {
            throw new IllegalStateException("trailer size does not match");
        }
    }

    public BlockTrailer getBlockTrailer() {
        return blockTrailer;
    }

    public PstIo getData() {
        return data;
    }
    
    /**
     * Get a decrypted version of this nodes data.<P>
     */
    public PstIo getDataDecrypted() {
        data.seek(0);
        if(bCryptMethod == BCryptMethod.NDB_CRYPT_NONE) {
            return data;
        }
        else if(bCryptMethod == BCryptMethod.NDB_CRYPT_PERMUTE) {
            if(data.size() > Integer.MAX_VALUE) {
                throw new IllegalStateException();
            }
            byte[] encrypted = data.read((int) data.size());
            byte[] decrypted = CryptPermute.decrypt(encrypted);
            return new InMemoryPstIo(data.getRoot(), decrypted);
        } else {
            throw new IllegalStateException("unsupported crypt method:" + bCryptMethod);
        }
        
    }
    
    public boolean isSLBLock() {
        if(!blockTrailer.getBid().isInternal()) {
            return false;
        }
        data.seek(0);
        return 
                //bType
                data.readByte() == 2 && 
                //cLevel
                data.readByte() == 0;
                
    }
    
    public boolean isSIBLock() {
        if(!blockTrailer.getBid().isInternal()) {
            return false;
        }
        data.seek(0);
        return 
                //bType
                data.readByte() == 2 && 
                //cLevel
                data.readByte() == 1;
                
    }
    
    public boolean isXBLock() {
        if(!blockTrailer.getBid().isInternal()) {
            return false;
        }
        data.seek(0);
        return 
                //bType
                data.readByte() == 1 && 
                //cLevel
                data.readByte() == 1;
                
    }
    
    public boolean isXXBLock() {
        if(!blockTrailer.getBid().isInternal()) {
            return false;
        }
        data.seek(0);
        return 
                //bType
                data.readByte() == 1 && 
                //cLevel
                data.readByte() == 2;
                
    }


    @Override
    public String toString() {
        return "Block [blockTrailer=" + blockTrailer + ", data=" + data + "]";
    }
}
