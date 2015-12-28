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

package com.github.sbridges.pasta.model.ltp.pc;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.github.sbridges.pasta.util.ByteUtils;
import com.github.sbridges.pasta.util.Bytes;

/**
 * Properties for the property context 
 *
 */
public class Property<T> {
    
    private static final Map<Short, Property<?>> ALL_PROPS = new HashMap<>();
    
    //2.4.3.1 Minimum Set of Required Properties
    //Record Key. This is the Provider UID of this PST.
    public static Property<Bytes> PidTagRecordKey = new Property<>("PidTagRecordKey", 0x0FF9, PropertyType.PtypBinary);
    //Display Name of PST 
    public static Property<String> PidTagDisplayName= new Property<>("PidTagDisplayName", 0x3001, PropertyType.PtypString);
    //EntryID of the Root Mailbox Folder object
    public static Property<Bytes> PidTagIpmSuBTreeEntryId= new Property<>("PidTagIpmSuBTreeEntryId", 0x35E0, PropertyType.PtypBinary);
    //EntryID of the Deleted Items Folder object
    public static Property<Bytes> PidTagIpmWastebasketEntryId= new Property<>("PidTagIpmWastebasketEntryId", 0x35E3, PropertyType.PtypBinary);
    //EntryID of the search Folder object
    public static Property<Bytes> PidTagFinderEntryId= new Property<>("PidTagFinderEntryId", 0x35E7, PropertyType.PtypBinary);

    //2.4.4.1.1 Property Schema of a Folder object PC
    //Display name of the Folder object
    //Display name of the Folder object
    //Display name of the Folder object
    //Total number of items in the Folder object
    public static Property<Integer> PidTagContentCount = new Property<>("PidTagContentCount", 0x3602, PropertyType.PtypInteger32);
    //Number of unread items in the Folder object
    public static Property<Integer> PidTagContentUnreadCount = new Property<>("PidTagContentUnreadCount", 0x3603, PropertyType.PtypInteger32);
    //Whether the Folder object has any sub-Folder objects
    public static Property<Boolean> PidTagSubfolders = new Property<>("PidTagSubfolders", 0x360A, PropertyType.PtypBoolean);

    //2.4.3.3
    //Specifically, the CRC-32 hash of the password text is stored in the 
    //PidTagPstPassword property in the PC associated with NID_MESSAGE_STORE,
    //and if the property exists and is nonzero, implementations SHOULD prompt 
    //the end user for a password, compute the CRC-32 hash of the user password,
    //and verify it against the value stored in PidTagPstPassword
    public static Property<Integer> PidTagPstPassword = new Property<>("PidTagPstPassword", 0x67FF, PropertyType.PtypInteger32);  
    
    //Message class. And it has an alternate name PidTagMessageCl ass_W.  
    public static Property<String> PidTagMessageClass = new Property<>("PidTagMessageClass", 0x001A, PropertyType.PtypString);
    //Cc: line 
    public static Property<String> PidTagDisplayCcW = new Property<>("PidTagDisplayCcW", 0x0E03, PropertyType.PtypString);
    //To: line 
    public static Property<String> PidTagDisplayToW = new Property<>("PidTagDisplayToW", 0x0E04, PropertyType.PtypString);
    //Message delivery timestamp 
    public static Property<Instant> PidTagMessageDeliveryTime = new Property<>("PidTagMessageDeliveryTime", 0x0E06, PropertyType.PtypTime);
    //Message flags.  
    public static Property<Integer> PidTagMessageFlags = new Property<>("PidTagMessageFlags", 0x0E07, PropertyType.PtypInteger32);
    //Message size 
    public static Property<Integer> PidTagMessageSize = new Property<>("PidTagMessageSize", 0x0E08, PropertyType.PtypInteger32);
    //Message status.  
    public static Property<Integer> PidTagMessageStatus = new Property<>("PidTagMessageStatus", 0x0E17, PropertyType.PtypInteger32);
    //Replication item ID 
    public static Property<Bytes> PidTagReplItemid = new Property<>("PidTagReplItemid", 0x0E30,
            //the pst spec says this is an Integer32 type
            //but sample pst files disagree
            PropertyType.PtypBinary);
    //Replication change number 
    public static Property<Long> PidTagReplChangenum = new Property<>("PidTagReplChangenum", 0x0E33, PropertyType.PtypInteger64);
    //Replication version history 
    public static Property<Bytes> PidTagReplVersionHistory = new Property<>("PidTagReplVersionHistory", 0x0E34, PropertyType.PtypBinary);
    //Replication flags.  
    public static Property<Integer> PidTagReplFlags = new Property<>("PidTagReplFlags", 0x0E38, PropertyType.PtypInteger32);
    //Replication version information 
    public static Property<Bytes> PidTagReplCopiedfromVersionhistory = new Property<>("PidTagReplCopiedfromVersionhistory", 0x0E3C, PropertyType.PtypBinary);
    //Replication item ID information 
    public static Property<Bytes> PidTagReplCopiedfromItemid = new Property<>("PidTagReplCopiedfromItemid", 0x0E3D, PropertyType.PtypBinary);
    //Temporary flags 
    public static Property<Integer> PidTagItemTemporaryFlags = new Property<>("PidTagItemTemporaryFlags", 0x1097, PropertyType.PtypInteger32);
    //Last modification time of Message object 
    public static Property<Instant> PidTagLastModificationTime = new Property<>("PidTagLastModificationTime", 0x3008, PropertyType.PtypTime);
    
