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

import com.github.sbridges.pasta.io.PstIo;

/**
 * 
 * 2.3.4.3.1 TCROWID
 * 
 * The TCROWID structure is a manifestation of the BTH data 
 * record (section 2.3.2.3). The size of the TCROWID structure
 * varies depending on the version of the PST. For the
 * Unicode PST, each record in the BTH are 8 bytes 
 * in size, where cbKey=4 and cEnt=4. For an ANSI PST,
 * each record is 6 bytes in size, where cbKey=4 and cEnt=2.
 * The following is the binary layout of the TCROWID structure. 
 *
 */
public class TCROWID {

    
    //dwRowID (4 bytes): This is the 32-bit primary key value that
    //uniquely identifies a row in the Row Matrix.
    private final int dRowId;
    
    //dwRowIndex (Unicode: 4 bytes; ANSI: 2 bytes): 
    //The 0-based index to the corresponding row in 
    //the Row Matrix. Note that for ANSI PSTs, the maximum number of rows is 2^16.
    private final int dRowIndex;

    
    
    public TCROWID(PstIo slice) {
        this.dRowId = slice.readDw();
        this.dRowIndex = slice.readDw();
    }



    @Override
    public String toString() {
        return "TCROWID [dRowId=" + dRowId + ", dRowIndex=" + dRowIndex + "]";
    }

    public int getdRowId() {
        return dRowId;
    }

    public int getdRowIndex() {
        return dRowIndex;
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + dRowId;
        result = prime * result + dRowIndex;
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
        TCROWID other = (TCROWID) obj;
        if (dRowId != other.dRowId) {
            return false;
        }
        if (dRowIndex != other.dRowIndex) {
            return false;
        }
        return true;
    };

    
}
