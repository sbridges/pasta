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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.sbridges.pasta.PstReader;
import com.github.sbridges.pasta.io.InMemoryPstIo;
import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.ltp.bth.BTH;
import com.github.sbridges.pasta.model.ltp.hn.BClientSig;
import com.github.sbridges.pasta.model.ltp.hn.HN;
import com.github.sbridges.pasta.model.ndb.NBTEntry;
import com.github.sbridges.pasta.model.ndb.NID;
import com.github.sbridges.pasta.model.ndb.SLEntry;
import com.github.sbridges.pasta.model.ndb.SubnodeBTree;
import com.github.sbridges.pasta.util.ByteUtils;
import com.github.sbridges.pasta.util.Bytes;

/**
 * 
 * 2.3.3 Property Context (PC)
 * 
 * The Property Context is built directly on top of a BTH. 
 * The existence of a PC is indicated at the HN level, 
 * where bClientSig is set to bTypePC. Implementation-wise, 
 * the PC is simply a BTH with cbKey=2 and cbEnt=6 (see section 2.3.3.3).
 * The following section explains the layout of a PC BTH record.
 *  
 * Each property is stored as an entry in the BTH. 
 * Accessing a specific property is just a matter of searching 
 * the BTH for a key that matches the property identifier 
 * of the desired property.
 *
 */
public class PC {

    private final BTH bth;
    private final Optional<NID> nidParent;
    private final NBTEntry entry;
    private final PstReader reader;
    
    public PC(PstReader reader, NID nid) {
        this.reader = reader;
        this.entry = reader.getNBT().load(nid).orElseGet(() -> { 
            throw new IllegalStateException("cant find nid" + nid);
        });
        this.nidParent = entry.getNidParent();
        HN hn = new HN(reader.getBBT(), entry.getBidData());
        if(hn.getHnhdr().getbClientSig() != BClientSig.bTypePC) {
            throw new IllegalStateException("wrong type:" + hn.getHnhdr());
        }
        BTH bth = new BTH(hn, hn.getHidUserRoot());

        this.bth = bth;
        if(bth.getHN().getHnhdr().getbClientSig() != BClientSig.bTypePC) {
            throw new IllegalStateException();
        }
        if(bth.getHeader().getCbKey() != 2 ||
           bth.getHeader().getCbEnt() != 6) {
            throw new IllegalStateException("invalid bth:" + bth);
        }
    }
    
    public List<byte[]> listKeys() {
        return bth.getKeys();
    }
    
    public List<Property<?>> listProps() {
        List<Property<?>> answer = new ArrayList<>();
        for(byte[] key : bth.getKeys()) {
            answer.add(Property.fromCode(ByteUtils.bytesToShort(key[0], key[1])));
        }
        return answer;
    }
    
    public Object load(byte[] key) {
        return load(key, Optional.empty());
    }
    
    public <T> T load(Property<T> property) {
        return (T) load(
                    toBytes(property),
                    Optional.of(property.getType())
                );
    }

    private <T> byte[] toBytes(Property<T> property) {
        return ByteUtils.shortToBytesLE(property.getCode());
    }
    
    public String debugString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PC nidParent:" + nidParent + " map:\n");
        for(byte[] key : listKeys()) {
            builder.append("   " +  new Bytes(key) + "->" + load(key) + "\n");
        }
        
        return builder.toString();
    }
    
    public boolean containsKey(Property<?> prop) {
        return bth.load(toBytes(prop)).isPresent();
    }
    
    public PropertyType<?> getType(byte[] key) {
        byte[] value = bth.load(key).orElseThrow(() -> new IllegalStateException("not found:" + ByteUtils.bytesToHex(key)));
        return PropertyType.fromCode(ByteUtils.bytesToShort(value[0], value[1]));
    }
    
    private Object load(byte[] key, Optional<PropertyType<?>> validationType) {
        byte[] value = bth.load(key).orElseThrow(() -> new IllegalStateException("not found:" + ByteUtils.bytesToHex(key)));
        
        PropertyType<?> type = PropertyType.fromCode(ByteUtils.bytesToShort(value[0], value[1]));
        
        
        validationType.ifPresent(t -> { if(t != type) {throw new IllegalStateException("expected:" + t + " got:" + type);}} );
        
        if(type.getSize().isVariable() || type.getSize().getSize() > 4) {
            HNID hnid = new HNID(ByteUtils.bytesToInt(value[2], value[3], value[4], value[5]));
            
            PstIo contents;
            //is this right
            if(hnid.isBlank()) {
                return type.loadVariableSize(new InMemoryPstIo(null, new byte[] {}));
            }
            else if(hnid.isHid()) {
                contents = bth.getHN().load(hnid.asHID());
            } else {
                SubnodeBTree sbt = new SubnodeBTree(reader.getBBT(), entry.getBidSub().get());
                SLEntry entry = sbt.load(hnid.asNID()).get();
                contents = reader.getBBT().load(entry.getBidData()).getDataDecrypted();
            }
            
            return type.loadVariableSize(contents);
            
            
        } else {
            return type.loadFixed(Arrays.copyOfRange(value, 2, 6));
        }
        
    }
    
    public Optional<NID> getNidParent() {
        return nidParent;
    }
    
    public BTH getBTH() {
        return bth;
    }



    
}
