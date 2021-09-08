/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.LongUnaryOperator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class LongIterate extends PrimitiveIterator.OfLong {

    private final LongUnaryOperator op;
    private long current;

    public LongIterate(long seed, @NotNull LongUnaryOperator f) {
        this.op = f;
        current = seed;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public long nextLong() {
        final long old = current;
        current = op.applyAsLong(current);
        return old;
    }
}
