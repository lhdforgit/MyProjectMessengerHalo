/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.iterator.LsaIterator;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ObjLimit<T> extends LsaIterator<T> {

    private final Iterator<? extends T> iterator;
    private final long maxSize;
    private long index;

    public ObjLimit(
            @NotNull Iterator<? extends T> iterator, long maxSize) {
        this.iterator = iterator;
        this.maxSize = maxSize;
        index = 0;
    }

    @Override
    public boolean hasNext() {
        return (index < maxSize) && iterator.hasNext();
    }

    @Override
    public T nextIteration() {
        index++;
        return iterator.next();
    }
}
