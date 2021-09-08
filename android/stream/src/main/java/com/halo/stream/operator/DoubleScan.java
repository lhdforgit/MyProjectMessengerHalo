/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.DoubleBinaryOperator;
import com.halo.stream.iterator.PrimitiveExtIterator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class DoubleScan extends PrimitiveExtIterator.OfDouble {

    private final PrimitiveIterator.OfDouble iterator;
    private final DoubleBinaryOperator accumulator;

    public DoubleScan(
            @NotNull PrimitiveIterator.OfDouble iterator,
            @NotNull DoubleBinaryOperator accumulator) {
        this.iterator = iterator;
        this.accumulator = accumulator;
    }

    @Override
    protected void nextIteration() {
        hasNext = iterator.hasNext();
        if (hasNext) {
            final double current = iterator.nextDouble();
            if (isInit) {
                next = accumulator.applyAsDouble(next, current);
            } else {
                next = current;
            }
        }
    }
}
