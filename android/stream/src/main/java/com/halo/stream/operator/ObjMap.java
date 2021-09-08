/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.Function;
import com.halo.stream.iterator.LsaIterator;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ObjMap<T, R> extends LsaIterator<R> {

    private final Iterator<? extends T> iterator;
    private final Function<? super T, ? extends R> mapper;

    public ObjMap(
            @NotNull Iterator<? extends T> iterator,
            @NotNull Function<? super T, ? extends R> mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public R nextIteration() {
        return mapper.apply(iterator.next());
    }
}
