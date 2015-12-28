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
import com.github.sbridges.pasta.util.CollectionUtils;

/**
 * 2.2.2.8.3.3.2 SIBLOCKs
 * An SIBLOCK is a block that contains an array 
 * of SIENTRYs. It is used to extend the number 
 * of subnodes that a node can reference by chaining SLBLOCKS.
 */
public class SIBlock {

    private final DataBlock block;
    
    public SIBlock(DataBlock block) {
        this.block = block;
    }
       
    public Iterable<SIEntry> getSLEntries() {
        PstIo data = block.getData();
        data.seek(0);
        //btype (1 byte): Block type; MUST be set to 0x02.
        byte bType = data.readByte();
        //cLevel (1 byte): MUST be set to 0x01.
        byte cLevel = data.readByte();
        //cEnt (2 bytes): The number of SLENTRYs in the SLBLOCK. This value and the number of elements in the rgentries array MUST be non-zero. When this value transitions to zero, it is required for the block to be deleted.
        int cEnt = data.readW();
        //dwPadding (4 bytes): Padding; MUST be set to zero.
        long dwPadding = data.readDw();
        if(bType != 2) {
            throw new IllegalStateException("bType must be 2, not:" + bType + " cLevel:" + cLevel + " cent:" + cEnt + " dataBlock:" + this);
        }
        if(cLevel != 1) {
            throw new IllegalStateException("cLevel must be 1, not:" + cLevel + " dataBlock:" + this);
        }
        if(dwPadding != 0) {
            throw new IllegalStateException("dwPadding must be 0, not:" + dwPadding + " dataBlock:" + this);
        }
        
        //rgbid (variable): Array of BIDs that reference data blocks. The size is equal to the number of entries indicated by cEnt multiplied by the size of a BID (8 bytes for Unicode PST files, 4 bytes for ANSI PST files).
        return () -> CollectionUtils.transform(
                        data.chunk(8, cEnt, 24),
                        p -> new SIEntry(p)
                        );
    }
}
