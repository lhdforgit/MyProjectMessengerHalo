/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.IntToLongFunction;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class IntMapToLong extends PrimitiveIterator.OfLong {

    private final PrimitiveIterator.OfInt iterator;
    private final IntToLongFunction mapper;

    public IntMapToLong(
            @NotNull PrimitiveIterator.OfInt iterator,
            @NotNull IntToLongFunction mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public long nextLong() {
        return mapper.applyAsLong(iterator.nextInt());
    }
}
