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

/**
 * 2.3.3.3 PC BTH Record
 */
public class PCBTHRecord {

    //wPropId (2 bytes): Property ID, as specified in [MS-OXCDATA] section 2.10.
    //This is the upper 16 bits of the property tag value. This is a manifestation 
    //of the BTH record (section 2.3.2.3) and constitutes the key of this record.
    private short wPropId;
    
    //wPropType (2 bytes): Property type. This is the lower 16 bits of the property 
    //tag value, which identifies the type of data that is associated with the property.
    //The complete list of property type values and their data sizes are specified
    //in [MS-OXCDATA] section 2.12.1.
    private short wPropType;
    
    //dwValueHnid (4 bytes): Depending on the data size of the property type indicated 
    //by wPropType and a few other factors, this field represents different values. 
    //The following table explains the value contained in dwValueHnid based on 
    //the different scenarios. In the event where the dwValueHnid value contains 
    //a HID or NID (section 2.3.3.2), the actual data is stored in the corresponding 
    //heap or subnode entry, respectively.
    private HNID dwValueHnid;
    
    
    
}
