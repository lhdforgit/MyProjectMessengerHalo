/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.IndexedDoublePredicate;
import com.halo.stream.iterator.PrimitiveIndexedIterator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

public class DoubleFilterIndexed extends PrimitiveIterator.OfDouble {

    private final PrimitiveIndexedIterator.OfDouble iterator;
    private final IndexedDoublePredicate predicate;
    private boolean hasNext, hasNextEvaluated;
    private double next;

    public DoubleFilterIndexed(
            @NotNull PrimitiveIndexedIterator.OfDouble iterator,
            @NotNull IndexedDoublePredicate predicate) {
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
    public double nextDouble() {
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
}
