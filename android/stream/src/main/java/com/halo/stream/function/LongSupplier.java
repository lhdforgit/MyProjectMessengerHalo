/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream.function;

import com.halo.stream.Objects;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a supplier of {@code long}-valued results.
 *
 * @since 1.1.4
 * @see Supplier
 */
public interface LongSupplier {

    /**
     * Gets a result.
     *
     * @return a result
     */
    long getAsLong();

    class Util {

        private Util() { }

        /**
         * Creates a safe {@code LongSupplier}.
         *
         * @param throwableSupplier  the supplier that may throw an exception
         * @return a {@code LongSupplier}
         * @throws NullPointerException if {@code throwableSupplier} is null
         * @since 1.1.7
         * @see #safe(com.halo.stream.function.ThrowableLongSupplier, long)
         */
        public static LongSupplier safe(@NotNull ThrowableLongSupplier<Throwable> throwableSupplier) {
            return safe(throwableSupplier, 0L);
        }

        /**
         * Creates a safe {@code LongSupplier}.
         *
         * @param throwableSupplier  the supplier that may throw an exception
         * @param resultIfFailed  the result which returned if exception was thrown
         * @return a {@code LongSupplier}
         * @throws NullPointerException if {@code throwableSupplier} is null
         * @since 1.1.7
         */
        public static LongSupplier safe(
                @NotNull final ThrowableLongSupplier<Throwable> throwableSupplier,
                final long resultIfFailed) {
            Objects.requireNonNull(throwableSupplier);
            return new LongSupplier() {

                @Override
                public long getAsLong() {
                    try {
                        return throwableSupplier.getAsLong();
                    } catch (Throwable ex) {
                        return resultIfFailed;
                    }
                }
            };
        }

    }
}
