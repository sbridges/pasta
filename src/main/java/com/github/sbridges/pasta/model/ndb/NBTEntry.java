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

import java.util.Optional;

import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.BID;

/**
 * 2.2.2.7.7.4 NBTENTRY (Leaf NBT Entry)
 * NBTENTRY records contain information about nodes 
 * and are found in BTPAGES with cLevel equal to 0,
 * with the ptype of ptypeNBT. These are the leaf entries 
 * of the NBT.
 *
 */
public class NBTEntry {

    //nid (Unicode: 8 bytes; ANSI: 4 bytes): 
    //The NID (section 2.2.2.1) of the entry. 
    //Note that the NID is a 4-byte value for both
    //Unicode and ANSI formats. However, to stay 
    //consistent with the size of the btkey member in 
    //BTENTRY, the 4-byte NID is extended to its 8-byte 
    //equivalent for Unicode PST files.
    private final NID nid;
    
    //bidData (Unicode: 8 bytes; ANSI: 4 bytes): The BID of the data block for this node.
    private final BID bidData;
    
    //bidSub (Unicode: 8 bytes; ANSI: 4 bytes): The BID of the subnode block for this node. 
    //If this value is zero, a subnode block does not exist for this node.
    private final BID bidSub;
    
    //nidParent (4 bytes): If this node represents a child of a 
    //Folder object defined in the Messaging Layer, then this value 
    //is nonzero and contains the NID of the parent Folder object's node. 
    //Otherwise, this value is zero. See section 2.2.2.7.7.4.1 for more information.
    //This field is not interpreted by any structure defined at the NDB Layer.
    private final Optional<NID> nidParent;
    
    public NBTEntry(PstIo slice) {
        
        nid = new NID(slice.readLong());
        bidData = new BID(slice.readLong());
        long bidSubVal = slice.readLong();
        if(bidSubVal == 0) {
            bidSub = null;
        } else {
            bidSub = new BID(bidSubVal); 
        }
        int nidParentInt = slice.readDw();
        if(nidParentInt == 0) {
            nidParent = Optional.empty();
        } else {
            nidParent = Optional.of(new NID(nidParentInt));
        }
        
        if(nid.getNid() <= 0 || bidData.getBid() < 0 || bidSubVal < 0 || nidParentInt < 0) {
            throw new IllegalStateException("invalid:" + this);
        }
        
        int dwPadding = slice.readDw();
        if(dwPadding != 0) {
            throw new IllegalStateException("invalid dwPadding:" + dwPadding);
        }
        
        slice.assertExhausted();
    }

    public NID getNid() {
        return nid;
    }

    public BID getBidData() {
        return bidData;
    }

    public Optional<BID> getBidSub() {
        return Optional.ofNullable(bidSub);
    }

    public Optional<NID> getNidParent() {
        return nidParent;
    }

    @Override
    public String toString() {
        return "NBTEntry [nid=" + nid + ", bidData=" + bidData + ", bidSub=" + bidSub
                + ", nidParent=" + nidParent + "]";
    }
    
    
    
}
