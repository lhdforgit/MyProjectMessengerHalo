/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.LongUnaryOperator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class LongMap extends PrimitiveIterator.OfLong {

    private final PrimitiveIterator.OfLong iterator;
    private final LongUnaryOperator mapper;

    public LongMap(
            @NotNull PrimitiveIterator.OfLong iterator,
            @NotNull LongUnaryOperator mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public long nextLong() {
        return mapper.applyAsLong(iterator.nextLong());
    }
}
