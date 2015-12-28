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

import java.util.Optional;
import java.util.function.Consumer;

import com.github.sbridges.pasta.io.PstIo;
import com.github.sbridges.pasta.model.Page;

/**
 * Utils for BTree's, callers should typically use the methods on BBT or NBT instead
 */
class BTreeUtil {
    
    private BTreeUtil() {}
    
    public static void walkDepthFirst(BTPage btPage, PstIo io, Consumer<BTPage> consumer) {
        consumer.accept(btPage);
        
        if(!btPage.isLeaf()) {
          for(BTEntry entry : btPage.getBTEntries()) {
              Page childPage = new Page(entry.getbRef(), io); 
              BTPage childBtPage = new BTPage(childPage);
              consumer.accept(childBtPage);
              walkDepthFirst(childBtPage, io, consumer);
          }  
        }
    }
    
    
    public static String debugString(BTPage page, PstIo io) {
        StringBuilder sb = new StringBuilder();
        debugString(sb, io, page, 0);
        return sb.toString();
        
    }
    
    public static Optional<BTPage> findLeaf(long key, BTPage page, PstIo io) {
        if(page.isLeaf()) {
            return Optional.of(page);
        }
        BTEntry last = null;
        for(BTEntry e : page.getBTEntries()) {
            if(e.getBtkey() > key) {
                break;
            }
            last = e;
        }
        if(last == null) {
            return Optional.empty();
        } else {
            return findLeaf(key, new BTPage(new Page(last.getbRef(), io)), io);
        }
        
    }

    private static void debugString(StringBuilder sb, PstIo io, BTPage page, int depth) {

        
        if(page.isLeaf()) {
            if(page.isBBT()) {
                for(BBTEntry e : page.getBBTEntries()) {
                    indent(sb, depth);
                    sb.append(e);
                    sb.append("\n");
                }
            } else if(page.isNBT()) {
                for(NBTEntry e : page.getNBTEntries()) {
                    indent(sb, depth);
                    sb.append(e);
                    sb.append("\n");
                }
            }
        } else {
            indent(sb, depth);
            sb.append("\n");
            for(BTEntry e : page.getBTEntries()) {
                indent(sb, depth);
                sb.append(e);
                sb.append("\n");
                debugString(
                        sb, io, new BTPage(new Page(e.getbRef(), io)), depth + 1
                        );
            }
        }
    }
    
    private static void indent(StringBuilder sb, int depth) {
        for(int i = 0; i < depth; i++) {
            sb.append("  ");
        }
    }
}
