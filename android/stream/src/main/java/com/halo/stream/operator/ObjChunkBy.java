/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.Function;
import com.halo.stream.iterator.LsaIterator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObjChunkBy<T, K> extends LsaIterator<List<T>> {

    private final Iterator<? extends T> iterator;
    private final Function<? super T, ? extends K> classifier;
    private T next;
    private boolean peekedNext;

    public ObjChunkBy(
            @NotNull Iterator<? extends T> iterator,
            @NotNull Function<? super T, ? extends K> classifier) {
        this.iterator = iterator;
        this.classifier = classifier;
    }

    @Override
    public boolean hasNext() {
        return peekedNext || iterator.hasNext();
    }

    @Override
    public List<T> nextIteration() {
        final K key = classifier.apply(peek());

        final List<T> list = new ArrayList<T>();
        do {
            list.add(takeNext());
        } while ( iterator.hasNext() && key.equals(classifier.apply(peek())) );

        return list;
    }

    private T takeNext() {
        final T element = peek();
        peekedNext = false;
        return element;
    }

    private T peek() {
        if (!peekedNext) {
            next = iterator.next();
            peekedNext = true;
        }
        return next;
    }
}
