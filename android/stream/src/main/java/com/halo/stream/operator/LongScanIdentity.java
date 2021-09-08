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

public class LongScanIdentity extends PrimitiveExtIterator.OfLong {

    private final PrimitiveIterator.OfLong iterator;
    private final long identity;
    private final LongBinaryOperator accumulator;

    public LongScanIdentity(
            @NotNull PrimitiveIterator.OfLong iterator,
            long identity,
            @NotNull LongBinaryOperator accumulator) {
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
            final long current = iterator.next();
            next = accumulator.applyAsLong(next, current);
        }
    }
}
