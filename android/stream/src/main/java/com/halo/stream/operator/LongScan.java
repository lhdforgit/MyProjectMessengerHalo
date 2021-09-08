/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.LongBinaryOperator;
import com.halo.stream.iterator.PrimitiveExtIterator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class LongScan extends PrimitiveExtIterator.OfLong {

    private final PrimitiveIterator.OfLong iterator;
    private final LongBinaryOperator accumulator;

    public LongScan(
            @NotNull PrimitiveIterator.OfLong iterator,
            @NotNull LongBinaryOperator accumulator) {
        this.iterator = iterator;
        this.accumulator = accumulator;
    }

    @Override
    protected void nextIteration() {
        hasNext = iterator.hasNext();
        if (hasNext) {
            final long current = iterator.nextLong();
            if (isInit) {
                next = accumulator.applyAsLong(next, current);
            } else {
                next = current;
            }
        }
    }
}
