/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.iterator.LsaIterator;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ObjSkip<T> extends LsaIterator<T> {

    private final Iterator<? extends T> iterator;
    private final long n;
    private long skipped;

    public ObjSkip(@NotNull Iterator<? extends T> iterator, long n) {
        this.iterator = iterator;
        this.n = n;
        skipped = 0;
    }

    @Override
    public boolean hasNext() {
        while (skipped < n) {
            if (!iterator.hasNext()) {
                return false;
            }
            iterator.next();
            skipped++;
        }
        return iterator.hasNext();
    }

    @Override
    public T nextIteration() {
        return iterator.next();
    }
}
