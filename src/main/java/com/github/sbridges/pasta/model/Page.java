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

import com.github.sbridges.pasta.io.PstIo;

/**
 * 2.2.2.7
 * 
 * A page is a fixed-size structure of 512 bytes that is used in the
 *  NDB Layer to represent allocation metadata and BTree data structures. 
 *  A page trailer is placed at the very end of every page such that 
 *  the end of the page trailer is aligned with the end of the page. 
 *  
 */
public class Page {

    private final long ib;
    private final PstIo slice;
    private final PageTrailer pageTrailer;
    
    public Page(BRef ref, PstIo io) {
        this(io.slice(ref.getIb(), 512), ref.getIb());
    }
    
    public Page(PstIo slice, long ib) {
        if(slice.size() != 512) {
            throw new IllegalStateException("invalid page size:" + slice.size());
        }
        this.slice = slice;
        this.ib = ib;
        
        this.pageTrailer = new PageTrailer(slice, ib);
    }

    public PstIo getSlice() {
        return slice;
    }

    public PageTrailer getPageTrailer() {
        return pageTrailer;
    }
    
    @Override
    public String toString() {
        return "Page[ib:" + ib + " trailer:" + pageTrailer + "]";
    }
    
    
}
