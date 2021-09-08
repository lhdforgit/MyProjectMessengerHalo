/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.LongPredicate;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

public class LongFilter extends PrimitiveIterator.OfLong {

    private final PrimitiveIterator.OfLong iterator;
    private final LongPredicate predicate;
    private boolean hasNext, hasNextEvaluated;
    private long next;

    public LongFilter(
            @NotNull PrimitiveIterator.OfLong iterator,
            @NotNull LongPredicate predicate) {
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
            next = iterator.nextLong();
            if (predicate.test(next)) {
                hasNext = true;
                return;
            }
        }
        hasNext = false;
    }
}
