/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.internal.Operators;
import com.halo.stream.iterator.LsaExtIterator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ObjSorted<T> extends LsaExtIterator<T> {

    private final Iterator<? extends T> iterator;
    private final Comparator<? super T> comparator;
    private Iterator<T> sortedIterator;

    public ObjSorted(
            @NotNull Iterator<? extends T> iterator,
            @Nullable Comparator<? super T> comparator) {
        this.iterator = iterator;
        this.comparator = comparator;
    }

    @Override
    protected void nextIteration() {
        if (!isInit) {
            final List<T> list = Operators.<T>toList(iterator);
            Collections.sort(list, comparator);
            sortedIterator = list.iterator();
        }
        hasNext = sortedIterator.hasNext();
        if (hasNext) {
            next = sortedIterator.next();
        }
    }
}
