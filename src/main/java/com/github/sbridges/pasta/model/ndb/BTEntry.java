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

import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.BRef;

/**
 * 
 * 2.2.2.7.7.2 BTENTRY (Intermediate Entries)
 * 
 * BTENTRY records contain a key value (NID or BID) and a reference to a child BTPAGE page in the BTree.
 */
public class BTEntry {

    //btkey (Unicode: 8 bytes; ANSI: 4 bytes): 
    //The key value associated with this BTENTRY. 
    //All the entries in the child BTPAGE referenced 
    //by BREF have key values greater than or equal to this key value. 
    //The btkey is either an NID (zero extended to 8 bytes for Unicode PSTs)
    //or a BID, depending on the ptype of the page.
    private final long btkey;
    //BREF (Unicode: 16 bytes; ANSI: 8 bytes):
    //BREF structure (section 2.2.2.4) that points to the child BTPAGE.
    private final BRef bRef;  
    
    public BTEntry(PstIo slice) {
        btkey = slice.readLong();
        bRef = new BRef(slice.sliceAndSkip(16));
        slice.assertExhausted();
    }

    public long getBtkey() {
        return btkey;
    }

    public BRef getbRef() {
        return bRef;
    }

    @Override
    public String toString() {
        return "BTEntry [btkey=" + btkey + ", bRef=" + bRef + "]";
    }
}
