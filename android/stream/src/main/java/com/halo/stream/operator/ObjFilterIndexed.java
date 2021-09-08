/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.IndexedPredicate;
import com.halo.stream.iterator.IndexedIterator;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ObjFilterIndexed<T> implements Iterator<T> {

    private final IndexedIterator<? extends T> iterator;
    private final IndexedPredicate<? super T> predicate;
    private boolean hasNext, hasNextEvaluated;
    private T next;

    public ObjFilterIndexed(
            @NotNull IndexedIterator<? extends T> iterator,
            @NotNull IndexedPredicate<? super T> predicate) {
        this.iterator = iterator;
        this.predicate = predicate;
    }

    @Override
    public boolean hasNext() {
        if (!hasNextEvaluated) {
            nextIteration();
            hasNextEvaluated = true;
        }
        return hasNext;
    }

    @Override
    public T next() {
        if (!hasNextEvaluated) {
            hasNext = hasNext();
        }
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        hasNextEvaluated = false;
        return next;
    }

    private void nextIteration() {
        while (iterator.hasNext()) {
            final int index = iterator.getIndex();
            next = iterator.next();
            if (predicate.test(index, next)) {
                hasNext = true;
                return;
            }
        }
        hasNext = false;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove not supported");
    }
}
