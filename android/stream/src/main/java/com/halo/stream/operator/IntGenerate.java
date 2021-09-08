/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.IntSupplier;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class IntGenerate extends PrimitiveIterator.OfInt {

    private final IntSupplier supplier;

    public IntGenerate(@NotNull IntSupplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public int nextInt() {
        return supplier.getAsInt();
    }
}
