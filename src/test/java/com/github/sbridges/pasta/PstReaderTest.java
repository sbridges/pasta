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

package com.github.sbridges.pasta;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.github.sbridges.pasta.PstReader;
import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.BID;
import com.github.sbridges.pasta.model.ltp.pc.PC;
import com.github.sbridges.pasta.model.ltp.pc.Property;
import com.github.sbridges.pasta.model.ltp.tc.TC;
import com.github.sbridges.pasta.model.message.EntryId;
import com.github.sbridges.pasta.model.message.Folder;
import com.github.sbridges.pasta.model.message.InternalNids;
import com.github.sbridges.pasta.model.message.Message;
import com.github.sbridges.pasta.model.message.NamedPropertyLookupMap;
import com.github.sbridges.pasta.model.ndb.BBT;
import com.github.sbridges.pasta.model.ndb.BBTEntry;
import com.github.sbridges.pasta.model.ndb.DataBlock;
import com.github.sbridges.pasta.model.ndb.NBT;
import com.github.sbridges.pasta.model.ndb.NBTEntry;
import com.github.sbridges.pasta.model.ndb.NID;
import com.github.sbridges.pasta.model.ndb.SIBlock;
import com.github.sbridges.pasta.model.ndb.SIEntry;
import com.github.sbridges.pasta.model.ndb.SLBlock;
import com.github.sbridges.pasta.model.ndb.SLEntry;
import com.github.sbridges.pasta.model.ndb.SubnodeBTree;
import com.github.sbridges.pasta.model.ndb.XBlock;

public class PstReaderTest {

    @Test
    public void testReadEnron() throws Exception {
        read("albert_meyers_000_1_1.pst");
    }
    
    private void read(String fileName) throws URISyntaxException, IOException {
        Path path = Paths.get(PstReaderTest.class.getClassLoader().getResource(fileName).toURI());
      
       
        
        try(PstReader reader = new PstReader(path)) {
            
            NamedPropertyLookupMap nplm = new NamedPropertyLookupMap(reader);
            
            Set<BID> bids = walkNBT(reader.getNBT(), reader.getBBT());
            walkBBT(bids, reader.getBBT(), reader.getNBT(), reader.getIo());
        
            validateMessageStore(reader);
            validateRootFolder(reader);
        }
    }

    private void validateMessageStore(PstReader reader) {
        
        PC pc = new PC(reader, InternalNids.NID_MESSAGE_STORE);
        validate(pc);
        UUID recordKey = pc.load(Property.PidTagRecordKey).toUUID();
        pc.load(Property.PidTagDisplayName);
        
        
        EntryId ipmSuBTreeEntryId = new EntryId(pc.load(Property.PidTagIpmSuBTreeEntryId).asIo());
        validateEntryId(ipmSuBTreeEntryId, recordKey, reader.getNBT());
         
        
        EntryId pidTagIpmWastebasketEntryId = new EntryId(pc.load(Property.PidTagIpmWastebasketEntryId).asIo());
        validateEntryId(pidTagIpmWastebasketEntryId, recordKey, reader.getNBT());
        
        EntryId pidTagFinderEntryId = new EntryId(pc.load(Property.PidTagFinderEntryId).asIo());
        validateEntryId(pidTagFinderEntryId, recordKey, reader.getNBT());
        
        assertEquals(Integer.valueOf(0), pc.load(Property.PidTagPstPassword));
    }
    
    private void validateRootFolder(PstReader reader) {
        
        Folder folder = new Folder(reader,  InternalNids.NID_ROOT_FOLDER);
        validateTree(folder);
    }
    
    public void validateTree(Folder folder) {
        validateSingle(folder);
        for(Folder child : folder.getChildren()) {
            validateTree(child);
        }
        for(Message m : folder.getMessages()) {
            //TODO - validate m.getPc()
            //it fails currently as some keys can't 
            //be mapped to properties
            assertNotNull(m.getPc().load(Property.PidTagSubjectW));
        }
    }

    public void validateSingle(Folder folder) {
        
        validate(folder.getPc());
        
        folder.getPc().load(Property.PidTagDisplayName);
        folder.getPc().load(Property.PidTagContentCount);
        folder.getPc().load(Property.PidTagContentUnreadCount);
        folder.getPc().load(Property.PidTagSubfolders);
        
      
        validate(folder.getHtTc());
        validate(folder.getHtTc());
        validate(folder.getAtTc());
        
        
       
    }
    
