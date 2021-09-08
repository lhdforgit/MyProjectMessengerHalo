/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.operator;

import com.halo.stream.function.BiFunction;
import com.halo.stream.iterator.LsaIterator;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ObjZip<F, S, R> extends LsaIterator<R> {

    private final Iterator<? extends F> iterator1;
    private final Iterator<? extends S> iterator2;
    private final BiFunction<? super F, ? super S, ? extends R> combiner;

    public ObjZip(
            @NotNull Iterator<? extends F> iterator1,
            @NotNull Iterator<? extends S> iterator2,
            @NotNull BiFunction<? super F, ? super S, ? extends R> combiner) {
        this.iterator1 = iterator1;
        this.iterator2 = iterator2;
        this.combiner = combiner;
    }

    @Override
    public boolean hasNext() {
        return iterator1.hasNext() && iterator2.hasNext();
    }

    @Override
    public R nextIteration() {
        return combiner.apply(iterator1.next(), iterator2.next());
    }
}
