/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.Supplier;
import com.halo.stream.iterator.LsaIterator;

import org.jetbrains.annotations.NotNull;

public class ObjGenerate<T> extends LsaIterator<T> {

    private final Supplier<T> supplier;

    public ObjGenerate(@NotNull Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public T nextIteration() {
        return supplier.get();
    }
}
