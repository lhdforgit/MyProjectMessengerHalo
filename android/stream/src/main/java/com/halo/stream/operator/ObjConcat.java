/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.iterator.LsaExtIterator;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ObjConcat<T> extends LsaExtIterator<T> {

    private final Iterator<? extends T> iterator1;
    private final Iterator<? extends T> iterator2;
    private boolean firstStreamIsCurrent;

    public ObjConcat(
            @NotNull Iterator<? extends T> iterator1,
            @NotNull Iterator<? extends T> iterator2) {
        this.iterator1 = iterator1;
        this.iterator2 = iterator2;
        firstStreamIsCurrent = true;
    }

    @Override
    protected void nextIteration() {
        if (firstStreamIsCurrent) {
            if (iterator1.hasNext()) {
                next = iterator1.next();
                hasNext = true;
                return;
            }
            firstStreamIsCurrent = false;
        }
        if (iterator2.hasNext()) {
            next = iterator2.next();
            hasNext = true;
            return;
        }
        hasNext = false;
    }
}
