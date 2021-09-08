/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class LongArray extends PrimitiveIterator.OfLong {

    private final long[] values;
    private int index;

    public LongArray(@NotNull long[] values) {
        this.values = values;
        index = 0;
    }

    @Override
    public long nextLong() {
        return values[index++];
    }

    @Override
    public boolean hasNext() {
        return index < values.length;
    }
}
