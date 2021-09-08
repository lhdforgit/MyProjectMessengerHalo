/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class LongLimit extends PrimitiveIterator.OfLong {

    private final PrimitiveIterator.OfLong iterator;
    private final long maxSize;
    private long index;

    public LongLimit(@NotNull PrimitiveIterator.OfLong iterator, long maxSize) {
        this.iterator = iterator;
        this.maxSize = maxSize;
        index = 0;
    }

    @Override
    public boolean hasNext() {
        return (index < maxSize) && iterator.hasNext();
    }

    @Override
    public long nextLong() {
        index++;
        return iterator.nextLong();
    }
}
