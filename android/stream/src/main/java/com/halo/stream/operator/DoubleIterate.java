/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.DoubleUnaryOperator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class DoubleIterate extends PrimitiveIterator.OfDouble {

    private final DoubleUnaryOperator op;
    private double current;

    public DoubleIterate(double seed, @NotNull DoubleUnaryOperator f) {
        this.op = f;
        current = seed;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public double nextDouble() {
        final double old = current;
        current = op.applyAsDouble(current);
        return old;
    }
}
