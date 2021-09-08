/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream;

import com.halo.stream.function.Function;
import com.halo.stream.function.IndexedLongConsumer;
import com.halo.stream.function.IndexedLongPredicate;
import com.halo.stream.function.IndexedLongUnaryOperator;
import com.halo.stream.function.LongBinaryOperator;
import com.halo.stream.function.LongConsumer;
import com.halo.stream.function.LongFunction;
import com.halo.stream.function.LongPredicate;
import com.halo.stream.function.LongSupplier;
import com.halo.stream.function.LongToDoubleFunction;
import com.halo.stream.function.LongToIntFunction;
import com.halo.stream.function.LongUnaryOperator;
import com.halo.stream.function.ObjLongConsumer;
import com.halo.stream.function.Supplier;
import com.halo.stream.function.ToLongFunction;
import com.halo.stream.internal.Compose;
import com.halo.stream.internal.Operators;
import com.halo.stream.internal.Params;
import com.halo.stream.iterator.PrimitiveIndexedIterator;
import com.halo.stream.iterator.PrimitiveIterator;
import com.halo.stream.operator.LongArray;
import com.halo.stream.operator.LongConcat;
import com.halo.stream.operator.LongDropWhile;
import com.halo.stream.operator.LongFilter;
import com.halo.stream.operator.LongFilterIndexed;
import com.halo.stream.operator.LongFlatMap;
import com.halo.stream.operator.LongGenerate;
import com.halo.stream.operator.LongIterate;
import com.halo.stream.operator.LongLimit;
import com.halo.stream.operator.LongMap;
import com.halo.stream.operator.LongMapIndexed;
import com.halo.stream.operator.LongMapToDouble;
import com.halo.stream.operator.LongMapToInt;
import com.halo.stream.operator.LongMapToObj;
import com.halo.stream.operator.LongPeek;
import com.halo.stream.operator.LongRangeClosed;
import com.halo.stream.operator.LongSample;
import com.halo.stream.operator.LongScan;
import com.halo.stream.operator.LongScanIdentity;
import com.halo.stream.operator.LongSkip;
import com.halo.stream.operator.LongSorted;
import com.halo.stream.operator.LongTakeUntil;
import com.halo.stream.operator.LongTakeWhile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * A sequence of {@code long}-valued elements supporting aggregate operations.
 *
 * @since 1.1.4
 * @see Stream
 */
public final class LongStream implements Closeable {

    /**
     * Single instance for empty stream. It is safe for multi-thread environment because it has no content.
     */
    private static final LongStream EMPTY = new LongStream(new PrimitiveIterator.OfLong() {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public long nextLong() {
            return 0L;
        }
    });

    /**
     * Returns an empty stream.
     *
     * @return the empty stream
     */
    @NotNull
    public static LongStream empty() {
        return EMPTY;
    }

    /**
     * Creates a {@code LongStream} from {@code PrimitiveIterator.OfLong}.
     *
     * @param iterator  the iterator with elements to be passed to stream
     * @return the new {@code LongStream}
     * @throws NullPointerException if {@code iterator} is null
     */
    @NotNull
    public static LongStream of(@NotNull PrimitiveIterator.OfLong iterator) {
        Objects.requireNonNull(iterator);
        return new LongStream(iterator);
    }

    /**
     * Creates a {@code LongStream} from the specified values.
     *
     * @param values  the elements of the new stream
     * @return the new stream
     * @throws NullPointerException if {@code values} is null
     */
    @NotNull
    public static LongStream of(@NotNull final long... values) {
        Objects.requireNonNull(values);
        if (values.length == 0) {
            return LongStream.empty();
        }
        return new LongStream(new LongArray(values));
    }

    /**
     * Returns stream which contains single element passed as param
     *
     * @param t  element of the stream
     * @return the new stream
     */
    @NotNull
    public static LongStream of(final long t) {
        return new LongStream(new LongArray(new long[] { t }));
    }

    /**
     * Returns a sequential ordered {@code LongStream} from {@code startInclusive}
     * (inclusive) to {@code endExclusive} (exclusive) by an incremental step of
     * {@code 1}.
     *
     * @param startInclusive the (inclusive) initial value
     * @param endExclusive the exclusive upper bound
     * @return a sequential {@code LongStream} for the range of {@code long}
     *         elements
     */
    @NotNull
    public static LongStream range(final long startInclusive, final long endExclusive) {
        if (startInclusive >= endExclusive) {
            return empty();
        }
        return rangeClosed(startInclusive, endExclusive - 1);
    }

