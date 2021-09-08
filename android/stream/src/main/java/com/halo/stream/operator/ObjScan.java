/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.BiFunction;
import com.halo.stream.iterator.LsaExtIterator;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ObjScan<T> extends LsaExtIterator<T> {

    private final Iterator<? extends T> iterator;
    private final BiFunction<T, T, T> accumulator;

    public ObjScan(
            @NotNull Iterator<? extends T> iterator,
            @NotNull BiFunction<T, T, T> accumulator) {
        this.iterator = iterator;
        this.accumulator = accumulator;
    }

    @Override
    protected void nextIteration() {
        hasNext = iterator.hasNext();
        if (hasNext) {
            final T value = iterator.next();
            if (isInit) {
                next = accumulator.apply(next, value);
            } else {
                next = value;
            }
        }
    }
}
