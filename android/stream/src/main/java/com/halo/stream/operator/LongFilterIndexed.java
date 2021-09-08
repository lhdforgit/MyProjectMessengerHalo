/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.IndexedLongPredicate;
import com.halo.stream.iterator.PrimitiveIndexedIterator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

public class LongFilterIndexed extends PrimitiveIterator.OfLong {

    private final PrimitiveIndexedIterator.OfLong iterator;
    private final IndexedLongPredicate predicate;
    private boolean hasNext, hasNextEvaluated;
    private long next;

    public LongFilterIndexed(
            @NotNull PrimitiveIndexedIterator.OfLong iterator,
            @NotNull IndexedLongPredicate predicate) {
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
    public long nextLong() {
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