    /**
     * Returns a sequential ordered {@code LongStream} from {@code startInclusive}
     * (inclusive) to {@code endInclusive} (inclusive) by an incremental step of
     * {@code 1}.
     *
     * @param startInclusive the (inclusive) initial value
     * @param endInclusive the inclusive upper bound
     * @return a sequential {@code LongStream} for the range of {@code long}
     *         elements
     */
    @NotNull
    public static LongStream rangeClosed(final long startInclusive, final long endInclusive) {
        if (startInclusive > endInclusive) {
            return empty();
        } else if (startInclusive == endInclusive) {
            return of(startInclusive);
        } else return new LongStream(new LongRangeClosed(startInclusive, endInclusive));
    }

    /**
     * Creates a {@code LongStream} by elements that generated by {@code LongSupplier}.
     *
     * @param s  the {@code LongSupplier} for generated elements
     * @return a new infinite sequential {@code LongStream}
     * @throws NullPointerException if {@code s} is null
     */
    @NotNull
    public static LongStream generate(@NotNull final LongSupplier s) {
        Objects.requireNonNull(s);
        return new LongStream(new LongGenerate(s));
    }

    /**
     * Creates a {@code LongStream} by iterative application {@code LongUnaryOperator} function
     * to an initial element {@code seed}. Produces {@code LongStream} consisting of
     * {@code seed}, {@code f(seed)}, {@code f(f(seed))}, etc.
     *
     * <p> The first element (position {@code 0}) in the {@code LongStream} will be
     * the provided {@code seed}. For {@code n > 0}, the element at position
     * {@code n}, will be the result of applying the function {@code f} to the
     * element at position {@code n - 1}.
     *
     * <p>Example:
     * <pre>
     * seed: 1
     * f: (a) -&gt; a + 5
     * result: [1, 6, 11, 16, ...]
     * </pre>
     *
     * @param seed the initial element
     * @param f  a function to be applied to the previous element to produce a new element
     * @return a new sequential {@code LongStream}
     * @throws NullPointerException if {@code f} is null
     */
    @NotNull
    public static LongStream iterate(final long seed,
                                     @NotNull final LongUnaryOperator f) {
        Objects.requireNonNull(f);
        return new LongStream(new LongIterate(seed, f));
    }

    /**
     * Creates an {@code LongStream} by iterative application {@code LongUnaryOperator} function
     * to an initial element {@code seed}, conditioned on satisfying the supplied predicate.
     *
     * <p>Example:
     * <pre>
     * seed: 0
     * predicate: (a) -&gt; a &lt; 20
     * f: (a) -&gt; a + 5
     * result: [0, 5, 10, 15]
     * </pre>
     *
     * @param seed  the initial value
     * @param predicate  a predicate to determine when the stream must terminate
     * @param op  operator to produce new element by previous one
     * @return the new stream
     * @throws NullPointerException if {@code op} is null
     * @since 1.1.5
     */
    @NotNull
    public static LongStream iterate(
            final long seed,
            @NotNull final LongPredicate predicate,
            @NotNull final LongUnaryOperator op) {
        Objects.requireNonNull(predicate);
        return iterate(seed, op).takeWhile(predicate);
    }

    /**
     * Concatenates two streams.
     *
     * <p>Example:
     * <pre>
     * stream a: [1, 2, 3, 4]
     * stream b: [5, 6]
     * result:   [1, 2, 3, 4, 5, 6]
     * </pre>
     *
     * @param a  the first stream
     * @param b  the second stream
     * @return the new concatenated stream
     * @throws NullPointerException if {@code a} or {@code b} is null
     */
    @NotNull
    public static LongStream concat(
            @NotNull final LongStream a,
            @NotNull final LongStream b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        LongStream result = new LongStream(new LongConcat(a.iterator, b.iterator));
        return result.onClose(Compose.closeables(a, b));
    }


    private final PrimitiveIterator.OfLong iterator;
    private final Params params;

    private LongStream(PrimitiveIterator.OfLong iterator) {
        this(null, iterator);
    }

    LongStream(Params params, PrimitiveIterator.OfLong iterator) {
        this.params = params;
        this.iterator = iterator;
    }

    /**
     * Returns internal {@code LongStream} iterator.
     *
     * @return internal {@code LongStream} iterator.
     */
    public PrimitiveIterator.OfLong iterator() {
        return iterator;
    }

