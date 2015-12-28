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

package com.github.sbridges.pasta.model.ltp.hn;

/**
 * 2.3.1.1 HID
 * A HID is a 4-byte value that identifies an 
 * item allocated from the heap. 
 * The value is unique only within the heap itself. 
 */
public class HID {

    private final int hid;
    
    //hidIndex (11 bits): HID index. This is the 1-based index value that 
    //identifies an item allocated from the heap node. 
    //This value MUST NOT be zero (0).
    private final int hidIndex;
    
    //hidBlockIndex (16 bits): This is the 0-based data block index. 
    //This number indicates the 0-based index of the data block in which this 
    //heap item resides.
    private final int hidBlockIndex;

    public HID(int hid) {

        this.hid = hid;
        int w1 = hid & 0xFFFF;
        hidIndex = w1 >> 5;
        hidBlockIndex = hid >>> 16;
        
        //hidType (5 bits): HID Type; MUST be set to 0 (NID_TYPE_HID) to indicate a valid HID.
        if((w1 & 0x1F) != 0 || hidIndex <= 0 ) {
           throw new IllegalStateException("invalid hid this:" + this);
        }
    }
    
    public int getHidIndex() {
        return hidIndex;
    }

    public int getHidBlockIndex() {
        return hidBlockIndex;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + hid;
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
        HID other = (HID) obj;
        if (hid != other.hid) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "HID [hid=" + hid + ", hidIndex=" + hidIndex
                + ", hidBlockIndex=" + hidBlockIndex + "]";
    }
    
}
