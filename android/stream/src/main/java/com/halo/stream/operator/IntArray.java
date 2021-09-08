/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class IntArray extends PrimitiveIterator.OfInt {

    private final int[] values;
    private int index;

    public IntArray(@NotNull int[] values) {
        this.values = values;
        index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < values.length;
    }
    
    @Override
    public int nextInt() {
        return values[index++];
    }
}