    /**
     * Applies custom operator on stream.
     *
     * Transforming function can return {@code LongStream} for intermediate operations,
     * or any value for terminal operation.
     *
     * <p>Operator examples:
     * <pre><code>
     *     // Intermediate operator
     *     public class Zip implements Function&lt;LongStream, LongStream&gt; {
     *
     *         private final LongStream secondStream;
     *         private final LongBinaryOperator combiner;
     *
     *         public Zip(LongStream secondStream, LongBinaryOperator combiner) {
     *             this.secondStream = secondStream;
     *             this.combiner = combiner;
     *         }
     *
     *         &#64;Override
     *         public LongStream apply(LongStream firstStream) {
     *             final PrimitiveIterator.OfLong it1 = firstStream.iterator();
     *             final PrimitiveIterator.OfLong it2 = secondStream.iterator();
     *             return LongStream.of(new PrimitiveIterator.OfLong() {
     *                 &#64;Override
     *                 public boolean hasNext() {
     *                     return it1.hasNext() &amp;&amp; it2.hasNext();
     *                 }
     *
     *                 &#64;Override
     *                 public long nextLong() {
     *                     return combiner.applyAsLong(it1.nextLong(), it2.nextLong());
     *                 }
     *             });
     *         }
     *     }
     *
     *     // Intermediate operator based on existing stream operators
     *     public class SkipAndLimit implements UnaryOperator&lt;LongStream&gt; {
     *
     *         private final int skip, limit;
     *
     *         public SkipAndLimit(int skip, int limit) {
     *             this.skip = skip;
     *             this.limit = limit;
     *         }
     *
     *         &#64;Override
     *         public LongStream apply(LongStream stream) {
     *             return stream.skip(skip).limit(limit);
     *         }
     *     }
     *
     *     // Terminal operator
     *     public class LongSummaryStatistics implements Function&lt;LongStream, long[]&gt; {
     *         &#64;Override
     *         public long[] apply(LongStream stream) {
     *             long count = 0;
     *             long sum = 0;
     *             final PrimitiveIterator.OfLong it = stream.iterator();
     *             while (it.hasNext()) {
     *                 count++;
     *                 sum += it.nextLong();
     *             }
     *             return new long[] {count, sum};
     *         }
     *     }
     * </code></pre>
     *
     * @param <R> the type of the result
     * @param function  a transforming function
     * @return a result of the transforming function
     * @see Stream#custom(com.halo.stream.function.Function)
     * @throws NullPointerException if {@code function} is null
     */
    @Nullable
    public <R> R custom(@NotNull final Function<LongStream, R> function) {
        Objects.requireNonNull(function);
        return function.apply(this);
    }

    /**
     * Returns a {@code Stream} consisting of the elements of this stream,
     * each boxed to an {@code Long}.
     *
     * <p>This is an lazy intermediate operation.
     *
     * @return a {@code Stream} consistent of the elements of this stream,
     *         each boxed to an {@code Long}
     */
    @NotNull
    public Stream<Long> boxed() {
        return new Stream<Long>(params, iterator);
    }

    /**
     * Returns {@code LongStream} with elements that satisfy the given predicate.
     *
     * <p> This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (a) -&gt; a &gt; 2
     * stream: [1, 2, 3, 4, -8, 0, 11]
     * result: [3, 4, 11]
     * </pre>
     *
     * @param predicate  the predicate used to filter elements
     * @return the new stream
     */
    @NotNull
    public LongStream filter(@NotNull final LongPredicate predicate) {
        return new LongStream(params, new LongFilter(iterator, predicate));
    }

    /**
     * Returns a {@code LongStream} with elements that satisfy the given {@code IndexedLongPredicate}.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (index, value) -&gt; (index + value) &gt; 6
     * stream: [1, 2, 3, 4, 0, 11]
     * index:  [0, 1, 2, 3, 4,  5]
     * sum:    [1, 3, 5, 7, 4, 16]
     * filter: [         7,    16]
     * result: [4, 11]
     * </pre>
     *
     * @param predicate  the {@code IndexedLongPredicate} used to filter elements
     * @return the new stream
     * @since 1.2.1
     */
    @NotNull
    public LongStream filterIndexed(@NotNull IndexedLongPredicate predicate) {
        return filterIndexed(0, 1, predicate);
    }

    /**
     * Returns a {@code LongStream} with elements that satisfy the given {@code IndexedLongPredicate}.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * from: 4
     * step: 3
     * predicate: (index, value) -&gt; (index + value) &gt; 15
     * stream: [1, 2,  3,  4,  0, 11]
     * index:  [4, 7, 10, 13, 16, 19]
     * sum:    [5, 9, 13, 17, 16, 30]
     * filter: [          17, 16, 30]
     * result: [4, 0, 11]
     * </pre>
     *
     * @param from  the initial value of the index (inclusive)
     * @param step  the step of the index
     * @param predicate  the {@code IndexedLongPredicate} used to filter elements
     * @return the new stream
     * @since 1.2.1
     */
    @NotNull
    public LongStream filterIndexed(int from, int step,
                                    @NotNull IndexedLongPredicate predicate) {
        return new LongStream(params, new LongFilterIndexed(
                new PrimitiveIndexedIterator.OfLong(from, step, iterator),
                predicate));
    }

