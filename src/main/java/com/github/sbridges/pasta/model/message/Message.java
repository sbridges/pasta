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

import com.github.sbridges.pasta.PstReader;
import com.github.sbridges.pasta.model.BID;
import com.github.sbridges.pasta.model.ltp.pc.PC;
import com.github.sbridges.pasta.model.ltp.pc.Property;
import com.github.sbridges.pasta.model.ndb.NID;
import com.github.sbridges.pasta.model.ndb.NidType;

/**
 * 2.4.5 Message Objects
 * 
 * A Message object is a composite structure
 * , but unlike a Folder object, all the data 
 * of a Message object is contained in a single 
 * top-level node (that is, accessed through a 
 * single top-level NID). Both the data block and 
 * subnode are used in a Message object node, 
 * where the data block contains a PC structure 
 * that contains the immediate properties of 
 * the Message object, and the subnode contains a 
 * number of composite structures that contain information 
 * such as the Recipient List and Attachment objects, if any.
 *
 */
public class Message {
    
    //The presence of Attachment objects is indicated by the MSGFLAG_HASATTACH (0x10) bit set to "1" in PidTagMessageFlags.
    private static final int MSGFLAG_HASATTACH = 0x10;
    
    private final PstReader reader;
    private final NID nid;
    private final PC pc;
    
    public Message(PstReader reader, NID nid) {
        this.reader = reader;
        this.nid = nid;
        
        if(nid.getType() != NidType.NID_TYPE_NORMAL_MESSAGE) {
            throw new IllegalStateException("invalid nid:" + nid);
        }
        
        
        //
        //A Message object keeps track of its Attachment 
        //objects using an optional Attachment Table 
        //in its subnode. The Attachment Table is said to
        //be optional because it only exists if a Message 
        //object has at least one Attachment object.
        //The presence of Attachment objects is indicated in 
        //PidTagMessageFlags property in the Message object.
        //The presence of Attachment objects is indicated by
        //the MSGFLAG_HASATTACH (0x10) bit set to "1" in PidTagMessageFlags. 
        //If Attachment objects are present, then the 
        //Attachment Table can be accessed by scanning the 
        //subnode BTree of the Message object subnode to 
        //locate a subnode whose NID is NID_ATTACHMENT_TABLE. 
        //Each Message object MUST have at most one Attachment Table.

        pc = new PC(reader, nid);
        if((pc.load(Property.PidTagMessageFlags) & MSGFLAG_HASATTACH) != 0) {
            BID bidSub = reader.getNBT().load(nid).get().getBidSub().orElseGet(() -> {
                throw new IllegalStateException("no bid sib? :" + nid);
                
            });
            
            //TODO load attachment table
            
        }
    }
    
    
    public PC getPc() {
        return pc;
    }
    
}
