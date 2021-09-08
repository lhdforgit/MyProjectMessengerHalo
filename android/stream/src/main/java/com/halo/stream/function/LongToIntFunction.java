/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.function;

/**
 * Represents a function which produces an {@code int}-valued result from input argument.
 *
 * @since 1.1.4
 * @see Function
 */
public interface LongToIntFunction {

    /**
     * Applies this function to the given argument.
     *
     * @param value  an argument
     * @return the function result
     */
    int applyAsInt(long value);
}