    /**
     * Returns {@code LongStream} with elements that does not satisfy the given predicate.
     *
     * <p> This is an intermediate operation.
     *
     * @param predicate  the predicate used to filter elements
     * @return the new stream
     */
    @NotNull
    public LongStream filterNot(@NotNull final LongPredicate predicate) {
        return filter(LongPredicate.Util.negate(predicate));
    }

    /**
     * Returns an {@code LongStream} consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p> This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * mapper: (a) -&gt; a + 5
     * stream: [1, 2, 3, 4]
     * result: [6, 7, 8, 9]
     * </pre>
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new stream
     * @see Stream#map(com.halo.stream.function.Function)
     */
    @NotNull
    public LongStream map(@NotNull final LongUnaryOperator mapper) {
        return new LongStream(params, new LongMap(iterator, mapper));
    }

    /**
     * Returns a {@code LongStream} with elements that obtained
     * by applying the given {@code IndexedLongUnaryOperator}.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * mapper: (index, value) -&gt; (index * value)
     * stream: [1, 2, 3,  4]
     * index:  [0, 1, 2,  3]
     * result: [0, 2, 6, 12]
     * </pre>
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new stream
     * @since 1.2.1
     */
    @NotNull
    public LongStream mapIndexed(@NotNull IndexedLongUnaryOperator mapper) {
        return mapIndexed(0, 1, mapper);
    }

    /**
     * Returns a {@code LongStream} with elements that obtained
     * by applying the given {@code IndexedLongUnaryOperator}.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * from: -2
     * step: 2
     * mapper: (index, value) -&gt; (index * value)
     * stream: [ 1, 2, 3,  4]
     * index:  [-2, 0, 2,  4]
     * result: [-2, 0, 6, 16]
     * </pre>
     *
     * @param from  the initial value of the index (inclusive)
     * @param step  the step of the index
     * @param mapper  the mapper function used to apply to each element
     * @return the new stream
     * @since 1.2.1
     */
    @NotNull
    public LongStream mapIndexed(int from, int step,
                                 @NotNull IndexedLongUnaryOperator mapper) {
        return new LongStream(params, new LongMapIndexed(
                new PrimitiveIndexedIterator.OfLong(from, step, iterator),
                mapper));
    }

    /**
     * Returns a {@code Stream} consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p> This is an intermediate operation.
     *
     * @param <R> the type result
     * @param mapper  the mapper function used to apply to each element
     * @return the new {@code Stream}
     */
    @NotNull
    public <R> Stream<R> mapToObj(@NotNull final LongFunction<? extends R> mapper) {
        return new Stream<R>(params, new LongMapToObj<R>(iterator, mapper));
    }

    /**
     * Returns an {@code IntStream} consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p> This is an intermediate operation.
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new {@code IntStream}
     */
    @NotNull
    public IntStream mapToInt(@NotNull final LongToIntFunction mapper) {
        return new IntStream(params, new LongMapToInt(iterator, mapper));
    }

    /**
     * Returns an {@code DoubleStream} consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p> This is an intermediate operation.
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new {@code DoubleStream}
     */
    @NotNull
    public DoubleStream mapToDouble(@NotNull final LongToDoubleFunction mapper) {
        return new DoubleStream(params, new LongMapToDouble(iterator, mapper));
    }

    /**
     * Returns a stream consisting of the results of replacing each element of
     * this stream with the contents of a mapped stream produced by applying
     * the provided mapping function to each element.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * mapper: (a) -&gt; [a, a + 5]
     * stream: [1, 2, 3, 4]
     * result: [1, 6, 2, 7, 3, 8, 4, 9]
     * </pre>
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new stream
     * @see Stream#flatMap(com.halo.stream.function.Function)
     */
    @NotNull
    public LongStream flatMap(@NotNull final LongFunction<? extends LongStream> mapper) {
        return new LongStream(params, new LongFlatMap(iterator, mapper));
    }

    /**
     * Returns a stream consisting of the distinct elements of this stream.
     *
     * <p>This is a stateful intermediate operation.
     *
     * <p>Example:
     * <pre>
     * stream: [1, 4, 2, 3, 3, 4, 1]
     * result: [1, 4, 2, 3]
     * </pre>
     *
     * @return the new stream
     */
    @NotNull
    public LongStream distinct() {
        return boxed().distinct().mapToLong(UNBOX_FUNCTION);
    }

