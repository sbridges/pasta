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

import java.util.List;
import java.util.function.Function;

//loosly based on
//http://stackoverflow.com/questions/4965335/how-to-print-binary-tree-diagram
public class TreePrinter {

    public static <T> String print(T root, Function<T, List<T>> getChildren,
            Function<T, String> display) {
        StringBuilder builder = new StringBuilder();
        print(builder, "", true, true, root, getChildren, display);
        return builder.toString().trim();
    }

    private static <T> void print(StringBuilder builder, String prefix,
            boolean isTail, boolean isRoot, T node,
            Function<T, List<T>> getChildren, Function<T, String> display) {

        List<T> children = getChildren.apply(node);

        builder.append(prefix);

        if (!isRoot) {
            builder.append(isTail ? "└── " : "├── ");
        }
        builder.append(display.apply(node));
        builder.append("\n");

        String newPrefix = isRoot ? "" : prefix + (isTail ? "    " : "│   ");
        for (int i = 0; i < children.size() - 1; i++) {
            print(builder, newPrefix, false, false, children.get(i),
                    getChildren, display);
        }
        if (children.size() > 0) {
            print(builder, newPrefix, true, false,
                    children.get(children.size() - 1), getChildren, display);
        }
    }
}
