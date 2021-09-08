/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.IndexedDoubleUnaryOperator;
import com.halo.stream.iterator.PrimitiveIndexedIterator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class DoubleMapIndexed extends PrimitiveIterator.OfDouble {

    private final PrimitiveIndexedIterator.OfDouble iterator;
    private final IndexedDoubleUnaryOperator mapper;

    public DoubleMapIndexed(
            @NotNull PrimitiveIndexedIterator.OfDouble iterator,
            @NotNull IndexedDoubleUnaryOperator mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public double nextDouble() {
        return mapper.applyAsDouble(iterator.getIndex(), iterator.next());
    }
}
