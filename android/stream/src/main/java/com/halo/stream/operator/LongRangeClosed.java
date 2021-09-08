/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.iterator.PrimitiveIterator;

public class LongRangeClosed extends PrimitiveIterator.OfLong {

    private final long endInclusive;
    private long current;
    private boolean hasNext;

    public LongRangeClosed(long startInclusive, long endInclusive) {
        this.endInclusive = endInclusive;
        current = startInclusive;
        hasNext = current <= endInclusive;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public long nextLong() {
        if (current >= endInclusive) {
            hasNext = false;
            return endInclusive;
        }
        return current++;
    }
}