    /**
     * Returns a stream consisting of the elements of this stream in sorted order.
     *
     * <p>This is a stateful intermediate operation.
     *
     * <p>Example:
     * <pre>
     * stream: [3, 4, 1, 2]
     * result: [1, 2, 3, 4]
     * </pre>
     *
     * @return the new stream
     */
    @NotNull
    public LongStream sorted() {
        return new LongStream(params, new LongSorted(iterator));
    }

    /**
     * Returns a stream consisting of the elements of this stream
     * in sorted order as determinated by provided {@code Comparator}.
     *
     * <p>This is a stateful intermediate operation.
     *
     * <p>Example:
     * <pre>
     * comparator: (a, b) -&gt; -a.compareTo(b)
     * stream: [1, 2, 3, 4]
     * result: [4, 3, 2, 1]
     * </pre>
     *
     * @param comparator  the {@code Comparator} to compare elements
     * @return the new {@code LongStream}
     */
    @NotNull
    public LongStream sorted(@Nullable Comparator<Long> comparator) {
        return boxed().sorted(comparator).mapToLong(UNBOX_FUNCTION);
    }

    /**
     * Samples the {@code LongStream} by emitting every n-th element.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * stepWidth: 3
     * stream: [1, 2, 3, 4, 5, 6, 7, 8]
     * result: [1, 4, 7]
     * </pre>
     *
     * @param stepWidth  step width
     * @return the new {@code LongStream}
     * @throws IllegalArgumentException if {@code stepWidth} is zero or negative
     * @see Stream#sample(int)
     */
    @NotNull
    public LongStream sample(final int stepWidth) {
        if (stepWidth <= 0) throw new IllegalArgumentException("stepWidth cannot be zero or negative");
        if (stepWidth == 1) return this;
        return new LongStream(params, new LongSample(iterator, stepWidth));
    }

    /**
     * Performs provided action on each element.
     *
     * <p>This is an intermediate operation.
     *
     * @param action the action to be performed on each element
     * @return the new stream
     */
    @NotNull
    public LongStream peek(@NotNull final LongConsumer action) {
        return new LongStream(params, new LongPeek(iterator, action));
    }

    /**
     * Returns a {@code LongStream} produced by iterative application of a accumulation function
     * to reduction value and next element of the current stream.
     * Produces a {@code LongStream} consisting of {@code value1}, {@code acc(value1, value2)},
     * {@code acc(acc(value1, value2), value3)}, etc.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * accumulator: (a, b) -&gt; a + b
     * stream: [1, 2, 3, 4, 5]
     * result: [1, 3, 6, 10, 15]
     * </pre>
     *
     * @param accumulator  the accumulation function
     * @return the new stream
     * @throws NullPointerException if {@code accumulator} is null
     * @since 1.1.6
     */
    @NotNull
    public LongStream scan(@NotNull final LongBinaryOperator accumulator) {
        Objects.requireNonNull(accumulator);
        return new LongStream(params, new LongScan(iterator, accumulator));
    }

    /**
     * Returns a {@code LongStream} produced by iterative application of a accumulation function
     * to an initial element {@code identity} and next element of the current stream.
     * Produces a {@code LongStream} consisting of {@code identity}, {@code acc(identity, value1)},
     * {@code acc(acc(identity, value1), value2)}, etc.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * identity: 0
     * accumulator: (a, b) -&gt; a + b
     * stream: [1, 2, 3, 4, 5]
     * result: [0, 1, 3, 6, 10, 15]
     * </pre>
     *
     * @param identity  the initial value
     * @param accumulator  the accumulation function
     * @return the new stream
     * @throws NullPointerException if {@code accumulator} is null
     * @since 1.1.6
     */
    @NotNull
    public LongStream scan(final long identity,
                           @NotNull final LongBinaryOperator accumulator) {
        Objects.requireNonNull(accumulator);
        return new LongStream(params, new LongScanIdentity(iterator, identity, accumulator));
    }

    /**
     * Takes elements while the predicate returns {@code true}.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (a) -&gt; a &lt; 3
     * stream: [1, 2, 3, 4, 1, 2, 3, 4]
     * result: [1, 2]
     * </pre>
     *
     * @param predicate  the predicate used to take elements
     * @return the new {@code LongStream}
     */
    @NotNull
    public LongStream takeWhile(@NotNull final LongPredicate predicate) {
        return new LongStream(params, new LongTakeWhile(iterator, predicate));
    }

    /**
     * Takes elements while the predicate returns {@code false}.
     * Once predicate condition is satisfied by an element, the stream
     * finishes with this element.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * stopPredicate: (a) -&gt; a &gt; 2
     * stream: [1, 2, 3, 4, 1, 2, 3, 4]
     * result: [1, 2, 3]
     * </pre>
     *
     * @param stopPredicate  the predicate used to take elements
     * @return the new {@code LongStream}
     * @since 1.1.6
     */
    @NotNull
    public LongStream takeUntil(@NotNull final LongPredicate stopPredicate) {
        return new LongStream(params, new LongTakeUntil(iterator, stopPredicate));
    }

