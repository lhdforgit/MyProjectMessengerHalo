/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.IntStream;
import com.halo.stream.function.IntFunction;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

public class IntFlatMap extends PrimitiveIterator.OfInt {

    private final PrimitiveIterator.OfInt iterator;
    private final IntFunction<? extends IntStream> mapper;
    private PrimitiveIterator.OfInt inner;
    private IntStream innerStream;

    public IntFlatMap(
            @NotNull PrimitiveIterator.OfInt iterator,
            @NotNull IntFunction<? extends IntStream> mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        if (inner != null && inner.hasNext()) {
            return true;
        }
        while (iterator.hasNext()) {
            if (innerStream != null) {
                innerStream.close();
                innerStream = null;
            }
            final int arg = iterator.nextInt();
            final IntStream result = mapper.apply(arg);
            if (result == null) {
                continue;
            }
            innerStream = result;
            if (result.iterator().hasNext()) {
                inner = result.iterator();
                return true;
            }
        }
        if (innerStream != null) {
            innerStream.close();
            innerStream = null;
        }
        return false;
    }

    @Override
    public int nextInt() {
        if (inner == null) {
            throw new NoSuchElementException();
        }
        return inner.nextInt();
    }
}
