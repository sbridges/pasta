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

package com.github.sbridges.pasta.model.ltp.tc;

import java.util.ArrayList;
import java.util.List;

import com.github.sbridges.pasta.model.ltp.bth.BTH;
import com.github.sbridges.pasta.util.ByteUtils;

/**
 * 2.3.4.3.1 TCROWID
 * 
 * The TCROWID structure is a manifestation of the BTH
 *  data record (section 2.3.2.3). The size of the 
 *  TCROWID structure varies depending on the version of 
 *  the PST. For the Unicode PST, each record in the BTH 
 *  are 8 bytes in size, where cbKey is set to 4 and cEnt 
 *  is set to 4. For an ANSI PST, each record is 6 bytes 
 *  in size, where cbKey is set to 4 and cEnt is set to 2.
 */
public class RowIndex {

    private final BTH bth;

    public RowIndex(BTH bth) {
        this.bth = bth;
        
        if(bth.getHeader().getCbEnt() != 4 | bth.getHeader().getCbKey() != 4) {
            throw new IllegalStateException("invalid row index:" + bth);
        }
    }
    
    public int size() {
        return bth.getKeys().size();
    }
       
    public List<Integer> getRowIds() {
        List<Integer> answer = new ArrayList<>();
        for(byte[] key : bth.getKeys()) {
            answer.add(ByteUtils.bytesToInt(key));
        }
        return answer;
    }
    
    public int getRowIndex(int rowId) {
        byte[] val = bth
            .load(ByteUtils.intToBytesLE(rowId))
            .orElseThrow(() -> new IllegalStateException("can't load rowId:" + rowId + " this:" + bth.debugString()));
        return ByteUtils.bytesToInt(val);
    }
    
    @Override
    public String toString() {
        return "RowIndex [size=" + size() + " rowIds:" +  getRowIds() + "]";
    }
}
