/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.Consumer;
import com.halo.stream.iterator.LsaIterator;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ObjPeek<T> extends LsaIterator<T> {

    private final Iterator<? extends T> iterator;
    private final Consumer<? super T> action;

    public ObjPeek(
            @NotNull Iterator<? extends T> iterator,
            @NotNull Consumer<? super T> action) {
        this.iterator = iterator;
        this.action = action;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T nextIteration() {
        final T value = iterator.next();
        action.accept(value);
        return value;
    }
}
