/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.IntToDoubleFunction;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class IntMapToDouble extends PrimitiveIterator.OfDouble {

    private final PrimitiveIterator.OfInt iterator;
    private final IntToDoubleFunction mapper;

    public IntMapToDouble(
            @NotNull PrimitiveIterator.OfInt iterator,
            @NotNull IntToDoubleFunction mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public double nextDouble() {
        return mapper.applyAsDouble(iterator.nextInt());
    }
}
