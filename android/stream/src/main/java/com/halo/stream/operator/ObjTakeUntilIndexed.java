/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.IndexedPredicate;
import com.halo.stream.iterator.IndexedIterator;
import com.halo.stream.iterator.LsaExtIterator;

import org.jetbrains.annotations.NotNull;

public class ObjTakeUntilIndexed<T> extends LsaExtIterator<T> {

    private final IndexedIterator<? extends T> iterator;
    private final IndexedPredicate<? super T> stopPredicate;

    public ObjTakeUntilIndexed(
            @NotNull IndexedIterator<? extends T> iterator,
            @NotNull IndexedPredicate<? super T> predicate) {
        this.iterator = iterator;
        this.stopPredicate = predicate;
    }

    @Override
    protected void nextIteration() {
        hasNext = iterator.hasNext() && !(isInit && stopPredicate.test(iterator.getIndex(), next));
        if (hasNext) {
            next = iterator.next();
        }
    }
}
