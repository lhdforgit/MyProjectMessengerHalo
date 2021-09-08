/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.LongStream;
import com.halo.stream.function.Function;
import com.halo.stream.iterator.PrimitiveExtIterator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ObjFlatMapToLong<T> extends PrimitiveExtIterator.OfLong {

    private final Iterator<? extends T> iterator;
    private final Function<? super T, ? extends LongStream> mapper;
    private PrimitiveIterator.OfLong inner;

    public ObjFlatMapToLong(
            @NotNull Iterator<? extends T> iterator,
            @NotNull Function<? super T, ? extends LongStream> mapper) {
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
                final T arg = iterator.next();
                final LongStream result = mapper.apply(arg);
                if (result != null) {
                    inner = result.iterator();
                }
            }
            if ((inner != null) && inner.hasNext()) {
                next = inner.next();
                hasNext = true;
                return;
            }
        }
        hasNext = false;
    }
}
