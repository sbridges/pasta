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

import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.BID;
import com.github.sbridges.pasta.util.CollectionUtils;

/**
 * 2.2.2.8.3.2.1 XBLOCK
 * XBLOCKs are used when the data associated 
 * with a node data that exceeds 8,176 bytes 
 * in size. The XBLOCK expands the data that
 *  is associated with a node by using an 
 *  array of BIDs that reference data blocks
 *   that contain the data stream associated with 
 *   the node. A BLOCKTRAILER is present at the 
 *   end of an XBLOCK, and the end of the 
 *   BLOCKTRAILER MUST be aligned on a 64-byte boundary.
 */
public class XBlock {
    
    private final DataBlock block;
    
    public XBlock(DataBlock block) {
        this.block = block;
    }

    public Iterable<BID> getXBlockBids() {
        PstIo data = block.getData();
        data.seek(0);
        //btype (1 byte): Block type; MUST be set to 0x01 to indicate an XBLOCK or XXBLOCK.
        byte bType = data.readByte();
        //cLevel (1 byte): MUST be set to 0x01 to indicate an XBLOCK.
        byte cLevel = data.readByte();
        //cEnt (2 bytes): The count of BID entries in the XBLOCK.
        int cEnt = data.readW();
        //lcbTotal (4 bytes): Total count of bytes of all the external data stored in the data blocks referenced by XBLOCK.
        long lcbTotal = data.readDw();
        if(bType != 1) {
            throw new IllegalStateException("bType must be 1, not:" + bType + " cLevel:" + cLevel + " cent:" + cEnt + " dataBlock:" + this);
        }
        if(cLevel != 1 && cLevel != 2) {
            throw new IllegalStateException("cLevel must be 1 or 2, not:" + cLevel + " dataBlock:" + this);
        }
        if(lcbTotal <= 0) {
            throw new IllegalStateException("lcbTotal must be > 0, not:" + lcbTotal + " dataBlock:" + this);
        }

        //rgbid (variable): Array of BIDs that reference data blocks. The size is equal to the number of entries indicated by cEnt multiplied by the size of a BID (8 bytes for Unicode PST files, 4 bytes for ANSI PST files).
        return () -> CollectionUtils.transform(
                        data.chunk(8, cEnt, 8),
                        p -> new BID(p.readLong())
                        );
    } 
    
    public BlockTrailer getBlockTrailer() {
        return block.getBlockTrailer();
    }

    public PstIo getData() {
        return block.getData();
    }

    @Override
    public String toString() {
        return "DataBlock [block:" + block + "]";
    }
    
    
}
