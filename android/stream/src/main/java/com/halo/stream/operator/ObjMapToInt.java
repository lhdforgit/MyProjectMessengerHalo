/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.ToIntFunction;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ObjMapToInt<T> extends PrimitiveIterator.OfInt {

    private final Iterator<? extends T> iterator;
    private final ToIntFunction<? super T> mapper;

    public ObjMapToInt(
            @NotNull Iterator<? extends T> iterator,
            @NotNull ToIntFunction<? super T> mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public int nextInt() {
        return mapper.applyAsInt(iterator.next());
    }
}
