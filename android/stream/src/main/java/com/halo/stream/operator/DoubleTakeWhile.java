/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.DoublePredicate;
import com.halo.stream.iterator.PrimitiveExtIterator;
import com.halo.stream.iterator.PrimitiveIterator;

import org.jetbrains.annotations.NotNull;

public class DoubleTakeWhile extends PrimitiveExtIterator.OfDouble {

    private final PrimitiveIterator.OfDouble iterator;
    private final DoublePredicate predicate;

    public DoubleTakeWhile(
            @NotNull PrimitiveIterator.OfDouble iterator,
            @NotNull DoublePredicate predicate) {
        this.iterator = iterator;
        this.predicate = predicate;
    }

    @Override
    protected void nextIteration() {
        hasNext = iterator.hasNext()
                && predicate.test(next = iterator.next());
    }
}
