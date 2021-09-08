/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.IntPredicate;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

public class IntFilter extends PrimitiveIterator.OfInt {

    private final PrimitiveIterator.OfInt iterator;
    private final IntPredicate predicate;
    private boolean hasNext, hasNextEvaluated;
    private int next;

    public IntFilter(
            @NotNull PrimitiveIterator.OfInt iterator,
            @NotNull IntPredicate predicate) {
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
    public int nextInt() {
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
            next = iterator.nextInt();
            if (predicate.test(next)) {
                hasNext = true;
                return;
            }
        }
        hasNext = false;
    }
}
