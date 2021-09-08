/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.DoubleFunction;
import com.halo.stream.iterator.LsaIterator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class DoubleMapToObj<R> extends LsaIterator<R> {

    private final PrimitiveIterator.OfDouble iterator;
    private final DoubleFunction<? extends R> mapper;

    public DoubleMapToObj(
            @NotNull PrimitiveIterator.OfDouble iterator,
            @NotNull DoubleFunction<? extends R> mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public R nextIteration() {
        return mapper.apply(iterator.nextDouble());
    }
}
