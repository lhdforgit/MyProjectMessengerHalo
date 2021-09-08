/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.Predicate;
import com.halo.stream.iterator.LsaExtIterator;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ObjDropWhile<T> extends LsaExtIterator<T> {

    private final Iterator<? extends T> iterator;
    private final Predicate<? super T> predicate;

    public ObjDropWhile(
            @NotNull Iterator<? extends T> iterator,
            @NotNull Predicate<? super T> predicate) {
        this.iterator = iterator;
        this.predicate = predicate;
    }

    @Override
    protected void nextIteration() {
        if (!isInit) {
            // Skip first time
            while (hasNext = iterator.hasNext()) {
                next = iterator.next();
                if (!predicate.test(next)) {
                    return;
                }
            }
        }

        hasNext = hasNext && iterator.hasNext();
        if (!hasNext) return;

        next = iterator.next();
    }
}
