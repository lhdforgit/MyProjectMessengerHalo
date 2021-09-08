/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.internal.Operators;
import com.halo.stream.iterator.PrimitiveExtIterator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class LongSorted extends PrimitiveExtIterator.OfLong {

    private final PrimitiveIterator.OfLong iterator;
    private int index;
    private long[] array;

    public LongSorted(@NotNull PrimitiveIterator.OfLong iterator) {
        this.iterator = iterator;
        index = 0;
    }

    @Override
    protected void nextIteration() {
        if (!isInit) {
            array = Operators.toLongArray(iterator);
            Arrays.sort(array);
        }
        hasNext = index < array.length;
        if (hasNext) {
            next = array[index++];
        }
    }
}