    /**
     * Drops elements while the predicate is true and returns the rest.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (a) -&gt; a &lt; 3
     * stream: [1, 2, 3, 4, 1, 2, 3, 4]
     * result: [3, 4, 1, 2, 3, 4]
     * </pre>
     *
     * @param predicate  the predicate used to drop elements
     * @return the new {@code LongStream}
     */
    @NotNull
    public LongStream dropWhile(@NotNull final LongPredicate predicate) {
        return new LongStream(params, new LongDropWhile(iterator, predicate));
    }

    /**
     * Returns a stream consisting of the elements of this stream, truncated
     * to be no longer than {@code maxSize} in length.
     *
     * <p> This is a short-circuiting stateful intermediate operation.
     *
     * <p>Example:
     * <pre>
     * maxSize: 3
     * stream: [1, 2, 3, 4, 5]
     * result: [1, 2, 3]
     *
     * maxSize: 10
     * stream: [1, 2]
     * result: [1, 2]
     * </pre>
     *
     * @param maxSize  the number of elements the stream should be limited to
     * @return the new stream
     * @throws IllegalArgumentException if {@code maxSize} is negative
     */
    @NotNull
    public LongStream limit(final long maxSize) {
        if (maxSize < 0) throw new IllegalArgumentException("maxSize cannot be negative");
        if (maxSize == 0) return LongStream.empty();
        return new LongStream(params, new LongLimit(iterator, maxSize));
    }

    /**
     * Skips first {@code n} elements and returns {@code LongStream} with remaining elements.
     * If this stream contains fewer than {@code n} elements, then an
     * empty stream will be returned.
     *
     * <p>This is a stateful intermediate operation.
     *
     * <p>Example:
     * <pre>
     * n: 3
     * stream: [1, 2, 3, 4, 5]
     * result: [4, 5]
     *
     * n: 10
     * stream: [1, 2]
     * result: []
     * </pre>
     *
     * @param n  the number of elements to skip
     * @return the new stream
     * @throws IllegalArgumentException if {@code n} is negative
     */
    @NotNull
    public LongStream skip(final long n) {
        if (n < 0) throw new IllegalArgumentException("n cannot be negative");
        if (n == 0) return this;
        return new LongStream(params, new LongSkip(iterator, n));
    }

    /**
     * Performs an action for each element of this stream.
     *
     * <p>This is a terminal operation.
     *
     * @param action  the action to be performed on each element
     */
    public void forEach(@NotNull LongConsumer action) {
        while (iterator.hasNext()) {
            action.accept(iterator.nextLong());
        }
    }

    /**
     * Performs the given indexed action on each element.
     *
     * <p>This is a terminal operation.
     *
     * @param action  the action to be performed on each element
     * @since 1.2.1
     */
    public void forEachIndexed(@NotNull IndexedLongConsumer action) {
        forEachIndexed(0, 1, action);
    }

    /**
     * Performs the given indexed action on each element.
     *
     * <p>This is a terminal operation.
     *
     * @param from  the initial value of the index (inclusive)
     * @param step  the step of the index
     * @param action  the action to be performed on each element
     * @since 1.2.1
     */
    public void forEachIndexed(int from, int step,
                               @NotNull IndexedLongConsumer action) {
        int index = from;
        while (iterator.hasNext()) {
            action.accept(index, iterator.nextLong());
            index += step;
        }
    }

    /**
     * Performs a reduction on the elements of this stream, using the provided
     * identity value and an associative accumulation function, and returns the
     * reduced value.
     *
     * <p>The {@code identity} value must be an identity for the accumulator
     * function. This means that for all {@code x},
     * {@code accumulator.apply(identity, x)} is equal to {@code x}.
     * The {@code accumulator} function must be an associative function.
     *
     * <p>This is a terminal operation.
     *
     * <p>Example:
     * <pre>
     * identity: 0
     * accumulator: (a, b) -&gt; a + b
     * stream: [1, 2, 3, 4, 5]
     * result: 15
     * </pre>
     *
     * @param identity  the identity value for the accumulating function
     * @param accumulator  the accumulation function
     * @return the result of the reduction
     * @see #sum()
     * @see #min()
     * @see #max()
     */
    public long reduce(long identity, @NotNull LongBinaryOperator accumulator) {
        long result = identity;
        while (iterator.hasNext()) {
            final long value = iterator.nextLong();
            result = accumulator.applyAsLong(result, value);
        }
        return result;
    }