    //Container class of the sub-Folder object.  
    public static Property<String> PidTagContainerClass = new Property<>("PidTagContainerClass", 0x3613,
            //Note 2.4.4.4.1 of PST spec says this is PTypeBinary, but 
            //2.633 PidTagContainerClass of MS-0XProps defines this as pTypeString
            PropertyType.PtypString);
    //Secure submit flags 
    public static Property<Integer> PidTagSecureSubmitFlags = new Property<>("PidTagSecureSubmitFlags", 0x65C6, PropertyType.PtypInteger32);
    //Total number of hidden Items in sub-Folder object.  
    public static Property<Integer> PidTagPstHiddenCount = new Property<>("PidTagPstHiddenCount", 0x6635, PropertyType.PtypInteger32);
    //Unread hidden items in sub-Folder object.  
    public static Property<Integer> PidTagPstHiddenUnread = new Property<>("PidTagPstHiddenUnread", 0x6636, PropertyType.PtypInteger32);
    //LTP row ID.  
    public static Property<Integer> PidTagLtpRowId = new Property<>("PidTagLtpRowId", 0x67F2, PropertyType.PtypInteger32);
    //LTP row version.  
    public static Property<Integer> PidTagLtpRowVer = new Property<>("PidTagLtpRowVer", 0x67F3, PropertyType.PtypInteger32);
    //OAB name. And it has an alternate name PidTagOfflineAdd ressBookName_ W.  
    public static Property<String> PidTagOfflineAddressBookName = new Property<>("PidTagOfflineAddressBookName", 0x6800, PropertyType.PtypString);
    //Send recall report.  
    public static Property<Boolean> PidTagSendOutlookRecallReport = new Property<>("PidTagSendOutlookRecallReport", 0x6803, PropertyType.PtypBoolean);
    //OAB truncated properties. 
    public static Property<int[]> PidTagOfflineAddressBookTruncatedProperties = new Property<>("PidTagOfflineAddressBookTruncatedProperties", 0x6805, PropertyType.PtypMultipleInteger32);
    //View descriptor flags.  
    public static Property<Integer> PidTagViewDescriptorFlags = new Property<>("PidTagViewDescriptorFlags", 0x7003, PropertyType.PtypInteger32);
    //View descriptor link.  
    public static Property<Bytes> PidTagViewDescriptorLinkTo = new Property<>("PidTagViewDescriptorLinkTo", 0x7004, PropertyType.PtypBinary);
    //View descriptor Folder object.  
    public static Property<Bytes> PidTagViewDescriptorViewFolder = new Property<>("PidTagViewDescriptorViewFolder", 0x7005, PropertyType.PtypBinary);
    //View descriptor name.  
    public static Property<String> PidTagViewDescriptorName = new Property<>("PidTagViewDescriptorName", 0x7006, PropertyType.PtypString);
    //View descriptor version.  
    public static Property<Integer> PidTagViewDescriptorVersion = new Property<>("PidTagViewDescriptorVersion", 0x7007, PropertyType.PtypInteger32);

