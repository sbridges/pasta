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

package com.github.sbridges.pasta.model.message;

import java.util.UUID;

import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.ndb.NID;

/**
 * (2.4.3.2 Mapping between EntryID and NID
 * Objects in the message store are accessed externally 
 * using EntryIDs ([MS-OXCDATA] section 2.2), where 
 * within the PST, objects are accessed using their 
 * respective NIDs. The following explains the layout of
 * the ENTRYID structure, which is used to map between 
 * an NID and its EntryID:
 */
public class EntryId {
    //rgbFlags (4 bytes): Flags; each of these 
    //bytes MUST be initialized to zero.
    private int rgbFlags;
    
    //uid (16 bytes): The provider UID of this PST,
    //which is the value of the PidTagRecordKey property
    //in the message store. If this property does not exist, 
    //the PST client MAY generate a new unique ID, or reject the PST as invalid.
    private UUID uid;
    
    //nid (4 bytes): This is the corresponding NID of the
    //underlying node that represents the object.
    private NID nid;
    
    public EntryId(PstIo io) {
        rgbFlags = io.readDw();
        uid = new UUID(io.readLong(), io.readLong());
        nid = new NID(0xFFFFFFFFL & io.readDw());
        
        if(rgbFlags != 0) {
            throw new IllegalStateException("rgbFlags not 0, this:" + this);
        }
    }

    public int getRgbFlags() {
        return rgbFlags;
    }

    public void setRgbFlags(int rgbFlags) {
        this.rgbFlags = rgbFlags;
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public NID getNid() {
        return nid;
    }

    public void setNid(NID nid) {
        this.nid = nid;
    }

    @Override
    public String toString() {
        return "EntryId [rgbFlags=" + rgbFlags + ", uid=" + uid + ", nid=" + nid
                + "]";
    }
    
    
}
