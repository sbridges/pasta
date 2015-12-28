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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.github.sbridges.pasta.model.ndb.NID;

public class InternalNids {
    //Message store node (section 2.4.3).
    public static final NID NID_MESSAGE_STORE = new NID(0x21);
    //Named Properties Map (section 2.4.7).
    public static final NID NID_NAME_TO_ID_MAP = new NID(0x61);
    //Special template node for an empty Folder object.
    public static final NID NID_NORMAL_FOLDER_TEMPLATE = new NID(0xA1);
    //Special template node for an empty search Folder object.
    public static final NID NID_SEARCH_FOLDER_TEMPLATE = new NID(0xC1);
    //Root Mailbox Folder object of PST.
    public static final NID NID_ROOT_FOLDER = new NID(0x122);
    //Queue of Pending Search-related updates.
    public static final NID NID_SEARCH_MANAGEMENT_QUEUE = new NID(0x1E1);
    //Folder object NIDs with active Search activity.
    public static final NID NID_SEARCH_ACTIVITY_LIST = new NID(0x201);
    //Reserved.
    public static final NID NID_RESERVED1 = new NID(0x241);
    //Global list of all Folder objects that are referenced by any Folder object's Search Criteria.
    public static final NID NID_SEARCH_DOMAIN_OBJECT = new NID(0x261);
    //Search Gatherer Queue (section 2.4.8.5.1).
    public static final NID NID_SEARCH_GATHERER_QUEUE = new NID(0x281);
    //Search Gatherer Descriptor (section 2.4.8.5.2).
    public static final NID NID_SEARCH_GATHERER_DESCRIPTOR = new NID(0x2A1);
    //Reserved.
    public static final NID NID_RESERVED2 = new NID(0x2E1);
    //Reserved.
    public static final NID NID_RESERVED3 = new NID(0x301);
    //Search Gatherer Folder Queue (section 2.4.8.5.3)
    public static final NID NID_SEARCH_GATHERER_FOLDER_QUEUE = new NID(0x321);
    
    public static Map<String, NID> getInternalNids() {
        try {
            Map<String, NID> answer = new HashMap<>();
            for(Field f : InternalNids.class.getFields()) {
                if(f.getType() == NID.class && Modifier.isStatic(f.getModifiers())) {
                    answer.put(f.getName(), (NID) f.get(null));
                }
            }
            return answer;
        } catch(Exception e) {
            throw new IllegalStateException(e);
        }
        
        
    }
}
