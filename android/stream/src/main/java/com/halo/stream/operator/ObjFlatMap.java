/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.Stream;
import com.halo.stream.function.Function;
import com.halo.stream.iterator.LsaExtIterator;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ObjFlatMap<T, R> extends LsaExtIterator<R> {

    private final Iterator<? extends T> iterator;
    private final Function<? super T, ? extends Stream<? extends R>> mapper;
    private Iterator<? extends R> inner;
    private Stream<? extends R> innerStream;

    public ObjFlatMap(
            @NotNull Iterator<? extends T> iterator,
            @NotNull Function<? super T, ? extends Stream<? extends R>> mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    protected void nextIteration() {
        if ((inner != null) && inner.hasNext()) {
            next = inner.next();
            hasNext = true;
            return;
        }
        while (iterator.hasNext()) {
            if (inner == null || !inner.hasNext()) {
                if (innerStream != null) {
                    innerStream.close();
                    innerStream = null;
                }
                final T arg = iterator.next();
                final Stream<? extends R> result = mapper.apply(arg);
                if (result != null) {
                    inner = result.iterator();
                    innerStream = result;
                }
            }
            if ((inner != null) && inner.hasNext()) {
                next = inner.next();
                hasNext = true;
                return;
            }
        }
        hasNext = false;
        if (innerStream != null) {
            innerStream.close();
            innerStream = null;
        }
    }
}
