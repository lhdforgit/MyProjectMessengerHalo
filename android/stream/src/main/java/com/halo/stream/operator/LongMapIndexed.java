/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.IndexedLongUnaryOperator;
import com.halo.stream.iterator.PrimitiveIndexedIterator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class LongMapIndexed extends PrimitiveIterator.OfLong {

    private final PrimitiveIndexedIterator.OfLong iterator;
    private final IndexedLongUnaryOperator mapper;

    public LongMapIndexed(
            @NotNull PrimitiveIndexedIterator.OfLong iterator,
            @NotNull IndexedLongUnaryOperator mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public long nextLong() {
        return mapper.applyAsLong(iterator.getIndex(), iterator.next());
    }
}
