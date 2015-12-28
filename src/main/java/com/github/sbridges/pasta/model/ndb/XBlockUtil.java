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

import java.util.Iterator;

import com.github.sbridges.pasta.model.BID;

public class XBlockUtil {
    
    /**
     * get the index'th data block from this tree.
     * 
     * rootBid may point to a datablock, an xblock, or an xxblock
     */
    public static DataBlock getDataBlock(
            int index, 
            BID rootBid,
            BBT bbt
            ) {
        DataBlock dataBlock = bbt.load(rootBid);
        
        if(!rootBid.isInternal()) {
            if(index != 0) {
                throw new IllegalStateException("not internal, but asking for non 0 block? index:" + index + " rootBid:" + rootBid);
            }
            return dataBlock;
        }
        
        if(dataBlock.isXBLock()) {
            Iterator<BID> bidIter = new XBlock(dataBlock).getXBlockBids().iterator();
            for(int i =0; i < index; i++) {
                bidIter.next();
            }
            BID bid = bidIter.next();
            if(bid.isInternal()) {
                throw new IllegalStateException("internal?");
            }
            DataBlock answer = bbt.load(bid);
            //all blocks but the last must be of maximum size
            if(bidIter.hasNext() && answer.getData().size() != 8192 - BlockTrailer.SIZE) {
                throw new IllegalStateException();
            }
            return answer;
        }
        
        if(dataBlock.isXXBLock()) {
            int remaining = index;
            Iterator<BID> xxBlockIter = new XBlock(dataBlock).getXBlockBids().iterator();
            while(xxBlockIter.hasNext()) {
                BID xBid = xxBlockIter.next();
                if(!xBid.isInternal()) {
                    throw new IllegalStateException("not internal?:" + xBid);
                }
                DataBlock xBlockDataBlock = bbt.load(xBid);
                if(!xBlockDataBlock.isXBLock()) {
                    throw new IllegalStateException();
                }
                XBlock xBlock = new XBlock(xBlockDataBlock);
                Iterator<BID> xBlockIter = xBlock.getXBlockBids().iterator();
                while(remaining > 0 && xBlockIter.hasNext()) {
                    remaining--;
                }
                if(xBlockIter.hasNext()) {
                    if(remaining !=0) {
                        throw new IllegalStateException();
                    }
                    DataBlock answer = bbt.load(xBlockIter.next());
                    //all blocks but the last must be of maximum size
                    if((xBlockIter.hasNext() || xxBlockIter.hasNext() ) && answer.getData().size() != 8192 - BlockTrailer.SIZE) {
                        throw new IllegalStateException();
                    }
                    return answer;
                }
            }
        }
        
        if(dataBlock.isSLBLock()) {
            Iterator<SLEntry> entriesIter = new SLBlock(dataBlock).getSLEntries().iterator();
            for(int i =0; i < index; i++) {
                entriesIter.next();
            }
            SLEntry entry = entriesIter.next();
            return bbt.load(entry.getBidData());
        }
        
        
        if(dataBlock.isSIBLock()) {
            int remaining = index;
            Iterator<SIEntry> siIter = new SIBlock(dataBlock).getSLEntries().iterator();
            while(siIter.hasNext()) {
                SIEntry siEntry = siIter.next();
                if(!siEntry.getBid().isInternal()) {
                    throw new IllegalStateException("not internal?:" + siEntry);
                }
                DataBlock slBlock = bbt.load(siEntry.getBid());
                if(!slBlock.isSLBLock()) {
                    throw new IllegalStateException();
                }
                SLBlock sl = new SLBlock(slBlock);
                Iterator<SLEntry> slIter = sl.getSLEntries().iterator();
                while(remaining > 0 && slIter.hasNext()) {
                    remaining--;
                }
                if(slIter.hasNext()) {
                    if(remaining !=0) {
                        throw new IllegalStateException();
                    }
                    DataBlock answer = bbt.load(slIter.next().getBidData());
                    //all blocks but the last must be of maximum size
                    if((slIter.hasNext() || slIter.hasNext() ) && answer.getData().size() != 8192 - BlockTrailer.SIZE) {
                        throw new IllegalStateException();
                    }
                    return answer;
                }
            }
        }
        
        throw new IllegalStateException("not found:" + index + " bid:" + rootBid);
        
        
    }
}
