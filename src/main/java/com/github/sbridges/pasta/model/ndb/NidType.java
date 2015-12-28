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

import java.util.HashMap;
import java.util.Map;

import com.github.sbridges.pasta.util.ByteUtils;

public enum NidType {
    NID_TYPE_HID(0x00), //Heap node
    NID_TYPE_INTERNAL(0x01), //Internal node (section 2.4.1)
    NID_TYPE_NORMAL_FOLDER(0x02), //Normal Folder object (PC)
    NID_TYPE_SEARCH_FOLDER(0x03), //Search Folder object (PC)
    NID_TYPE_NORMAL_MESSAGE(0x04), //Normal Message object (PC)
    NID_TYPE_ATTACHMENT(0x05), //Attachment object (PC)
    NID_TYPE_SEARCH_UPDATE_QUEUE(0x06), //Queue of changed objects for search Folder objects
    NID_TYPE_SEARCH_CRITERIA_OBJECT(0x07), //Defines the search criteria for a search Folder object
    NID_TYPE_ASSOC_MESSAGE(0x08), //Folder associated information (FAI) Message object (PC)
    NID_TYPE_CONTENTS_TABLE_INDEX(0x0A), //Internal, persisted view-related
    NID_TYPE_RECEIVE_FOLDER_TABLE(0X0B), //Receive Folder object (Inbox)
    NID_TYPE_OUTGOING_QUEUE_TABLE(0x0C), //Outbound queue (Outbox)
    NID_TYPE_HIERARCHY_TABLE(0x0D), //Hierarchy table (TC)
    NID_TYPE_CONTENTS_TABLE(0x0E), //Contents table (TC)
    NID_TYPE_ASSOC_CONTENTS_TABLE(0x0F), //FAI contents table (TC)
    NID_TYPE_SEARCH_CONTENTS_TABLE(0x10), //Contents table (TC) of a search Folder object
    NID_TYPE_ATTACHMENT_TABLE(0x11), //Attachment table (TC)
    NID_TYPE_RECIPIENT_TABLE(0x12), //Recipient table (TC)
    NID_TYPE_SEARCH_TABLE_INDEX(0x13), //Internal, persisted view-related
    NID_TYPE_LTP(0x1F); //LTP

    private static final Map<Integer, NidType> lookup = new HashMap<>();
    private final int code;
    
    static NidType fromCode(int code) {
        NidType answer = lookup.get(code);
        if(answer == null) {
            throw new IllegalStateException("invalid code:" +  ByteUtils.intToHex(code));
        }
        return answer;
    }
    
    NidType(int code) {
        this.code = code;
    }
    
    static {
        for(NidType t : values()) {
            lookup.merge(
                    t.getCode(), t, 
                    (a, b) -> {
                            throw new IllegalStateException("duplicate code:" + t.code);
                        });
        }
    }
    
    public int getCode() {
        return code;
    }
}
