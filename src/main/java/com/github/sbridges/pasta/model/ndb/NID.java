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

/**
 *  Nodes provide the primary abstraction used 
 *  to reference data stored in the PST file that
 *  is not interpreted by the NDB layer. Each node 
 *  is identified using its NID. Each NID is unique within 
 *  the namespace in which it is used. Each node referenced by 
 *  the NBT MUST have a unique NID. However, two subnodes
 *  of two different nodes can have identical NIDs,
 *  but two subnodes of the same node MUST have different NIDs.
 */
public class NID implements Comparable<NID> {

    public static final NID NID_ATTACHMENT_TABLE = new NID(0x671);
    
    private final long nid;

    public NID(long nid) {
        this.nid = nid;
        getType();
    }
    
    public NidType getType() {
        return NidType.fromCode(0X1F & (int) nid);
    }
    
    public long getIndex() {
        return nid >>> 5;
    }
    
    public NID copyWith(NidType type) {
        long newVal = nid & 0xFFFF_FFFF_FFFF_FFE0L | type.getCode();
        NID answer = new NID(newVal);
        if(answer.getType() != type || answer.getIndex() != getIndex()) {
            throw new IllegalStateException();
        }
        return answer;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (nid ^ (nid >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NID other = (NID) obj;
        if (nid != other.nid) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NID [getType()=" + getType() + 
                ", getIndex()=0x" + Long.toHexString(getIndex()) + 
                " nid=0x" + Long.toHexString(nid) + "]";
    }
    
    
    @Override
    public int compareTo(NID o) {
        return Long.compare(this.nid, o.nid);
    }
    
    public long getNid() {
        return nid;
    }
    
    
}
