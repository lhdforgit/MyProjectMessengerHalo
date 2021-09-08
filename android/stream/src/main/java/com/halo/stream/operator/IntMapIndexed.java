/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.IntBinaryOperator;
import com.halo.stream.iterator.PrimitiveIndexedIterator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class IntMapIndexed extends PrimitiveIterator.OfInt {

    private final PrimitiveIndexedIterator.OfInt iterator;
    private final IntBinaryOperator mapper;

    public IntMapIndexed(
            @NotNull PrimitiveIndexedIterator.OfInt iterator,
            @NotNull IntBinaryOperator mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public int nextInt() {
        return mapper.applyAsInt(iterator.getIndex(), iterator.next());
    }
}
