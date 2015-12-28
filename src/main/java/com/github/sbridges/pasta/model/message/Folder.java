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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.sbridges.pasta.PstReader;
import com.github.sbridges.pasta.model.ltp.pc.PC;
import com.github.sbridges.pasta.model.ltp.pc.Property;
import com.github.sbridges.pasta.model.ltp.tc.TC;
import com.github.sbridges.pasta.model.ndb.NID;
import com.github.sbridges.pasta.model.ndb.NidType;
import com.github.sbridges.pasta.util.CollectionUtils;
import com.github.sbridges.pasta.util.TreePrinter;

/**
 * 2.4.4 Folders
 * Folder objects are hierarchical containers that are used to 
 * create a storage hierarchy for the message store. In the 
 * PST architecture, a single root Folder object exists at
 * the top of the message store, from which an arbitrarily
 * complex hierarchy of Folder objects descends to 
 * provide structured storage for all the Messaging objects.
 * 
 * At the LTP level, a Folder object is a composite entity 
 * that is represented using four LTP constructs. Specifically, 
 * each Folder object consists of one PC, which contains the 
 * properties directly associated with the Folder object,
 *  and three TCs for information about the contents, 
 *  hierarchy and other associated information of the Folder 
 *  object. Some Folder objects MAY have additional nodes 
 *  that pertain to Search, which is discussed in section 
 *  2.4.8.6.
 * 
 * At the NDB level, the 4 LTP constructs 
 * are persisted as 4 separate top-level nodes (that is, 4 different NIDs). 
 * For identification purposes, the nidIndex portion for each of 
 * the NIDs is the same to indicate that these nodes 
 * collectively make up a Folder object. However, each 
 * of the 4 NIDs has a different nidType value to
 *  differentiate their respective function. 
 *  The following diagram indicates the relationships among these elements.
 *
 */
public class Folder {

    private final PstReader reader;
    private final NID nid;
    private final TC ctTc;
    private final TC atTc;
    private final TC htTc;
    private final PC pc;
    

    public Folder(PstReader reader, NID nid) {
        
        if(nid.getType() != NidType.NID_TYPE_NORMAL_FOLDER) {
            throw new IllegalStateException("invalid nid:" + nid);
        }
        
        this.reader = reader;
        this.nid = nid;
        NID ht = nid.copyWith(NidType.NID_TYPE_HIERARCHY_TABLE);
        NID ct = nid.copyWith(NidType.NID_TYPE_CONTENTS_TABLE);
        NID at = nid.copyWith(NidType.NID_TYPE_ASSOC_CONTENTS_TABLE);
     
        pc = new PC(reader, nid);
        
        htTc = new TC(reader, ht);
        assertEquals(htTc.getColumns().toString(),
                Arrays.asList(
                        Property.PidTagReplItemid,
                        Property.PidTagReplChangenum,
                        Property.PidTagReplVersionHistory,
                        Property.PidTagReplFlags,
                        Property.PidTagDisplayName,
                        Property.PidTagContentCount,
                        Property.PidTagContentUnreadCount,
                        Property.PidTagSubfolders,
                        Property.PidTagContainerClass,
                        Property.PidTagPstHiddenCount,
                        Property.PidTagPstHiddenUnread,
                        Property.PidTagLtpRowId,
                        Property.PidTagLtpRowVer
                        ).toString()
                );
        
        ctTc = new TC(reader, ct);
        assertEquals(ctTc.getColumns().toString(),
                Arrays.asList(
                        Property.PidTagImportance,
                        Property.PidTagMessageClass,
                        Property.PidTagSensitivity,
                        Property.PidTagSubjectW,
                        Property.PidTagClientSubmitTime,
                        Property.PidTagSentRepresentingNameW,
                        Property.PidTagMessageToMe,
                        Property.PidTagMessageCcMe,
                        Property.PidTagConversationTopicW,
                        Property.PidTagConversationIndex,
                        Property.PidTagDisplayCcW,
                        Property.PidTagDisplayToW,
                        Property.PidTagMessageDeliveryTime,
                        Property.PidTagMessageFlags,
                        Property.PidTagMessageSize,
                        Property.PidTagMessageStatus,
                        Property.PidTagReplItemid,
                        Property.PidTagReplChangenum,
                        Property.PidTagReplVersionHistory,
                        Property.PidTagReplFlags,
                        Property.PidTagReplCopiedfromVersionhistory,
                        Property.PidTagReplCopiedfromItemid,
                        Property.PidTagItemTemporaryFlags,
                        Property.PidTagLastModificationTime,
                        //this is not in the spec
                        Property.PidTagConversationId,
                        Property.PidTagSecureSubmitFlags,
                        Property.PidTagLtpRowId,
                        Property.PidTagLtpRowVer
                        ).toString()
                );
       
        atTc = new TC(reader, at);
        assertEquals(atTc.getColumns().toString(),
                Arrays.asList(
                        Property.PidTagMessageClass,
                        Property.PidTagMessageFlags,
                        Property.PidTagMessageStatus,
                        Property.PidTagDisplayName,
                        //not in the spec
                        Property.PidTagLtpRowId,
                        Property.PidTagLtpRowVer,
                        Property.PidTagOfflineAddressBookName,
                        Property.PidTagSendOutlookRecallReport,
                        Property.PidTagOfflineAddressBookTruncatedProperties,
                        //not in the spec
                        Property.PidTagMapiFormComposeCommand,
                        Property.PidTagViewDescriptorFlags,
                        Property.PidTagViewDescriptorLinkTo,
                        Property.PidTagViewDescriptorViewFolder,
                        Property.PidTagViewDescriptorName,
                        Property.PidTagViewDescriptorVersion
                        ).toString()
                );
    }
    
