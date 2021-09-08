/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.function;

/**
 * Represents an operation on {@code int}-valued input argument.
 *
 * @param <E> the type of the exception
 * @since 1.1.7
 * @see IntConsumer
 */
public interface ThrowableIntConsumer<E extends Throwable> {

    /**
     * Performs operation on the given argument.
     *
     * @param value  the input argument
     * @throws E an exception
     */
    void accept(int value) throws E;
}
