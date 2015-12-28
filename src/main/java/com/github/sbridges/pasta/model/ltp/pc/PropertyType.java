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

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.util.ByteUtils;
import com.github.sbridges.pasta.util.Bytes;

/**
 * [MS-OXCDATA]
 */
public class PropertyType<T> {

    public static enum Size {
        FIXED_1(1),
        FIXED_4(4),
        FIXED_8(8),
        VARIABLE(-1);
        
        private final int size;
        
        public int getSize() {
            if(isVariable()) {
                throw new IllegalStateException();
            }
            return size;
        }
        
        public boolean isVariable() {
            return size == -1;
        }
        
        private Size(int size) {
            this.size = size;
        }
    }
    
    private static final Map<Short, PropertyType<?>> lookup = new HashMap<>();
    
    public static PropertyType<Boolean> PtypBoolean = new PropertyType<Boolean>("PtypBoolean", Boolean.class, Size.FIXED_1, (short) 0x000B) {
        @Override
        public Boolean loadFixed(byte[] contents) {
            
            //a boolean must be 1 or 0, as a Little Endian 
            //integer, so the first byte contains
            //the value, the rest of the bytes must be 0
            
            for(int i = 1; i < contents.length; i++) {
                if(contents[i] != 0) {
                    throw new IllegalStateException();
                }
            }
            
            byte val = contents[0];
            if(val == 1) {
                return Boolean.TRUE;
            } else if(val == 0) {
                return Boolean.FALSE;
            } else {
                throw new IllegalStateException();
            }
        }
    };
    
    public static PropertyType<Integer> PtypInteger32 = new PropertyType<Integer>("PtypInteger32", Integer.class, Size.FIXED_4, (short) 0x0003) {
        @Override
        public Integer loadFixed(byte[] contents) {
            if(contents.length != 4) {
                throw new IllegalStateException();
            }
            return ByteUtils.bytesToInt(contents);
        }
    };
    
    
    
    public static PropertyType<Long> PtypInteger64 = new PropertyType<Long>("PtypInteger64", Long.class, Size.FIXED_8, (short) 0x0014) {
        
        //from a PC, we will be loaded from 
        //the heap, as direct values can only be 4 bytes
        @Override
        public Long loadVariableSize(PstIo io) {
            io.seek(0);
            long answer = io.readLong();
            io.assertExhausted();
            return answer;
        }

        //from a TC we will be loaded directly
        @Override
        public Long loadFixed(byte[] contents) {
            if(contents.length != 8) {
                throw new IllegalStateException();
            }
            return ByteUtils.bytesToLong(contents, 0);
        }
    };
    
    /*
     * PtypTime
     * 0x0040, %x40.00
     * 8 bytes; a 64-bit integer representing the number of 100-nanosecond intervals since January 1, 1601
     * [MS-DTYP]: FILETIME
     * PT_SYSTIME, time, datetime, datetime.tz, datetime.rfc1123, Date, time, time.tz 
     */
    public static PropertyType<Instant> PtypTime = new PropertyType<Instant>("PtypTime", Instant.class, Size.FIXED_8, (short) 0x0040) {
        @Override
        public Instant loadVariableSize(PstIo io) {
            io.seek(0);
            long intervals = io.readLong();
            //http://stackoverflow.com/questions/5200192/convert-64-bit-windows-number-to-time-java
            long msSinceEpoch = (intervals-116444736000000000L)/10000;
            io.assertExhausted();
            return Instant.ofEpochMilli(msSinceEpoch);
        }  
    };
    
    /*
     * PtypMultipleInteger32
     * 
     * 0x1003, %x03.10
     * 
     * Variable size; a COUNT field followed by
     * that many PtypInteger32 values.
     * 
     * PT_MV_LONG, PT_MV_I4, mv.i4
     */
    public static PropertyType<int[]> PtypMultipleInteger32 = new PropertyType<int[]>("PtypTime", int[].class, Size.VARIABLE, (short) 0x1003) {
        @Override
        public int[] loadVariableSize(PstIo io) {
            io.seek(0);
            int count = io.readDw();
            int[] answer = new int[count];
            for(int i = 0; i < count; i++) {
                answer[i] = io.readDw(); 
            }
            io.assertExhausted();
            return answer;
        }  
    };
    