    //Importance 
    public static Property<Integer> PidTagImportance = new Property<>("PidTagImportance", 0x0017, PropertyType.PtypInteger32);
    //Sensitivity 
    public static Property<Integer> PidTagSensitivity = new Property<>("PidTagSensitivity", 0x0036, PropertyType.PtypInteger32);
    //Subject 
    public static Property<String> PidTagSubjectW = new Property<>("PidTagSubjectW", 0x0037, PropertyType.PtypString);
    //Submit timestamp 
    public static Property<Instant> PidTagClientSubmitTime = new Property<>("PidTagClientSubmitTime", 0x0039, PropertyType.PtypTime);
    //Sender representative name 
    public static Property<String> PidTagSentRepresentingNameW = new Property<>("PidTagSentRepresentingNameW", 0x0042, PropertyType.PtypString);
    //Whether recipient is in To: line 
    public static Property<Boolean> PidTagMessageToMe = new Property<>("PidTagMessageToMe", 0x0057, PropertyType.PtypBoolean);
    //Whether recipient is in Cc: line 
    public static Property<Boolean> PidTagMessageCcMe = new Property<>("PidTagMessageCcMe", 0x0058, PropertyType.PtypBoolean);
    //Conversation topic 
    public static Property<String> PidTagConversationTopicW = new Property<>("PidTagConversationTopicW", 0x0070, PropertyType.PtypString);
    //Conversation index 
    public static Property<Bytes> PidTagConversationIndex = new Property<>("PidTagConversationIndex", 0x0071, PropertyType.PtypBinary);
    

    //Contains a computed value derived from other conversation-related properties
    public static Property<Bytes> PidTagConversationId = new Property<>("PidTagConversationId", 0x3013, PropertyType.PtypBinary);

    public static Property<Integer> PidTagNameidBucketCount = new Property<>("PidTagNameidBucketCount", 0x0001, PropertyType.PtypInteger32);
    public static Property<Bytes> PidTagNameidStreamGuid = new Property<>("PidTagNameidStreamGuid", 0x0002, PropertyType.PtypBinary);
    public static Property<Bytes> PidTagNameidStreamEntry = new Property<>("PidTagNameidStreamEntry", 0x0003, PropertyType.PtypBinary);
    public static Property<Bytes> PidTagNameidStreamString = new Property<>("PidTagNameidStreamString", 0x0004, PropertyType.PtypBinary);
    //NOTE - this clashes with PidTagBody
    //public static Property<Bytes> PidTagNameidBucketBase = new Property<>("PidTagNameidBucketBase", 0x1000, PropertyType.PtypBinary);
    public static Property<Integer> PidTagPstBestBodyProptag = new Property<>("PidTagPstBestBodyProptag", 0x661D, PropertyType.PtypInteger32);
    public static Property<Boolean> PidTagPstIpmsubTreeDescendant = new Property<>("PidTagPstIpmsubTreeDescendant", 0x6705, PropertyType.PtypBoolean);
    public static Property<Integer> PidTagPstSubTreeContainer = new Property<>("PidTagPstSubTreeContainer", 0x6772, PropertyType.PtypInteger32);
    public static Property<Integer> PidTagLtpParentNid = new Property<>("PidTagLtpParentNid", 0x67F1, PropertyType.PtypInteger32);
    public static Property<String> PidTagMapiFormComposeCommand = new Property<>("PidTagMapiFormComposeCommand", 0x682F, PropertyType.PtypString);

