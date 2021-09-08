/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class DoubleArray extends PrimitiveIterator.OfDouble {

    private final double[] values;
    private int index;

    public DoubleArray(@NotNull double[] values) {
        this.values = values;
        index = 0;
    }

    @Override
    public double nextDouble() {
        return values[index++];
    }

    @Override
    public boolean hasNext() {
        return index < values.length;
    }
}
