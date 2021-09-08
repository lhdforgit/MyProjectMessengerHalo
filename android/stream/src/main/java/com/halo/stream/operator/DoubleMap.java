/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.DoubleUnaryOperator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class DoubleMap extends PrimitiveIterator.OfDouble {

    private final PrimitiveIterator.OfDouble iterator;
    private final DoubleUnaryOperator mapper;

    public DoubleMap(
            @NotNull PrimitiveIterator.OfDouble iterator,
            @NotNull DoubleUnaryOperator mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public double nextDouble() {
        return mapper.applyAsDouble(iterator.nextDouble());
    }
}