    //Creation time.
    public static Property<Instant> PidTagCreationTime = new Property<>("PidTagCreationTime", 0x3007, PropertyType.PtypTime);
    //Message Search Key.
    public static Property<Bytes> PidTagSearchKey = new Property<>("PidTagSearchKey", 0x300B, PropertyType.PtypBinary);

    
    public static Property<Bytes> PidTagSentRepresentingSearchKey = new Property<>("PidTagSentRepresentingSearchKey", 0x003B, PropertyType.PtypBinary);
    public static Property<String> PidTagSubjectPrefix = new Property<>("PidTagSubjectPrefix", 0x003D, PropertyType.PtypString);
    public static Property<Bytes> PidTagSentRepresentingEntryId = new Property<>("PidTagSentRepresentingEntryId", 0x0041, PropertyType.PtypBinary);
    public static Property<String> PidTagInternetMessageId = new Property<>("PidTagInternetMessageId", 0x1035, PropertyType.PtypString);
    public static Property<Bytes> PidTagHtml = new Property<>("PidTagHtml", 0x1013, PropertyType.PtypBinary);
    public static Property<String> PidTagBody = new Property<>("PidTagBody", 0x1000, PropertyType.PtypString);
    public static Property<Integer> PidTagAccessLevel = new Property<>("PidTagAccessLevel", 0x0FF7, PropertyType.PtypInteger32);
    public static Property<Integer> PidTagAccess = new Property<>("PidTagAccess", 0x0FF4, PropertyType.PtypInteger32);
    public static Property<Integer> PidTagAttachNumber = new Property<>("PidTagAttachNumber", 0x0E21, PropertyType.PtypInteger32);
    public static Property<Boolean> PidTagRtfInSync = new Property<>("PidTagRtfInSync", 0x0E1F, PropertyType.PtypBoolean);
    public static Property<String> PidTagNormalizedSubject = new Property<>("PidTagNormalizedSubject", 0x0E1D, PropertyType.PtypString);
    public static Property<Boolean> PidTagHasAttachments = new Property<>("PidTagHasAttachments", 0x0E1B, PropertyType.PtypBoolean);
    public static Property<String> PidTagDisplayBcc = new Property<>("PidTagDisplayBcc", 0x0E02, PropertyType.PtypString);
    public static Property<String> PidTagSenderEmailAddress = new Property<>("PidTagSenderEmailAddress", 0x0C1F, PropertyType.PtypString);
    public static Property<String> PidTagSenderAddressType = new Property<>("PidTagSenderAddressType", 0x0C1E, PropertyType.PtypString);
    public static Property<Bytes> PidTagSenderSearchKey = new Property<>("PidTagSenderSearchKey", 0x0C1D, PropertyType.PtypBinary);
    public static Property<String> PidTagSenderName = new Property<>("PidTagSenderName", 0x0C1A, PropertyType.PtypString);
    public static Property<Bytes> PidTagSenderEntryId = new Property<>("PidTagSenderEntryId", 0x0C19, PropertyType.PtypBinary);
    public static Property<String> PidTagTransportMessageHeaders = new Property<>("PidTagTransportMessageHeaders", 0x007D, PropertyType.PtypString);
    public static Property<String> PidTagSentRepresentingAddressType = new Property<>("PidTagSentRepresentingAddressType", 0x0064, PropertyType.PtypString);
    public static Property<String> PidTagSentRepresentingEmailAddress = new Property<>("PidTagSentRepresentingEmailAddress", 0x0065, PropertyType.PtypString);
    
    public static Property<Integer> PidTagRecipientNumber = new Property<>("PidTagRecipientNumber", 0x6662, PropertyType.PtypInteger32);
    
    //Contains a bitmask of flags that client applications query to determine the characteristics of a message store.
    public static Property<Integer> PidTagStoreSupportMask = new Property<>("PidTagStoreSupportMask", 0x340D, PropertyType.PtypInteger32);
    //Contains a provider-defined MAPIUID structure that indicates the type of the message store.
    public static Property<Bytes> PidTagStoreProvider = new Property<>("PidTagStoreProvider", 0x3414, PropertyType.PtypBinary);
    //indicates the code page used for PR_BODY (PidTagBody) or PR_BODY_HTML (PidTagBodyHtml) properties.
    public static Property<Integer> PidTagInternetCodepage = new Property<>("PidTagInternetCodepage", 0x3FDE, PropertyType.PtypInteger32);
    
    
    public static Property<Bytes> PidTagTnefCorrelationKey = new Property<>("PidTagTnefCorrelationKey", 0x007F, PropertyType.PtypBinary);
    public static Property<String> PidTagInternetReturnPath = new Property<>("PidTagInternetReturnPath", 0x1046, PropertyType.PtypString);
    public static Property<Bytes> PidTagReplyRecipientEntries = new Property<>("PidTagReplyRecipientEntries", 0x004F, PropertyType.PtypBinary);
    
    
    public static Property<Boolean> PidTagOriginatorDeliveryReportRequested = new Property<>("PidTagOriginatorDeliveryReportRequested", 0x0023, PropertyType.PtypBoolean);
    public static Property<String> PidTagReplyRecipientNames = new Property<>("PidTagReplyRecipientNames", 0x0050, PropertyType.PtypString);
    
    private final String name;
    private final short code;
    private final PropertyType<T> type;

    public static Property<?> fromCode(short code) {
        Property<?> answer =  ALL_PROPS.get(code);
        if(answer == null) {
            throw new IllegalStateException("unrecognized code:" + ( 0xFFFF & code) + " hex:" + ByteUtils.shortToHex(code));
        }
        return answer;
    }
    
    private Property(String name, int code, PropertyType<T> type) {
        this.name = name;
        this.code = (short) code;
        this.type = type;
        
        ALL_PROPS.merge(this.code, this, (x, y) -> { throw new IllegalStateException("duplicate code:" + code); });
        
    }
    
    public String getName() {
        return name;
    }

    public short getCode() {
        return code;
    }

    public PropertyType<T> getType() {
        return type;
    }

    @Override
    public String toString() {
        return name + "[" + ByteUtils.shortToHex(code) + ", " + type.getName() +"]";
    }
    
}
