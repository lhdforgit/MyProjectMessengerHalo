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

public class ObjTakeWhileIndexed<T> extends LsaExtIterator<T> {

    private final IndexedIterator<? extends T> iterator;
    private final IndexedPredicate<? super T> predicate;

    public ObjTakeWhileIndexed(
            @NotNull IndexedIterator<? extends T> iterator,
            @NotNull IndexedPredicate<? super T> predicate) {
        this.iterator = iterator;
        this.predicate = predicate;
    }

    @Override
    protected void nextIteration() {
        hasNext = iterator.hasNext()
                && predicate.test(iterator.getIndex(), next = iterator.next());
    }
}
