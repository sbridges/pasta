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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.github.sbridges.pasta.model.BID;

/**
 * 2.2.2.8.3.3 Subnode BTree
 * The subnode BTree collectively refers to all 
 * the elements that make up a subnode. 
 * The subnode BTree is a BTree that is made up of SIBLOCK 
 * and SLBLOCK structures, which contain SIENTRY and SLENTRY structures,
 *  respectively. These structures are defined in the following sections.
 *
 */
public class SubnodeBTree {

    private final BBT bbt;
    private final BID bidSub;
    
    public SubnodeBTree(BBT bbt, BID bidSub) {
        this.bbt = bbt;
        this.bidSub = bidSub;
    }

    public Iterable<SLEntry> getEntries() {
        Set<SLEntry> answer = new HashSet<>();
        addEntries(bidSub, answer);
        return answer;
        
    }
    
    private void addEntries(BID bid, Set<SLEntry> answer) {
        DataBlock db = load(bid);
        
        if(db.isSIBLock()) {
            for(SIEntry entry : new SIBlock(db).getSLEntries()) {
                addEntries(entry.getBid(), answer);
            }
        } else if(db.isSLBLock()) {
            for(SLEntry entry : new SLBlock(db).getSLEntries()) {
                answer.add(entry);
            }
        } else {
            throw new IllegalStateException("invalid type:" + db);
        }
        
        
    }

    public Optional<SLEntry> load(NID nid) {
        return find(bidSub, nid);
    }

    private Optional<SLEntry> find(BID bid, NID nid) {
        DataBlock db = load(bid);
        if(db.isSIBLock()) {
            SIEntry last = null;
            for(SIEntry entry : new SIBlock(db).getSLEntries()) {
                if(entry.getNid().getNid() > nid.getNid()) {
                    break;
                }
                last = entry;
            }
            if(last == null) {
                return Optional.empty();
            } else {
                return find(last.getBid(), nid);
            }
        } else if(db.isSLBLock()) {
            for(SLEntry entry : new SLBlock(db).getSLEntries()) {
                if(entry.getNid().equals(nid)) {
                    return Optional.of(entry);
                }
            }
            return Optional.empty();
        } else {
            throw new IllegalStateException("invalid type:" + db);
        }
    }

    private DataBlock load(BID bid) {
        DataBlock db = bbt.load(
                bbt.find(bid)
                .orElseThrow(() -> new IllegalStateException())
                );
        return db;
    }

}
