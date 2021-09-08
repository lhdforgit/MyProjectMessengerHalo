/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.function;

/**
 * Represents an operation upon two {@code int}-valued operands and producing an
 * {@code int}-valued result.  This is the primitive type specialization of
 * {@link com.halo.stream.function.BinaryOperator} for {@code int}.
 *
 * <p>This is a functional interface whose functional method is {@link #applyAsInt(int, int)}.
 *
 * @see com.halo.stream.function.BinaryOperator
 * @see IntUnaryOperator
 */
public interface IntBinaryOperator {

    /**
     * Applies this operator to the given operands.
     *
     * @param left the first operand
     * @param right the second operand
     * @return the operator result
     */
    int applyAsInt(int left, int right);
}
