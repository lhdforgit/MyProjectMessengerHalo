/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class DoubleLimit extends PrimitiveIterator.OfDouble {

    private final PrimitiveIterator.OfDouble iterator;
    private final long maxSize;
    private long index;

    public DoubleLimit(@NotNull PrimitiveIterator.OfDouble iterator, long maxSize) {
        this.iterator = iterator;
        this.maxSize = maxSize;
        index = 0;
    }

    @Override
    public boolean hasNext() {
        return (index < maxSize) && iterator.hasNext();
    }

    @Override
    public double nextDouble() {
        index++;
        return iterator.nextDouble();
    }
}
