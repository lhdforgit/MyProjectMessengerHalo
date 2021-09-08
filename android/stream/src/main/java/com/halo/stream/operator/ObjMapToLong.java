/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.ToLongFunction;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ObjMapToLong<T> extends PrimitiveIterator.OfLong {

    private final Iterator<? extends T> iterator;
    private final ToLongFunction<? super T> mapper;

    public ObjMapToLong(
            @NotNull Iterator<? extends T> iterator,
            @NotNull ToLongFunction<? super T> mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public long nextLong() {
        return mapper.applyAsLong(iterator.next());
    }
}
