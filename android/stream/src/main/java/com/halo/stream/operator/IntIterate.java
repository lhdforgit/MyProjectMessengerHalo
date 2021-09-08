/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.IntUnaryOperator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class IntIterate extends PrimitiveIterator.OfInt {

    private final IntUnaryOperator op;
    private int current;

    public IntIterate(int seed, @NotNull IntUnaryOperator f) {
        this.op = f;
        current = seed;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public int nextInt() {
        final int old = current;
        current = op.applyAsInt(current);
        return old;
    }
}
