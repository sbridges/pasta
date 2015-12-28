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

package com.github.sbridges.pasta.util;

/**
 * The following is the algorithm to calculate the signature of a block. 
 * The signature is calculated by first obtaining the DWORD XOR result
 * between the absolute file offset of the block and its BID. 
 * The WORD signature is then obtained by obtaining the XOR result between 
 * the higher and lower 16 bits of the DWORD obtained previously.
 *
 */
public class BlockSignature {

    /**
     * Note - this returns a signed short, if you compare this with
     * PstIo#readW() you will have to do pstIo.readW() == 0xffff & BlockSignature.compute(...)  
     */
    public static short compute(long ib, long bid) {
        //c code
        //        WORD ComputeSig(IB ib, BID bid)
        //        {
        //          ib ^= bid;
        //          return(WORD(WORD(ib >> 16) ^ WORD(ib))); }
        //        }
        
        ib ^= bid;
        return (short) (ib >>> 16 ^ ib);
    }
}
