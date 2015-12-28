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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.ltp.hn.BClientSig;
import com.github.sbridges.pasta.model.ltp.hn.HID;
import com.github.sbridges.pasta.model.ltp.pc.HNID;
import com.github.sbridges.pasta.model.ltp.pc.Property;
import com.github.sbridges.pasta.util.ByteUtils;

/**
 * 2.3.4.1 TCINFO
 * 
 * TCINFO is the header structure for the TC. 
 * The TCINFO is accessed using the hidUserRoot field in 
 * the HNHDR structure of the containing HN. 
 * The header contains the Column definitions and other relevant data. 
 *
 */
public class TCINFO {

    //bType (1 byte): TC signature; MUST be set to bTypeTC.
    private final BClientSig bType;
    
    //cCols (1 byte): Column count. This specifies the number of columns in the TC.
    private final int cCols;
    
    //rgib (8 bytes): This is an array of 4 16-bit values that specify
    //the offsets of various groups of data in the actual row data. 
    //The application of this array is specified in section 2.3.4.4, 
    //which covers the data layout of the Row Matrix.
    //
    //￼￼Index |   Friendly name |   Meaning of rgib[Index] value
    //-----------------------------------------------------------------------
    //0     |   TCI_4b        |   Ending offset of 8- and 4-byte data value group.
    //1     |   TCI_2b        |   Ending offset of 2-byte data value group.
    //2     |   TCI_1b        |   Ending offset of 1-byte data value group.
    //3     |   TCI_bm        |   Ending offset of the Cell Existence Block.
    //
    private final int tci4b;
    private final int tci2b;
    private final int tci1b;
    private final int tcibm;
    
    //hidRowIndex (4 bytes): HID to the Row ID BTH. 
    //The Row ID BTH contains (RowID, RowIndex) value pairs that 
    //correspond to each row of the TC. The RowID is a value that
    //is associated with the row identified by the RowIndex, 
    //whose meaning depends on the higher level structure that implements 
    //this TC. The RowIndex is the zero-based index to a particular row in the Row Matrix.
    private final HID hidRowIndex;
    
    //hnidRows (4 bytes): HNID to the Row Matrix (that is, actual table data). 
    //This value is set to zero (0) if the TC contains no rows.
    private final HNID hnidRows;
    
    //hidIndex (4 bytes): Deprecated. Implementations SHOULD ignore this value,
    //and creators of a new PST MUST set this value to zero (0).
    private final int hidIndex;
    
    //rgTCOLDESC (variable): Array of Column Descriptors. 
    //This array contains cCol entries of type TCOLDESC structures that define each TC column.
    private final List<TCOLDESC> rgTCOLDESC;
    
    private final Map<Property<?>, TCOLDESC> props = new LinkedHashMap<>();
    
    public TCINFO(PstIo slice) {
        bType = BClientSig.fromCode(slice.readByte());
        cCols = 0xFF & slice.readByte();
        tci4b = slice.readW();
        tci2b = slice.readW();
        tci1b = slice.readW();
        tcibm = slice.readW();
        hidRowIndex = new HID(slice.readDw());
        hnidRows = new HNID(slice.readDw());
        hidIndex = slice.readDw();
        
        rgTCOLDESC = new ArrayList<>(cCols);
        for(int i =0; i < cCols; i++) {
            rgTCOLDESC.add(new TCOLDESC(slice.sliceAndSkip(8)));
        }
        
        for(TCOLDESC colDesc : rgTCOLDESC) {
            short property = (short) (colDesc.getTag() >>> 16);
            short type = (short) colDesc.getTag();
            
            Property<?> p;
            try {
                p = Property.fromCode(property);
            } catch(IllegalStateException e) {
                throw new IllegalStateException("not found:" + ByteUtils.intToHex(colDesc.getTag()),  e);
            }
            
            if(p.getType().getCode() != type) {
                throw new IllegalStateException("wrong type for:" + ByteUtils.intToBytesLE(colDesc.getTag()) + " got prop:" + p);
            }
            
            if(p.getType().getSize().isVariable()) {
                //the size of an HNID
                if(colDesc.getCbData() != 4) {
                    throw new IllegalStateException("invalid size:" + colDesc);
                }
            } else if(p.getType().getSize().getSize() != colDesc.getCbData()) {
                throw new IllegalStateException("invalid size:" + colDesc + " type:" + p.getType());
            }
            
            props.merge(p, colDesc, (l, r) -> { throw new IllegalStateException("dupe property:" + p); });
        }
        
        if(bType != BClientSig.bTypeTC) {
            throw new IllegalStateException("invalid bType:" + this);
        }
        if(hidIndex != 0) {
            throw new IllegalStateException("invalid hidIndex:" + this);
        }
        
        //these should be increasing
        if(getTCI_2b() < getTCI_4b() || 
           getTCI_1b() < getTCI_2b() ||
           getTCI_bm() < getTCI_1b()) {
            throw new IllegalStateException("invalid offsets:" + this);
        }
        slice.assertExhausted();
    }

    public BClientSig getbType() {
        return bType;
    }

    public int getcCols() {
        return cCols;
    }

    public HID getHidRowIndex() {
        return hidRowIndex;
    }

    public HNID getHnidRows() {
        return hnidRows;
    }

    public List<TCOLDESC> getRgTCOLDESC() {
        return rgTCOLDESC;
    }

    public List<Property<?>> getProperties() {
        return new ArrayList<>(props.keySet());
    }
    
    @Override
    public String toString() {
        return "TCINFO [bType=" + bType + ", cCols=" + cCols + ", tci4b="
                + tci4b + ", tci2b=" + tci2b + ", tci1b=" + tci1b + ", tcibm="
                + tcibm + ", hidRowIndex=" + hidRowIndex + ", hnidRows="
                + hnidRows + ", hidIndex=" + hidIndex + ", rgTCOLDESC="
                + rgTCOLDESC + "]";
    }

    public int getTCI_4b() {
        return tci4b;
    }
    
    public int getTCI_2b() {
        return tci2b;
    }
    
    public int getTCI_1b() {
        return tci1b;
    }
    
    public int getTCI_bm() {
        return tcibm;
    }

    public TCOLDESC getTColDesc(Property<?> prop) {
        return props.computeIfAbsent(prop, __ -> {throw new IllegalStateException("not found:" + prop); });
    }
    
}
