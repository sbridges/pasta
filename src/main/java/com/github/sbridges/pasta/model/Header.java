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

package com.github.sbridges.pasta.model;

import java.util.Arrays;

import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.util.CRC;

/**
 * 2.2.2.6 HEADER
 * The HEADER structure is located at the beginning of the 
 * PST file (absolute file offset 0), and contains metadata 
 * about the PST file, as well as the ROOT information 
 * to access the NDB Layer data structures. Note that 
 * the layout of the HEADER structure, including the 
 * location and relative ordering of some fields, 
 * differs between the Unicode and ANSI versions.
 *  
 * */
public class Header {
    
    private static final byte[] dwMagic = new byte[] {
            0x21, 0x42, 0x44,0x4e
    };
    
    private static final byte[] wMagicClient = new byte[] {
            0x53, 0x4D
    };

    public long getBidNextP() {
        return bidNextP;
    }

    public long getBidNextB() {
        return bidNextB;
    }

    public BCryptMethod getBCryptMethod() {
        return bCryptMethod;
    }

    public Root getRoot() {
        return root;
    }

    private final long bidNextP;

    private final long bidNextB;

    private final BCryptMethod bCryptMethod;
    
    private final Root root;
    
    public Header(PstIo fileIo) {
        
        PstIo headerContents = fileIo.slice(0, 564);
        
        //dwMagic (4 bytes): MUST be "{ 0x21, 0x42, 0x44, 0x4E } ("!BDN")".
        if(!Arrays.equals(dwMagic, headerContents.read(4))) {
            throw new IllegalStateException("invalid dwMagic");
        }
        
        //The 32-bit cyclic redundancy check (CRC) value of the 471 bytes of
        //data starting from wMagicClient (0ffset 0x0008)
        int dwCRCPartial = headerContents.readDw();
        int calculatedDwCRCPartial = CRC.computeCRC(fileIo.read(8, 471));
        if(dwCRCPartial != calculatedDwCRCPartial) {
           throw new IllegalStateException("crc doesn't match, " + dwCRCPartial + " "
                   + "read:" + dwCRCPartial + 
                   " calculated:" + calculatedDwCRCPartial);
        }
        
        //wMagicClient (2 bytes): MUST be "{ 0x53, 0x4D }".
        if(!Arrays.equals(wMagicClient, headerContents.read(2))) {
            throw new IllegalStateException("invalid wMagicClient");
        }
        
        //wVer (2 bytes): File format version. This value MUST be 14 or 15 if the file is an ANSI PST file, and MUST be 23 if the file is a Unicode PST file.
        int wVer = headerContents.readW();
        if(wVer != 23) {
            throw new IllegalStateException("invalid wVer:" + wVer);
        }
        
        //wVer (2 bytes): File format version. This value MUST be 14 or 15 if the file is an ANSI PST file, and MUST be 23 if the file is a Unicode PST file.
        int wVerClient = headerContents.readW();
        if(wVerClient != 19) {
            throw new IllegalStateException("invalid wVerClient:" + wVerClient);
        }
        
        //bPlatformCreate (1 byte): This value MUST be set to 0x01.
        int bPlatformCreate = headerContents.readByte();
        if(bPlatformCreate != 1) {
            throw new IllegalStateException("invalid bPlatformCreate:" + bPlatformCreate);
        }
        
        //bPlatformAccess (1 byte): This value MUST be set to 0x01.
        int bPlatformAccess = headerContents.readByte();
        if(bPlatformAccess != 1) {
            throw new IllegalStateException("invalid bPlatformAccess:" + bPlatformAccess);
        }
        
        
        //dwReserved1 (4 bytes): Implementations SHOULD ignore this value and SHOULD NOT modify it. Creators of a new PST file MUST initialize this value to zero.<8>
        //dwReserved2 (4 bytes): Implementations SHOULD ignore this value and SHOULD NOT modify it. Creators of a new PST file MUST initialize this value to zero.<9>
        long reserved = headerContents.readLong();
        if(reserved != 0) {
            throw new IllegalStateException("reserved not 0");
        }
        //bidUnused (8 bytes Unicode only): Unused padding added when the Unicode PST file format was created.
        headerContents.skip(8);
        
        
        //bidNextB (4 bytes ANSI only): Next BID. This value is the monotonic counter that indicates the BID to be assigned for the next allocated block. BID values advance in increments of 4. For more details, see section 2.2.2.2.
        //bidNextP (Unicode: 8 bytes; ANSI: 4 bytes): Next page BID. Pages have a special counter for allocating bidIndex values. The value of bidIndex for BIDs for pages is allocated from this counter.
        //this is a unicde pst, so read 8 bytes
        bidNextP = headerContents.readLong();
        
        
        //dwUnique (4 bytes)
        //rgnid[] (128 bytes)
        //TODO we need to update rgnid when we write
        //qwUnused (8 bytes)
        headerContents.skip(140);
        
        //root (Unicode: 72 bytes; ANSI: 40 bytes): A ROOT structure (section 2.2.2.5).
        PstIo rootSlice = headerContents.sliceAndSkip(72); 
        //skip creating the root till we read the encryption method
        
        
        
        //dwAlign (4 bytes): Unused alignment bytes; MUST be set to zero. Unicode PST file format only.
        int dwAlign = headerContents.readDw();
        if(dwAlign != 0) {
            throw new IllegalStateException("dwAlign must be 0, not:" + dwAlign);
        }
        
        //rgbFM (128 bytes): Deprecated FMap. This is no longer used and MUST be filled with 0xFF. Readers SHOULD ignore the value of these bytes.
        headerContents.skip(128);
        
        //rgbFP (128 bytes): Deprecated FPMap. This is no longer used and MUST be filled with 0xFF. Readers SHOULD ignore the value of these bytes.
        headerContents.skip(128);
        
        //bSentinel (1 byte): MUST be set to 0x80.
        int bSentinal = headerContents.readByte() & 0xFF;
        if(bSentinal != 0x80) {
            throw new IllegalStateException("invalid bSentinal:" + bSentinal);
        }
        
        //bCryptMethod (1 byte)
        bCryptMethod = BCryptMethod.fromCode(headerContents.readByte());
        if(bCryptMethod == BCryptMethod.NDB_CRYPT_CYCLIC) {
            throw new IllegalStateException("unsupported crype:" + bCryptMethod);
        }
        
        root = new Root(bCryptMethod, rootSlice, fileIo.size());
        
        
        //rgbReserved (2 bytes):
        int rgbReserved = headerContents.readW();
        if(rgbReserved != 0) {
            throw new IllegalStateException("rgbReserved must be 0:" + rgbReserved);
        }
        
        bidNextB = headerContents.readLong();
        
        int dwCRCFull = headerContents.readDw();
        int calculatdDwCRCFull = CRC.computeCRC(fileIo.read(8, 516));
        if(dwCRCFull != calculatdDwCRCFull) {
            throw new IllegalStateException("invalid dwCRCFull, read:" + dwCRCFull + " calculated:" + calculatdDwCRCFull);
        }
        
        //rgbReserved2 (3 bytes): Implementations SHOULD ignore this value and SHOULD NOT modify it. Creators of a new PST MUST initialize this value to zero.<10>
        headerContents.skip(3);
        //bReserved (1 byte): Implementations SHOULD ignore this value and SHOULD NOT modify it. Creators of a new PST file MUST initialize this value to zero.<11>
        headerContents.skip(1);
        //rgbReserved3 (32 bytes): Implementations SHOULD ignore this value and SHOULD NOT modify it. Creators of a new PST MUST initialize this value to zero.<12>
        headerContents.skip(32);
        
        headerContents.assertExhausted();
    }
    
}
