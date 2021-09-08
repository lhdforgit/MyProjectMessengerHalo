/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.IntUnaryOperator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class IntMap extends PrimitiveIterator.OfInt {

    private final PrimitiveIterator.OfInt iterator;
    private final IntUnaryOperator mapper;

    public IntMap(
            @NotNull PrimitiveIterator.OfInt iterator,
            @NotNull IntUnaryOperator mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public int nextInt() {
        return mapper.applyAsInt(iterator.nextInt());
    }
}
