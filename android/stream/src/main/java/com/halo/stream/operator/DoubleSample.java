/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class DoubleSample extends PrimitiveIterator.OfDouble {

    private final PrimitiveIterator.OfDouble iterator;
    private final int stepWidth;

    public DoubleSample(@NotNull PrimitiveIterator.OfDouble iterator, int stepWidth) {
        this.iterator = iterator;
        this.stepWidth = stepWidth;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public double nextDouble() {
        final double result = iterator.nextDouble();
        int skip = 1;
        while (skip < stepWidth && iterator.hasNext()) {
            iterator.nextDouble();
            skip++;
        }
        return result;
    }
}
