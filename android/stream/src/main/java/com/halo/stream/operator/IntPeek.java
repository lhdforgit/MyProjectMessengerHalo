/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.IntConsumer;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class IntPeek extends PrimitiveIterator.OfInt {

    private final PrimitiveIterator.OfInt iterator;
    private final IntConsumer action;

    public IntPeek(
            @NotNull PrimitiveIterator.OfInt iterator,
            @NotNull IntConsumer action) {
        this.iterator = iterator;
        this.action = action;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public int nextInt() {
        final int value = iterator.nextInt();
        action.accept(value);
        return value;
    }
}
