/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.iterator.LsaExtIterator;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ObjDistinct<T> extends LsaExtIterator<T> {

    private final Iterator<? extends T> iterator;
    private final Set<T> set;

    public ObjDistinct(@NotNull Iterator<? extends T> iterator) {
        this.iterator = iterator;
        set = new HashSet<T>();
    }

    @Override
    protected void nextIteration() {
        while (hasNext = iterator.hasNext()) {
            next = iterator.next();
            if (set.add(next)) {
                return;
            }
        }
    }
}
