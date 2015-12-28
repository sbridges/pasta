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
 * 2.3.4.2 TCOLDESC
 * 
 * The TCOLDESC structure describes a single column in the TC,
 *  which includes metadata about the size of the data associated 
 *  with this column, as well as whether a column exists, 
 *  and how to locate the column data from the Row Matrix.
 *
 */
public class TCOLDESC {
    //tag (4 bytes): This field specifies that 32-bit tag that is associated with the column.
    private final int tag;
    
    //ibData (2 bytes): Data Offset. This field indicates the offset
    //from the beginning of the row data (in the Row Matrix) where the 
    //data for this column can be retrieved. 
    //Because each data row is laid out the same way in the Row Matrix,
    //the Column data for each row can be found at the same offset.
    private final int ibData;
    
    //cbData (1 byte): Data size. This field specifies 
    //the size of the data associated with this column
    //(that is, "width" of the column), in bytes per row.
    //However, in the case of variable-sized data,
    //this value is set to the size of an HNID instead. 
    //This is explained further in section 2.3.4.4.
    private final int cbData;
    
    //iBit (1 byte): Cell Existence Bitmap Index. 
    //This value is the 0-based index into the CEB bit that corresponds to this Column.
    private final int iBit;
    
    public TCOLDESC(PstIo slice) {
        tag = slice.readDw();
        ibData = slice.readW();
        cbData = 0xFF & slice.readByte();
        iBit = 0xFF & slice.readByte();
    }
    
    public int getTag() {
        return tag;
    }

    public int getIbData() {
        return ibData;
    }

    public int getCbData() {
        return cbData;
    }

    public int getiBit() {
        return iBit;
    }

    @Override
    public String toString() {
        return "TCOLDESC [tag=" + tag + ", ibData=" + ibData + ", cbData="
                + cbData + ", iBit=" + iBit + "]";
    }
    
    

}
