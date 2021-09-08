/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.ToDoubleFunction;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ObjMapToDouble<T> extends PrimitiveIterator.OfDouble {

    private final Iterator<? extends T> iterator;
    private final ToDoubleFunction<? super T> mapper;

    public ObjMapToDouble(
            @NotNull Iterator<? extends T> iterator,
            @NotNull ToDoubleFunction<? super T> mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public double nextDouble() {
        return mapper.applyAsDouble(iterator.next());
    }
}
