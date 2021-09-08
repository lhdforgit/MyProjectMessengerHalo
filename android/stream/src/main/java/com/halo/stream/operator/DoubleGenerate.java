/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.DoubleSupplier;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class DoubleGenerate extends PrimitiveIterator.OfDouble {

    private final DoubleSupplier supplier;

    public DoubleGenerate(@NotNull DoubleSupplier supplier) {
        this.supplier = supplier;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public double nextDouble() {
        return supplier.getAsDouble();
    }
}
