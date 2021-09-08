/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.function;

/**
 * Represents a function for supplying result which can throw an exception.
 *
 * @param <T> the type of the result
 * @param <E> the type of the exception
 * @see Supplier
 */
public interface ThrowableSupplier<T, E extends Throwable> {

    /**
     * Gets a result.
     *
     * @return a result
     * @throws E an exception
     */
    T get() throws E;
}