    /**
     * Performs a reduction on the elements of this stream, using an
     * associative accumulation function, and returns an {@code OptionalLong}
     * describing the reduced value, if any.
     *
     * <p>The {@code accumulator} function must be an associative function.
     *
     * <p>This is a terminal operation.
     *
     * @param accumulator  the accumulation function
     * @return the result of the reduction
     * @see #reduce(com.halo.stream.function.LongBinaryOperator)
     */
    @NotNull
    public OptionalLong reduce(@NotNull LongBinaryOperator accumulator) {
        boolean foundAny = false;
        long result = 0;
        while (iterator.hasNext()) {
            final long value = iterator.nextLong();
            if (!foundAny) {
                foundAny = true;
                result = value;
            } else {
                result = accumulator.applyAsLong(result, value);
            }
        }
        return foundAny ? OptionalLong.of(result) : OptionalLong.empty();
    }

    /**
     * Returns an array containing the elements of this stream.
     *
     * <p>This is a terminal operation.
     *
     * @return an array containing the elements of this stream
     */
    @NotNull
    public long[] toArray() {
        return Operators.toLongArray(iterator);
    }

    /**
     * Collects elements to {@code supplier} provided container by applying the given accumulation function.
     *
     * <p>This is a terminal operation.
     *
     * @param <R> the type of the result
     * @param supplier  the supplier function that provides container
     * @param accumulator  the accumulation function
     * @return the result of collect elements
     * @see Stream#collect(com.halo.stream.function.Supplier, com.halo.stream.function.BiConsumer)
     */
    @Nullable
    public <R> R collect(@NotNull Supplier<R> supplier,
                         @NotNull ObjLongConsumer<R> accumulator) {
        final R result = supplier.get();
        while (iterator.hasNext()) {
            final long value = iterator.nextLong();
            accumulator.accept(result, value);
        }
        return result;
    }

    /**
     * Returns the sum of elements in this stream.
     *
     * @return the sum of elements in this stream
     */
    public long sum() {
        long sum = 0;
        while (iterator.hasNext()) {
            sum += iterator.nextLong();
        }
        return sum;
    }

    /**
     * Returns an {@code OptionalLong} describing the minimum element of this
     * stream, or an empty optional if this stream is empty.
     *
     * <p>This is a terminal operation.
     *
     * @return the minimum element
     */
    @NotNull
    public OptionalLong min() {
        return reduce(new LongBinaryOperator() {
            @Override
            public long applyAsLong(long left, long right) {
                return Math.min(left, right);
            }
        });
    }

    /**
     * Returns an {@code OptionalLong} describing the maximum element of this
     * stream, or an empty optional if this stream is empty.
     *
     * <p>This is a terminal operation.
     *
     * @return the maximum element
     */
    @NotNull
    public OptionalLong max() {
        return reduce(new LongBinaryOperator() {
            @Override
            public long applyAsLong(long left, long right) {
                return Math.max(left, right);
            }
        });
    }

    /**
     * Returns the count of elements in this stream.
     *
     * <p>This is a terminal operation.
     *
     * @return the count of elements in this stream
     */
    public long count() {
        long count = 0;
        while (iterator.hasNext()) {
            iterator.nextLong();
            count++;
        }
        return count;
    }

    /**
     * Tests whether all elements match the given predicate.
     * May not evaluate the predicate on all elements if not necessary
     * for determining the result. If the stream is empty then
     * {@code false} is returned and the predicate is not evaluated.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (a) -&gt; a == 5
     * stream: [1, 2, 3, 4, 5]
     * result: true
     *
     * predicate: (a) -&gt; a == 5
     * stream: [5, 5, 5]
     * result: true
     * </pre>
     *
     * @param predicate  the predicate used to match elements
     * @return {@code true} if any elements of the stream match the provided
     *         predicate, otherwise {@code false}
     */
    public boolean anyMatch(@NotNull LongPredicate predicate) {
        while (iterator.hasNext()) {
            if (predicate.test(iterator.nextLong()))
                return true;
        }
        return false;
    }

    /**
     * Tests whether all elements match the given predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result. If the stream is empty then {@code true} is
     * returned and the predicate is not evaluated.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (a) -&gt; a == 5
     * stream: [1, 2, 3, 4, 5]
     * result: false
     *
     * predicate: (a) -&gt; a == 5
     * stream: [5, 5, 5]
     * result: true
     * </pre>
     *
     * @param predicate  the predicate used to match elements
     * @return {@code true} if either all elements of the stream match the
     *         provided predicate or the stream is empty, otherwise {@code false}
     */
    public boolean allMatch(@NotNull LongPredicate predicate) {
        while (iterator.hasNext()) {
            if (!predicate.test(iterator.nextLong()))
                return false;
        }
        return true;
    }