    public void validate(TC tc) {
        
        for(int rowId : tc.getRowIds()) {
            
            //rowId must be a valid NID
            new NID(rowId);
            assertEquals(rowId, (int) tc.get(rowId, Property.PidTagLtpRowId).get());
            tc.get(rowId, Property.PidTagLtpRowVer).get();
            
            for(Property<?> p : tc.getColumns()) {
                tc.get(rowId, p);
            }
        }
        
        
    }
    
    private void validateEntryId(EntryId entryId, UUID recordKey, NBT nbt) {
        assertEquals(recordKey, entryId.getUid());
        nbt.load(entryId.getNid()).get();
    }
    
    public void validate(PC pc) {
        for(Property<?> p : pc.listProps()) {
            pc.load(p);
        }
    }
    
    private Set<BID> walkNBT(NBT nbt, BBT bbt) {
        AtomicInteger interiorCount = new AtomicInteger();
        AtomicInteger leafCount = new AtomicInteger();
        
        Set<BID> bids = new HashSet<>();
        Set<NID> nids = new HashSet<>();
        
        nbt.walkDepthFirst(p -> {
            if(p.isLeaf()) {
                leafCount.incrementAndGet();
                if(p.isBBT()) {
                    throw new IllegalStateException("BBT in NBT?:" + p);
                } else {
                    for(NBTEntry entry : p.getNBTEntries()) {
                        entry.getBidSub().ifPresent(b -> { 
                            assertTrue(entry.getBidSub().get().isInternal());
                            nids.add(entry.getNid());
                            if(entry.getBidSub().isPresent()) {
                                bids.add(entry.getBidSub().get());
                                SubnodeBTree subNodeBTree = new SubnodeBTree(bbt, entry.getBidSub().get());
                                Set<NID> subNodeNids = new HashSet<>();
                                for(SLEntry slEntry : subNodeBTree.getEntries()) {
                                    assertTrue(subNodeBTree.load(slEntry.getNid()).isPresent());
                                    bids.add(slEntry.getBidData());
                                    slEntry.getBidSub().ifPresent(bids::add);
                                    assertTrue(subNodeNids.add(slEntry.getNid()));
                                }
                                assertFalse(subNodeNids.isEmpty());
                            }
                        });
                        
                    }
                }
            } else {
                interiorCount.incrementAndGet();
            }
            
        });
        assertTrue(leafCount.get() > 0);
        assertTrue(interiorCount.get() > 0);
        
        
        for(NID nid : nids) {
            assertTrue("can't find nid:" + nid, nbt.load(nid).isPresent());
        }
        
        return bids;
    }
    
    private void walkBBT(Set<BID> subNodeBids, BBT bbt, NBT nbt, PstIo io) {
        AtomicInteger interiorCount = new AtomicInteger();
        AtomicInteger leafCount = new AtomicInteger();
        
        Set<BID> bids = new HashSet<>();
        bids.addAll(subNodeBids);
        
        
        bbt.walkDepthFirst(p -> {
            if(p.isLeaf()) {
                leafCount.incrementAndGet();
                if(p.isBBT()) {
                    for(BBTEntry entry : p.getBBTEntries()) {
                        bids.add(entry.getBRef().getBid());
                        DataBlock dataBlock = bbt.load(entry);
                        if(dataBlock.isSIBLock()) {
                            for(SIEntry b : new SIBlock(dataBlock).getSLEntries()) {
                                bids.add(b.getBid());
                                assertTrue(nbt.load(b.getNid()).isPresent());
                            }
                        } else if(dataBlock.isSLBLock()) {
                            for(SLEntry b : new SLBlock(dataBlock).getSLEntries()) {
                                bids.add(b.getBidData());
                                b.getBidSub().ifPresent(bids::add);
                                
                                //TODO - find the NID of the SLENTRY in this sub tree
                                //this nid can't be found in the NBT since it is
                                //local to this subnode
                            }
                        } else if(dataBlock.isXBLock() || dataBlock.isXXBLock()) {
                            for(BID bid : new XBlock(dataBlock).getXBlockBids()) {
                                bids.add(bid);
                            }
                        } else if(entry.getBRef().getBid().isInternal()) {
                            throw new IllegalStateException("Unknown internal bid type");
                        }
                    }
                } else {
                    throw new IllegalStateException("NBT in BBT?:" + p);
                }
            } else {
                interiorCount.incrementAndGet();
            }
            
        });
        assertTrue(leafCount.get() > 0);
        assertTrue(interiorCount.get() > 0);
        
        for(BID bid : bids) {
            assertTrue(bbt.find(bid).isPresent());
            bbt.load(bbt.find(bid).get());
        }
    }
}
