/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class IntSample extends PrimitiveIterator.OfInt {

    private final PrimitiveIterator.OfInt iterator;
    private final int stepWidth;

    public IntSample(@NotNull PrimitiveIterator.OfInt iterator, int stepWidth) {
        this.iterator = iterator;
        this.stepWidth = stepWidth;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public int nextInt() {
        final int result = iterator.nextInt();
        int skip = 1;
        while (skip < stepWidth && iterator.hasNext()) {
            iterator.nextInt();
            skip++;
        }
        return result;
    }
}
