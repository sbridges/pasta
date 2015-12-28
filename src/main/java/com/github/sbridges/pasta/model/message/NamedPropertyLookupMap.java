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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import com.github.sbridges.pasta.PstReader;
import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.ltp.pc.PC;
import com.github.sbridges.pasta.model.ltp.pc.Property;
import com.github.sbridges.pasta.model.ltp.pc.PropertyType;
import com.github.sbridges.pasta.util.ByteUtils;
import com.github.sbridges.pasta.util.Bytes;
import com.github.sbridges.pasta.util.CRC;
import com.github.sbridges.pasta.util.CollectionUtils;

/**
 * 2.4.7 Named Property Lookup Map
 * The mapping between NPIDs and property names is done 
 * using a special Name-to-ID-Map in the PST, with a special
 * NID of NID_NAME_TO_ID_MAP (0x61). There is one Name-to-ID-Map 
 * per PST. From an implementation point of view, the 
 * Name-to-ID-Map is a standard PC with some special properties. 
 * Specifically, the properties in the PC do not refer to 
 * real property identifiers, but instead point to 
 * specific data sections of the Name-to-ID-Map.
 */
public class NamedPropertyLookupMap {
    
    private final PC pc;
    
    public NamedPropertyLookupMap(PstReader reader) {
        
        pc = new PC(reader, InternalNids.NID_NAME_TO_ID_MAP);
        
        Map<Integer, String> names = new TreeMap<>();
        PstIo nameStreamBytes = pc.load(Property.PidTagNameidStreamString).asIo();
        
        while(!nameStreamBytes.isExhausted()) {
            int position = (int) nameStreamBytes.getPosition();
            int length = nameStreamBytes.readDw();
            PstIo slice = nameStreamBytes.sliceAndSkip(length);
            String name = PropertyType.PtypString.loadVariableSize(slice);
            //Padding is also added to the end of each string, so each length/string pair 
            //ends on a 4 byte boundary. The strings are not null terminated.
            if(length % 4 != 0) {
                
                nameStreamBytes.skip(4 - (length % 4));
            }
            names.put(position, name);
        }
        
        List<UUID> guids = new ArrayList<>();
        pc.load(Property.PidTagNameidStreamGuid).asIo().chunkAll(16).forEachRemaining(
                c -> {
                    guids.add(c.readUUID());
                    c.assertExhausted();
                });
        
        List<NameId> nameIds = new ArrayList<>();
        pc.load(Property.PidTagNameidStreamEntry).asIo().chunkAll(8).forEachRemaining(
                c -> nameIds.add(new NameId(c)));
        
        //From the spec :
        //
        //The bucket count is stored in the property PidTagNameidBucketCount.
        //This property contains the number of hash buckets in the hash table. 
        //The value of this property SHOULD be 251 (0xFB). Implementations, however, 
        //MUST consult PidTagNameidBucketCount to obtain the actual bucket count.
        
        int bucketCount = pc.load(Property.PidTagNameidBucketCount);
        if(bucketCount != 251) {
            throw new IllegalStateException("invalid bucket count:" + bucketCount);
        }
        
        
        for(NameId nameId : nameIds) {
            if(nameId.getN() == 1) {
                //If this value is 1, the named property identifier is a string.
                String name = names.get((int) nameId.getDwPropertyID());
                if(name == null) {
                    throw new IllegalStateException("not found:" + nameId);
                } 
            } else {
                //If this value is 0, the named property identifier is a 16-bit numerical value.
                nameId.getDwPropertyID();
            }
            
            UUID guid;
            if(nameId.getwGuid() == 0) {
                guid = null;
            } else if(nameId.getwGuid() == 1) {
                //PS_MAPI
                guid = UUID.fromString("00020328-0000-0000-C000-000000000046");
            } else if(nameId.getwGuid() == 2) {
                //PS_PUBLIC_STRINGS
                //almost, but not quite the same as PS_MAPI
                guid = UUID.fromString("00020329-0000-0000-C000-000000000046");
            } else {
                guid = guids.get((nameId.getwGuid() - 3) / 16);
            }
            
        }
        
        validateBuckets(names, nameIds, bucketCount);
    }


    void validateBuckets(Map<Integer, String> names,
            List<NameId> nameIds, int bucketCount) {
        for(NameId nameId : nameIds) {
            Bytes hashContents;
            hashContents = (Bytes) pc.load(getBucket(nameId, bucketCount));
            
            boolean found = false;
            for(PstIo io : CollectionUtils.all(hashContents.asIo().chunkAll(8))) {
                NameId modifiedNameId = new NameId(io);
                if(modifiedNameId.getwPropIdx() == nameId.getwPropIdx()) {
                    found = true;
                    if(modifiedNameId.getN() != nameId.getN()) {
                        throw new IllegalStateException();
                    }
                    if(modifiedNameId.getN() == 1) {
                        if(nameId.getN() != 1) {
                            throw new IllegalStateException("invalid:" + nameId);
                        }
                        
                        String string = names.get((int) nameId.getDwPropertyID());
                        int stringHash = CRC.computeCRC(string.getBytes(StandardCharsets.UTF_16LE));
                        
                        if((int)  modifiedNameId.getDwPropertyID() != stringHash) {
                            throw new IllegalStateException("expected:" + stringHash + " got:" + (int) modifiedNameId.getDwPropertyID());
                        } 
                                
                    }
                    
                }
            }
            if(!found) {
                
                throw new IllegalStateException("not found:" + nameId);
                
            }
        }
    }
    
    
    private static byte[] getBucket(NameId nameId, int bucketCount) {
        
        //2.4.7.5 Hash Table
        short bucket = 0x1000;
        bucket += (short) (nameId.getBucketNoMod() % bucketCount);
        return ByteUtils.shortToBytesLE(bucket);
        
    }
    
    public PC getPc() {
        return pc;
    }
}
