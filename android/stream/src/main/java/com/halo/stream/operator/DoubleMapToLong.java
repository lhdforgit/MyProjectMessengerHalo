/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.DoubleToLongFunction;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class DoubleMapToLong extends PrimitiveIterator.OfLong {

    private final PrimitiveIterator.OfDouble iterator;
    private final DoubleToLongFunction mapper;

    public DoubleMapToLong(
            @NotNull PrimitiveIterator.OfDouble iterator,
            @NotNull DoubleToLongFunction mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public long nextLong() {
        return mapper.applyAsLong(iterator.nextDouble());
    }
}
