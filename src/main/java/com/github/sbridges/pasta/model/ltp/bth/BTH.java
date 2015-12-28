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

package com.github.sbridges.pasta.model.ltp.bth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.ltp.hn.HID;
import com.github.sbridges.pasta.model.ltp.hn.HN;
import com.github.sbridges.pasta.util.CollectionUtils;

/**
 *
 * 2.3.2 BTree-on-Heap (BTH)
 * A BTree-on-Heap implements a classic BTree on a heap node.
 *  A BTH consists of several parts: A header, the BTree records, and optional BTree data.  
 */
public class BTH {
    
    private final HN hn;
    private final BTHHEADER header;
    
    public BTH(HN hn, HID hid) {
        this.hn = hn;
        
        header = new BTHHEADER(hn.load(hid));
        
        //check we can load the header, if it exists
        header.getHidRoot().map(h -> hn.load(h));
    }
    
    public HN getHN() {
        return hn;
    }
    
    public BTHHEADER getHeader() {
        return header;
    }
    
    public List<byte[]> getKeys() {
        List<byte[]> answer = new ArrayList<>();
        
        if(!header.getHidRoot().isPresent()) {
            return new ArrayList<>();
        }
        getKeys(header.getHidRoot().get(), header.getbIdxLevels(), answer);
        return answer;
    }
    
    private void getKeys(
            HID hid, 
            int idxLevel,
            List<byte[]> answer) {
        if(idxLevel == 0) {
            for(LeafBTH leaf : loadLeaf(hid)) {
                answer.add(leaf.getKey());
            }
        } else {
            for(IntermediateBTH i : loadIntermediate(hid)) {
                getKeys(i.getHid(), idxLevel -1, answer);
            }
        }
    }
    
    
    public String debugString() {
        StringBuilder answer = new StringBuilder();
        answer.append("BTH").append(hn.debugString()).append("\n");
        answer.append("HN:").append("\n");
        answer.append("Header:").append(header).append("\n");
        
        if(header.getHidRoot().isPresent()) {
            debugString(answer, header.getHidRoot().get(), header.getbIdxLevels(), 1);
        }
        return answer.toString();
        
    }

    private void debugString(StringBuilder builder, HID hid, int ibxLevel, int indent) {
        if(ibxLevel == 0) {
            indent(builder, indent);
            builder.append("leaf hid:" + hid + "\n");
            indent(builder, indent);
            for(LeafBTH leaf : loadLeaf(hid)) {
                builder.append("leafs entry:" + leaf).append(" ");
            }
            builder.append("\n");
        } else {
            indent(builder, indent);
            builder.append("intermediate hid:" + hid + "\n");
            indent(builder, indent);
            for(IntermediateBTH intermediate : loadIntermediate(hid)) {
                builder.append("intermediate entry:" + intermediate + " ");
                debugString(builder, intermediate.getHid(), ibxLevel-1, indent+1);
            }
            builder.append("\n");
        }
        
    }
    
    private void indent(StringBuilder builder, int indent) {
        for(int i =0; i < indent; i++) {
            builder.append("  ");
        }
    }

    private Iterable<IntermediateBTH> loadIntermediate(HID hid) {
        PstIo slice = hn.load(hid);
        int cb = header.getCbKey() + 4;
        
        if(slice.size() % cb != 0) {
            throw new IllegalStateException("block size not an integral number of entries?");
        }
        int cRef = (int) slice.size() / cb;
        
        return () -> CollectionUtils.transform(
                slice.chunk(0, cRef, cb),
                s -> new IntermediateBTH(s, header.getCbKey())
                );
    }
    
    private Iterable<LeafBTH> loadLeaf(HID hid) {
        PstIo slice = hn.load(hid);
        int cb = header.getCbKey() + header.getCbEnt();
        if(slice.size() % cb != 0) {
            throw new IllegalStateException("block size not an integral number of entries?");
        }
        int cEnt = (int) slice.size() / cb;
        
        return () -> CollectionUtils.transform(
                slice.chunk(0, cEnt, cb),
                s -> new LeafBTH(s, header.getCbKey(), header.getCbEnt())
                );
    }
    
    public Optional<byte[]> load(byte[] key) {
        if(key.length != header.getCbKey()) {
            throw new IllegalStateException("invalid key length:" + key + " this:" + this);
        }
        HID current = header.getHidRoot().get();
        
        //walk through the indexes
        int indexLevel = header.getbIdxLevels();
        while(indexLevel > 0) {
            HID last = null;
            for(IntermediateBTH index : loadIntermediate(current)) {
                if(greaterThan(index.getKey(), key)) {
                    break;
                }
                last = index.getHid();
            }
            
            indexLevel--;
            if(last == null) {
                return Optional.empty();
            }
            current = last;
        }
        
        //current we now point at a leaf hid
        for(LeafBTH leaf : loadLeaf(current)) {
            if(Arrays.equals(leaf.getKey(), key)) {
                return Optional.of(leaf.getValue());
            }
        }
        
        return Optional.empty();
    }
    
    
    private static boolean greaterThan(byte[] l, byte[] r) {
        if(l.length != r.length) {
            throw new IllegalStateException("sizes should be the same");
        }
        for(int i = 0; i < l.length; i++) {
            if(l[i] == r[i]) {
                continue;
            }
            //signed bytes! compare as int's taking
            //care to ignore sign
            if( (l[i] & 0xFF) >  (r[i] & 0xFF)) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public String toString() {
        return "BTH [hn=" + hn + ", header=" + header + "]";
    }
    
    
    
    
}
