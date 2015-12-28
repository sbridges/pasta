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

import com.github.sbridges.pasta.model.ltp.hn.HID;
import com.github.sbridges.pasta.model.ndb.NID;

/**
 * 
 * 2.3.3.2 HNID
 * An HNID is a 32-bit hybrid value that represents either a HID or a NID. 
 * The determination is made by examining the hidType (or equivalently, nidType) value. 
 * The HNID refers to a HID if the hidType is NID_TYPE_HID. Otherwise, the HNID refers to a NID.
 * 
 * A HNID that refers to a HID indicates that the item is 
 * stored in the data block. An HNID that refers to a NID 
 * indicates that the item is stored in the subnode block, 
 * and the NID is the local NID under the subnode where the raw data is located.
 *
 */
public class HNID {
    private final int contents;
    
    public HNID(int contents) {
        this.contents = contents;
    }
    
    public HID asHID() {
        return new HID(contents);
    }
    
    public NID asNID() {
        return new NID(contents);
    }

    @Override
    public String toString() {
        return "HNID [contents=" + contents + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + contents;
        return result;
    }
    
    public boolean isBlank() {
        return contents == 0;
    }
    
    public boolean isHid() {
        return (contents & 0x1F) == 0;
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
        HNID other = (HNID) obj;
        if (contents != other.contents) {
            return false;
        }
        return true;
    }
    
    
    
    
}
