/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.IntBinaryOperator;
import com.halo.stream.iterator.PrimitiveExtIterator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class IntScan extends PrimitiveExtIterator.OfInt {

    private final PrimitiveIterator.OfInt iterator;
    private final IntBinaryOperator accumulator;

    public IntScan(
            @NotNull PrimitiveIterator.OfInt iterator,
            @NotNull IntBinaryOperator accumulator) {
        this.iterator = iterator;
        this.accumulator = accumulator;
    }

    @Override
    protected void nextIteration() {
        hasNext = iterator.hasNext();
        if (hasNext) {
            final int current = iterator.next();
            if (isInit) {
                next = accumulator.applyAsInt(next, current);
            } else {
                next = current;
            }
        }
    }
}
