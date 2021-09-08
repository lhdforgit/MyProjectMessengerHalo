/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.LongSupplier;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class LongGenerate extends PrimitiveIterator.OfLong {

    private final LongSupplier supplier;

    public LongGenerate(@NotNull LongSupplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public long nextLong() {
        return supplier.getAsLong();
    }
}
