/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.IntPredicate;
import com.halo.stream.iterator.PrimitiveExtIterator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class IntTakeUntil extends PrimitiveExtIterator.OfInt {

    private final PrimitiveIterator.OfInt iterator;
    private final IntPredicate stopPredicate;

    public IntTakeUntil(
            @NotNull PrimitiveIterator.OfInt iterator,
            @NotNull IntPredicate stopPredicate) {
        this.iterator = iterator;
        this.stopPredicate = stopPredicate;
    }

    @Override
    protected void nextIteration() {
        hasNext = iterator.hasNext() && !(isInit && stopPredicate.test(next));
        if (hasNext) {
            next = iterator.next();
        }
    }
}
