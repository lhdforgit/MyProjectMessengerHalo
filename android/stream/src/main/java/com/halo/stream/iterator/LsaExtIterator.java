/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Extended iterator for common purposes.
 *
 * @param <T> the type of the inner value
 */
public abstract class LsaExtIterator<T> implements Iterator<T> {

    protected T next;
    protected boolean hasNext, isInit;

    @Override
    public boolean hasNext() {
        if (!isInit) {
            // First call to hasNext() on new iterator
            nextIteration();
            isInit = true;
        }
        return hasNext;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public T next() {
        if (!isInit) {
            // First call to next() on new iterator
            hasNext();
        }
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        final T result = next;
        nextIteration();
        if (!hasNext) {
            // Clear reference to the previous element
            next = null;
        }
        return result;
    }

    protected abstract void nextIteration();

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove not supported");
    }
}