    /**
     * Tests whether no elements match the given predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result. If the stream is empty then {@code true} is
     * returned and the predicate is not evaluated.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (a) -&gt; a == 5
     * stream: [1, 2, 3, 4, 5]
     * result: false
     *
     * predicate: (a) -&gt; a == 5
     * stream: [1, 2, 3]
     * result: true
     * </pre>
     *
     * @param predicate  the predicate used to match elements
     * @return {@code true} if either no elements of the stream match the
     *         provided predicate or the stream is empty, otherwise {@code false}
     */
    public boolean noneMatch(@NotNull LongPredicate predicate) {
        while (iterator.hasNext()) {
            if (predicate.test(iterator.nextLong()))
                return false;
        }
        return true;
    }

    /**
     * Returns the first element wrapped by {@code OptionalLong} class.
     * If stream is empty, returns {@code OptionalLong.empty()}.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * @return an {@code OptionalLong} with first element
     *         or {@code OptionalLong.empty()} if stream is empty
     */
    @NotNull
    public OptionalLong findFirst() {
        if (iterator.hasNext()) {
            return OptionalLong.of(iterator.nextLong());
        }
        return OptionalLong.empty();
    }

    /**
     * Returns the last element wrapped by {@code OptionalLong} class.
     * If stream is empty, returns {@code OptionalLong.empty()}.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * @return an {@code OptionalLong} with the last element
     *         or {@code OptionalLong.empty()} if the stream is empty
     * @since 1.1.8
     */
    @NotNull
    public OptionalLong findLast() {
        return reduce(new LongBinaryOperator() {
            @Override
            public long applyAsLong(long left, long right) {
                return right;
            }
        });
    }

    /**
     * Returns the single element of stream.
     * If stream is empty, throws {@code NoSuchElementException}.
     * If stream contains more than one element, throws {@code IllegalStateException}.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * <p>Example:
     * <pre>
     * stream: []
     * result: NoSuchElementException
     *
     * stream: [1]
     * result: 1
     *
     * stream: [1, 2, 3]
     * result: IllegalStateException
     * </pre>
     *
     * @return single element of stream
     * @throws NoSuchElementException if stream is empty
     * @throws IllegalStateException if stream contains more than one element
     */
    public long single() {
        if (!iterator.hasNext()) {
            throw new NoSuchElementException("LongStream contains no element");
        }

        final long singleCandidate = iterator.nextLong();
        if (iterator.hasNext()) {
            throw new IllegalStateException("LongStream contains more than one element");
        }
        return singleCandidate;
    }

    /**
     * Returns the single element wrapped by {@code OptionalLong} class.
     * If stream is empty, returns {@code OptionalLong.empty()}.
     * If stream contains more than one element, throws {@code IllegalStateException}.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * <p>Example:
     * <pre>
     * stream: []
     * result: OptionalLong.empty()
     *
     * stream: [1]
     * result: OptionalLong.of(1)
     *
     * stream: [1, 2, 3]
     * result: IllegalStateException
     * </pre>
     *
     * @return an {@code OptionalLong} with single element
     *         or {@code OptionalLong.empty()} if stream is empty
     * @throws IllegalStateException if stream contains more than one element
     */
    @NotNull
    public OptionalLong findSingle() {
        if (!iterator.hasNext()) {
            return OptionalLong.empty();
        }

        final long singleCandidate = iterator.nextLong();
        if (iterator.hasNext()) {
            throw new IllegalStateException("LongStream contains more than one element");
        }
        return OptionalLong.of(singleCandidate);
    }

    /**
     * Adds close handler to the current stream.
     *
     * <p>This is an intermediate operation.
     *
     * @param closeHandler  an action to execute when the stream is closed
     * @return the new stream with the close handler
     * @since 1.1.8
     */
    @NotNull
    public LongStream onClose(@NotNull final Runnable closeHandler) {
        Objects.requireNonNull(closeHandler);
        final Params newParams = Params.wrapWithCloseHandler(params, closeHandler);
        return new LongStream(newParams, iterator);
    }

    /**
     * Causes close handler to be invoked if it exists.
     * Since most of the stream providers are lists or arrays,
     * it is not necessary to close the stream.
     *
     * @since 1.1.8
     */
    @Override
    public void close() {
        if (params != null && params.closeHandler != null) {
            params.closeHandler.run();
            params.closeHandler = null;
        }
    }


    private static final ToLongFunction<Long> UNBOX_FUNCTION = new ToLongFunction<Long>() {
        @Override
        public long applyAsLong(Long t) {
            return t;
        }
    };
}