    /*
     * 
     * PtypBinary
     * 0x0102, %x02.01
     * Variable size; a COUNT field followed by that many bytes.
     * PT_BINARY
     */
    public static PropertyType<Bytes> PtypBinary = new PropertyType<Bytes>("PtypBinary", Bytes.class, Size.VARIABLE, (short)  0x0102) {
        @Override
        public Bytes loadVariableSize(PstIo io) {
            io.seek(0);
            if(io.size() > Integer.MAX_VALUE) {
                throw new IllegalStateException();
            }
            Bytes answer = new Bytes(io.read((int) io.size()));
            io.assertExhausted();
            return answer;
        }
        
    };
    
    /*
     * 2.11.1.2 String Property Values
     * Clients SHOULD use string properties in Unicode format. When using strings in Unicode 
     * format, string data MUST be encoded as UTF-16LE format, and property data types MUST 
     * be specified as 0x001F (PtypString) or 0x101F (PtypMultipleString).
     * Clients can use PtypString8 and PtypMultipleString8 properties in a specific 8-bit or 
     * MBCS code page. If they do, property data types MUST be specified as
     *  0x001E (PtypString8) or 0x101E (PtypMultipleString8).
     * In requests sent to a message store server, the code page of strings 
     * MUST match the code page sent to the server in an EcDoConnectEx method call, 
     * as specified in [MS-OXCRPC] section 3.1.4.1, or sent to the server
     *  using the Connect request type<7>, as specified in [MS-OXCMAPIHTTP] 
     *  section 2.2.4.1. Address book server-side rules for working with 
     *  PtypString8 properties are somewhat more involved and are specified in
     *   [MS-NSPI]. 
     */
    public static PropertyType<String> PtypString = new PropertyType<String>("PtypString", String.class, Size.VARIABLE, (short) 0x001F) {
        @Override
        public String loadVariableSize(PstIo io) {
            io.seek(0);
            if(io.size() > Integer.MAX_VALUE) {
                throw new IllegalStateException();
            }
            byte[] contents = io.read((int) io.size());
            return new String(contents, StandardCharsets.UTF_16LE);
        }
    };
    
    
    
    public static PropertyType<?> fromCode(short code) {
        return lookup.computeIfAbsent(code, __ -> {
            throw new IllegalStateException("not found:" + code);
        });
    }
    
    private final String name;
    private final Class<T> type;
    private final Size size;
    private final short code;
    
    private PropertyType(String name, Class<T> type, Size size, short code) {
        this.type = type;
        this.size = size;
        this.code = code;
        this.name = name;
        lookup.merge(code, this, (l, r) -> {
           throw new IllegalStateException("duplicate code:" + code); 
        });
    }

    public String getName() {
        return name;
    }
    
    public Class<T> getType() {
        return type;
    }

    public Size getSize() {
        return size;
    }
    
    public int getCode() {
        return code;
    }
    
    public void assertRightCode(byte b1, byte b2) {
        short otherCode = ByteUtils.bytesToShort(b1, b2);
        if(otherCode != code) {
            throw new IllegalStateException("wrong code got:" + ByteUtils.shortToHex(otherCode) + " expected:" + ByteUtils.shortToHex(code) + " this:" + this);
        }
    }

    @Override
    public String toString() {
        return "PropertyType [name=" + name + ", type=" + type.getSimpleName() + ", size="
                + size + ", code=" + ByteUtils.shortToHex(code) + "]";
    }
    
    public T loadVariableSize(PstIo io) {
        throw new IllegalStateException("not variable:" + this);
    }
    
    /**
     * note contents may be sized correctly (loaded from a TC), or it may be 4 bytes if we are 
     * loaded from a PC 
     */
    public T loadFixed(byte[] contents) {
        throw new IllegalStateException("not fixed:" + this);
    }
    
    
}


