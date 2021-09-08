/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.function;

/**
 * Represents a supplier of {@code int}-valued results.
 *
 * @param <E> the type of the exception
 * @since 1.1.7
 * @see IntSupplier
 */
public interface ThrowableIntSupplier<E extends Throwable> {

    /**
     * Gets a result.
     *
     * @return a result
     * @throws E an exception
     */
    int getAsInt() throws E;
}
