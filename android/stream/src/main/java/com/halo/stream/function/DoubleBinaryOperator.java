/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.function;

/**
 * Represents an operation on two {@code double}-valued operands
 * that produces a {@code double}-valued result.
 *
 * @since 1.1.4
 * @see BinaryOperator
 */
public interface DoubleBinaryOperator {

    /**
     * Applies this operator to the given operands.
     *
     * @param left  the first operand
     * @param right  the second operand
     * @return the operator result
     */
    double applyAsDouble(double left, double right);
}
