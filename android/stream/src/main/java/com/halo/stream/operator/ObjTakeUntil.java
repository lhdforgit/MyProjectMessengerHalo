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

public class ObjTakeUntil<T> extends LsaExtIterator<T> {

    private final Iterator<? extends T> iterator;
    private final Predicate<? super T> stopPredicate;

    public ObjTakeUntil(
            @NotNull Iterator<? extends T> iterator,
            @NotNull Predicate<? super T> predicate) {
        this.iterator = iterator;
        this.stopPredicate = predicate;
    }

    @Override
    protected void nextIteration() {
        hasNext = iterator.hasNext() && !(isInit && stopPredicate.test(next));
        if (hasNext) {
            next = iterator.next();
        }
    }
}
