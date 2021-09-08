/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class LongSample extends PrimitiveIterator.OfLong {

    private final PrimitiveIterator.OfLong iterator;
    private final int stepWidth;

    public LongSample(@NotNull PrimitiveIterator.OfLong iterator, int stepWidth) {
        this.iterator = iterator;
        this.stepWidth = stepWidth;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public long nextLong() {
        final long result = iterator.nextLong();
        int skip = 1;
        while (skip < stepWidth && iterator.hasNext()) {
            iterator.nextLong();
            skip++;
        }
        return result;
    }
}
