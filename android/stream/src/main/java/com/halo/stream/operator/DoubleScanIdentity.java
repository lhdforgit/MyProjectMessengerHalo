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

public class DoubleScanIdentity extends PrimitiveExtIterator.OfDouble {

    private final PrimitiveIterator.OfDouble iterator;
    private final double identity;
    private final DoubleBinaryOperator accumulator;

    public DoubleScanIdentity(
            @NotNull PrimitiveIterator.OfDouble iterator,
            double identity,
            @NotNull DoubleBinaryOperator accumulator) {
        this.iterator = iterator;
        this.identity = identity;
        this.accumulator = accumulator;
    }

    @Override
    protected void nextIteration() {
        if (!isInit) {
            // Return identity
            hasNext = true;
            next = identity;
            return;
        }
        hasNext = iterator.hasNext();
        if (hasNext) {
            final double current = iterator.next();
            next = accumulator.applyAsDouble(next, current);
        }
    }
}
