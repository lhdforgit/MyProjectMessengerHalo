/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.iterator.LsaIterator;

import org.jetbrains.annotations.NotNull;

public class ObjArray<T> extends LsaIterator<T> {

    private final T[] elements;
    private int index;

    public ObjArray(@NotNull T[] elements) {
        this.elements = elements;
        index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < elements.length;
    }

    @Override
    public T nextIteration() {
        return elements[index++];
    }
}