    private void assertEquals(String expected, String actual) {
        if(!expected.equals(actual)) {
            throw new IllegalStateException("expected:" + expected + " got:" + actual);
        }
        
    }

    public String getDisplayName() {
        return getPc().load(Property.PidTagDisplayName);
    }

    public PC getPc() {
        return pc;
    }
    
    public TC getCtTc() {
        return ctTc;
    }

    public TC getAtTc() {
        return atTc;
    }

    public TC getHtTc() {
        return htTc;
    }
    
    public NID getParent() {
        return pc.getNidParent().get();
    }
    
    public int getMessageCount() {
        return ctTc.getRowCount();
    }
    
    public Iterable<Message> getMessages() {
        return () -> CollectionUtils.transform(
                ctTc.getRowIds().iterator(),
                rowId -> {
                    NID nid = new NID(rowId);
                    return new Message(reader, nid);
                }
                );
    }
    
    
    public List<Folder> getChildren() {
        
        /*
         * 2.4.4.4.2 Locating Sub-Folder Object Nodes
         * The RowIndex (section 2.3.4.3) of the hierarchy table TC 
         * provides a mechanism for efficiently locating immediate
         * sub-Folder objects. The dwRowIndex field represents 
         * the 0-based sub-Folder object row in the Row Matrix, 
         * whereas the dwRowID value represents the NID of the 
         * sub-Folder object node that corresponds to the row 
         * specified by RowIndex. For example, if a TCROWID is:
         * "{ dwRowID=0x8022, dwRowIndex=3 }", 
         * the sub-Folder object NID that corresponds to 
         * the fourth (first being 0th) sub-Folder object row 
         * in the Row Matrix is 0x8022.
         */
        
        
        List<Folder> answer = new ArrayList<>();
        for(Integer rowId : htTc.getRowIds()) {
            NID nid = new NID(rowId);
            if(nid.getType() != NidType.NID_TYPE_SEARCH_FOLDER) {
                Folder child = new Folder(reader, nid);
                if(child.getParent().equals(nid)) {
                    throw new IllegalStateException("wrong parent:" + child + " this:" + this);
                }
                answer.add(child);
            }
        }
        
        return answer;
    }

    @Override
    public String toString() {
        return "Folder [nid=" + nid + ", ctTc=" + ctTc + ", atTc=" + atTc
                + ", htTc=" + htTc + ", pc=" + pc + "]";
    }
    
    public String debugString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append("PC:" + pc.toString() + "\n");
        sb.append("htTc:" + htTc.debugString() + "\n");
        sb.append("ctTc:" + ctTc.debugString() + "\n");
        sb.append("atTc:" + atTc.debugString() + "\n");
        sb.append(TreePrinter.print(
                this, 
                Folder::getChildren,
                f -> "FOLDER:" + f.getDisplayName() + " messages:" + f.getMessageCount()));
        return sb.toString();
    }
    
    
}

