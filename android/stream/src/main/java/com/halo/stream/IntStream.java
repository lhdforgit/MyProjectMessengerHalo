/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.stream;

import com.halo.stream.function.Function;
import com.halo.stream.function.IndexedIntConsumer;
import com.halo.stream.function.IndexedIntPredicate;
import com.halo.stream.function.IntBinaryOperator;
import com.halo.stream.function.IntConsumer;
import com.halo.stream.function.IntFunction;
import com.halo.stream.function.IntPredicate;
import com.halo.stream.function.IntSupplier;
import com.halo.stream.function.IntToDoubleFunction;
import com.halo.stream.function.IntToLongFunction;
import com.halo.stream.function.IntUnaryOperator;
import com.halo.stream.function.ObjIntConsumer;
import com.halo.stream.function.Supplier;
import com.halo.stream.function.ToIntFunction;
import com.halo.stream.internal.Compose;
import com.halo.stream.internal.Operators;
import com.halo.stream.internal.Params;
import com.halo.stream.iterator.PrimitiveIndexedIterator;
import com.halo.stream.iterator.PrimitiveIterator;
import com.halo.stream.operator.IntArray;
import com.halo.stream.operator.IntCodePoints;
import com.halo.stream.operator.IntConcat;
import com.halo.stream.operator.IntDropWhile;
import com.halo.stream.operator.IntFilter;
import com.halo.stream.operator.IntFilterIndexed;
import com.halo.stream.operator.IntFlatMap;
import com.halo.stream.operator.IntGenerate;
import com.halo.stream.operator.IntIterate;
import com.halo.stream.operator.IntLimit;
import com.halo.stream.operator.IntMap;
import com.halo.stream.operator.IntMapIndexed;
import com.halo.stream.operator.IntMapToDouble;
import com.halo.stream.operator.IntMapToLong;
import com.halo.stream.operator.IntMapToObj;
import com.halo.stream.operator.IntPeek;
import com.halo.stream.operator.IntRangeClosed;
import com.halo.stream.operator.IntSample;
import com.halo.stream.operator.IntScan;
import com.halo.stream.operator.IntScanIdentity;
import com.halo.stream.operator.IntSkip;
import com.halo.stream.operator.IntSorted;
import com.halo.stream.operator.IntTakeUntil;
import com.halo.stream.operator.IntTakeWhile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * A sequence of primitive int-valued elements supporting sequential operations. This is the {@code int}
 * primitive specialization of {@link Stream}.
 */
public final class IntStream implements Closeable {

    /**
     * Single instance for empty stream. It is safe for multi-thread environment because it has no content.
     */
    private static final IntStream EMPTY = new IntStream(new PrimitiveIterator.OfInt() {
        @Override
        public int nextInt() {
            return 0;
        }

        @Override
        public boolean hasNext() {
            return false;
        }
    });

    /**
     * Returns an empty stream.
     *
     * @return the empty stream
     */
    @NotNull
    public static IntStream empty() {
        return EMPTY;
    }

    /**
     * Creates a {@code IntStream} from {@code PrimitiveIterator.OfInt}.
     *
     * @param iterator  the iterator with elements to be passed to stream
     * @return the new {@code IntStream}
     * @throws NullPointerException if {@code iterator} is null
     */
    @NotNull
    public static IntStream of(@NotNull PrimitiveIterator.OfInt iterator) {
        Objects.requireNonNull(iterator);
        return new IntStream(iterator);
    }

    /**
     * Returns stream whose elements are the specified values.
     *
     * @param values the elements of the new stream
     * @return the new stream
     * @throws NullPointerException if {@code values} is null
     */
    @NotNull
    public static IntStream of(@NotNull final int... values) {
        Objects.requireNonNull(values);
        if (values.length == 0) {
            return IntStream.empty();
        }
        return new IntStream(new IntArray(values));
    }

    /**
     * Returns stream which contains single element passed as parameter.
     *
     * @param t element of the stream
     * @return the new stream
     */
    @NotNull
    public static IntStream of(final int t) {
        return new IntStream(new IntArray(new int[] { t }));
    }

    /**
     * Creates an {@code IntStream} of code point values from the given sequence.
     * Any surrogate pairs encountered in the sequence are combined as if by {@linkplain
     * Character#toCodePoint Character.toCodePoint} and the result is passed to the stream.
     * Any other code units, including ordinary BMP characters, unpaired surrogates, and
     * undefined code units, are zero-extended to {@code int} values which are then
     * passed to the stream.
     *
     * @param charSequence  the sequence where to get all code points values.
     * @return the new stream
     * @since 1.1.8
     */
    @NotNull
    public static IntStream ofCodePoints(@NotNull CharSequence charSequence) {
        return new IntStream(new IntCodePoints(charSequence));
    }

    /**
     * Returns a sequential ordered {@code IntStream} from {@code startInclusive}
     * (inclusive) to {@code endExclusive} (exclusive) by an incremental step of
     * {@code 1}.
     *
     * @param startInclusive the (inclusive) initial value
     * @param endExclusive the exclusive upper bound
     * @return a sequential {@code IntStream} for the range of {@code int}
     *         elements
     */
    @NotNull
    public static IntStream range(final int startInclusive, final int endExclusive) {
        if (startInclusive >= endExclusive) {
            return empty();
        }
        return rangeClosed(startInclusive, endExclusive - 1);
    }

    /**
     * Returns a sequential ordered {@code IntStream} from {@code startInclusive}
     * (inclusive) to {@code endInclusive} (inclusive) by an incremental step of
     * {@code 1}.
     *
     * @param startInclusive the (inclusive) initial value
     * @param endInclusive the inclusive upper bound
     * @return a sequential {@code IntStream} for the range of {@code int}
     *         elements
     */
    @NotNull
    public static IntStream rangeClosed(final int startInclusive, final int endInclusive) {
        if (startInclusive > endInclusive) {
            return empty();
        } else if (startInclusive == endInclusive) {
            return of(startInclusive);
        } else {
            return new IntStream(new IntRangeClosed(startInclusive, endInclusive));
        }
    }

    /**
     * Returns an infinite sequential unordered stream where each element is
     * generated by the provided {@code IntSupplier}.  This is suitable for
     * generating constant streams, streams of random elements, etc.
     *
     * @param s the {@code IntSupplier} for generated elements
     * @return a new infinite sequential {@code IntStream}
     * @throws NullPointerException if {@code s} is null
     */
    @NotNull
    public static IntStream generate(@NotNull final IntSupplier s) {
        Objects.requireNonNull(s);
        return new IntStream(new IntGenerate(s));
    }

    /**
     * Returns an infinite sequential ordered {@code IntStream} produced by iterative
     * application of a function {@code f} to an initial element {@code seed},
     * producing a {@code Stream} consisting of {@code seed}, {@code f(seed)},
     * {@code f(f(seed))}, etc.
     *
     * <p> The first element (position {@code 0}) in the {@code IntStream} will be
     * the provided {@code seed}.  For {@code n > 0}, the element at position
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
     * @param f a function to be applied to the previous element to produce
     *          a new element
     * @return a new sequential {@code IntStream}
     * @throws NullPointerException if {@code f} is null
     */
    @NotNull
    public static IntStream iterate(final int seed,
                                    @NotNull final IntUnaryOperator f) {
        Objects.requireNonNull(f);
        return new IntStream(new IntIterate(seed, f));
    }

    /**
     * Creates an {@code IntStream} by iterative application {@code IntUnaryOperator} function
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
    public static IntStream iterate(
            final int seed,
            @NotNull final IntPredicate predicate,
            @NotNull final IntUnaryOperator op) {
        Objects.requireNonNull(predicate);
        return iterate(seed, op).takeWhile(predicate);
    }

    /**
     * Creates a lazily concatenated stream whose elements are all the
     * elements of the first stream followed by all the elements of the
     * second stream.
     *
     * <p>Example:
     * <pre>
     * stream a: [1, 2, 3, 4]
     * stream b: [5, 6]
     * result:   [1, 2, 3, 4, 5, 6]
     * </pre>
     *
     * @param a the first stream
     * @param b the second stream
     * @return the concatenation of the two input streams
     * @throws NullPointerException if {@code a} or {@code b} is null
     */
    @NotNull
    public static IntStream concat(
            @NotNull final IntStream a,
            @NotNull final IntStream b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        IntStream result = new IntStream(new IntConcat(a.iterator, b.iterator));
        return result.onClose(Compose.closeables(a, b));
    }

    private final PrimitiveIterator.OfInt iterator;
    private final Params params;

    private IntStream(PrimitiveIterator.OfInt iterator) {
        this(null, iterator);
    }

    IntStream(Params params, PrimitiveIterator.OfInt iterator) {
        this.params = params;
        this.iterator = iterator;
    }

    /**
     * Returns internal {@code IntStream} iterator.
     *
     * @return internal {@code IntStream} iterator.
     */
    public PrimitiveIterator.OfInt iterator() {
        return iterator;
    }

    /**
     * Applies custom operator on stream.
     *
     * Transforming function can return {@code IntStream} for intermediate operations,
     * or any value for terminal operation.
     *
     * <p>Operator examples:
     * <pre><code>
     *     // Intermediate operator
     *     public class Zip&lt;T&gt; implements Function&lt;IntStream, IntStream&gt; {
     *         &#64;Override
     *         public IntStream apply(IntStream firstStream) {
     *             final PrimitiveIterator.OfInt it1 = firstStream.iterator();
     *             final PrimitiveIterator.OfInt it2 = secondStream.iterator();
     *             return IntStream.of(new PrimitiveIterator.OfInt() {
     *                 &#64;Override
     *                 public boolean hasNext() {
     *                     return it1.hasNext() &amp;&amp; it2.hasNext();
     *                 }
     *
     *                 &#64;Override
     *                 public int nextInt() {
     *                     return combiner.applyAsInt(it1.nextInt(), it2.nextInt());
     *                 }
     *             });
     *         }
     *     }
     *
     *     // Intermediate operator based on existing stream operators
     *     public class SkipAndLimit implements UnaryOperator&lt;IntStream&gt; {
     *
     *         private final int skip, limit;
     *
     *         public SkipAndLimit(int skip, int limit) {
     *             this.skip = skip;
     *             this.limit = limit;
     *         }
     *
     *         &#64;Override
     *         public IntStream apply(IntStream stream) {
     *             return stream.skip(skip).limit(limit);
     *         }
     *     }
     *
     *     // Terminal operator
     *     public class Average implements Function&lt;IntStream, Double&gt; {
     *         long count = 0, sum = 0;
     *
     *         &#64;Override
     *         public Double apply(IntStream stream) {
     *             final PrimitiveIterator.OfInt it = stream.iterator();
     *             while (it.hasNext()) {
     *                 count++;
     *                 sum += it.nextInt();
     *             }
     *             return (count == 0) ? 0 : sum / (double) count;
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
    public <R> R custom(@NotNull final Function<IntStream, R> function) {
        Objects.requireNonNull(function);
        return function.apply(this);
    }

    /**
     * Returns a {@code Stream} consisting of the elements of this stream,
     * each boxed to an {@code Integer}.
     *
     * <p>This is an lazy intermediate operation.
     *
     * @return a {@code Stream} consistent of the elements of this stream,
     *         each boxed to an {@code Integer}
     */
    @NotNull
    public Stream<Integer> boxed() {
        return new Stream<Integer>(params, iterator);
    }

    /**
     * Returns a stream consisting of the elements of this stream that match
     * the given predicate.
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
     * @param predicate non-interfering, stateless predicate to apply to each
     *                  element to determine if it should be included
     * @return the new stream
     */
    @NotNull
    public IntStream filter(@NotNull final IntPredicate predicate) {
        return new IntStream(params, new IntFilter(iterator, predicate));
    }

    /**
     * Returns an {@code IntStream} with elements that satisfy the given {@code IndexedIntPredicate}.
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
     * @param predicate  the {@code IndexedIntPredicate} used to filter elements
     * @return the new stream
     * @since 1.2.1
     */
    @NotNull
    public IntStream filterIndexed(@NotNull IndexedIntPredicate predicate) {
        return filterIndexed(0, 1, predicate);
    }

    /**
     * Returns an {@code IntStream} with elements that satisfy the given {@code IndexedIntPredicate}.
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
     * @param predicate  the {@code IndexedIntPredicate} used to filter elements
     * @return the new stream
     * @since 1.2.1
     */
    @NotNull
    public IntStream filterIndexed(int from, int step,
                                   @NotNull IndexedIntPredicate predicate) {
        return new IntStream(params, new IntFilterIndexed(
                new PrimitiveIndexedIterator.OfInt(from, step, iterator),
                predicate));
    }

    /**
     * Returns a stream consisting of the elements of this stream that don't
     * match the given predicate.
     *
     * <p> This is an intermediate operation.
     *
     * @param predicate non-interfering, stateless predicate to apply to each
     *                  element to determine if it should not be included
     * @return the new stream
     */
    @NotNull
    public IntStream filterNot(@NotNull final IntPredicate predicate) {
        return filter(IntPredicate.Util.negate(predicate));
    }

    /**
     * Returns an {@code IntStream} consisting of the results of applying the given
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
     * @param mapper a non-interfering stateless function to apply to
     *               each element
     * @return the new {@code IntStream}
     */
    @NotNull
    public IntStream map(@NotNull final IntUnaryOperator mapper) {
        return new IntStream(params, new IntMap(iterator, mapper));
    }

    /**
     * Returns an {@code IntStream} with elements that obtained by applying the given {@code IntBinaryOperator}.
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
    public IntStream mapIndexed(@NotNull IntBinaryOperator mapper) {
        return mapIndexed(0, 1, mapper);
    }

    /**
     * Returns an {@code IntStream} with elements that obtained by applying the given {@code IntBinaryOperator}.
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
    public IntStream mapIndexed(int from, int step,
                                @NotNull IntBinaryOperator mapper) {
        return new IntStream(params, new IntMapIndexed(
                new PrimitiveIndexedIterator.OfInt(from, step, iterator),
                mapper));
    }

    /**
     * Returns a {@code Stream} consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p> This is an intermediate operation.
     *
     * @param <R> the type result
     * @param mapper the mapper function used to apply to each element
     * @return the new {@code Stream}
     */
    @NotNull
    public <R> Stream<R> mapToObj(@NotNull final IntFunction<? extends R> mapper) {
        return new Stream<R>(params, new IntMapToObj<R>(iterator, mapper));
    }

    /**
     * Returns a {@code LongStream} consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p> This is an intermediate operation.
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new {@code LongStream}
     * @since 1.1.4
     * @see #flatMap(com.halo.stream.function.IntFunction)
     */
    @NotNull
    public LongStream mapToLong(@NotNull final IntToLongFunction mapper) {
        return new LongStream(params, new IntMapToLong(iterator, mapper));
    }

    /**
     * Returns a {@code DoubleStream} consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p> This is an intermediate operation.
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new {@code DoubleStream}
     * @since 1.1.4
     * @see #flatMap(com.halo.stream.function.IntFunction)
     */
    @NotNull
    public DoubleStream mapToDouble(@NotNull final IntToDoubleFunction mapper) {
        return new DoubleStream(params, new IntMapToDouble(iterator, mapper));
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
     * @param mapper a non-interfering stateless function to apply to each
     *               element which produces an {@code IntStream} of new values
     * @return the new stream
     * @see Stream#flatMap(Function)
     */
    @NotNull
    public IntStream flatMap(@NotNull final IntFunction<? extends IntStream> mapper) {
        return new IntStream(params, new IntFlatMap(iterator, mapper));
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
    public IntStream distinct() {
        // While functional and quick to implement, this approach is not very efficient.
        // An efficient version requires an int-specific map/set implementation.
        return boxed().distinct().mapToInt(UNBOX_FUNCTION);
    }

    /**
     * Returns a stream consisting of the elements of this stream in sorted
     * order.
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
    public IntStream sorted() {
        return new IntStream(params, new IntSorted(iterator));
    }

    /**
     * Returns {@code IntStream} with sorted elements (as determinated by provided {@code Comparator}).
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
     * @return the new {@code IntStream}
     */
    @NotNull
    public IntStream sorted(@Nullable Comparator<Integer> comparator) {
        return boxed().sorted(comparator).mapToInt(UNBOX_FUNCTION);
    }

    /**
     * Samples the {@code IntStream} by emitting every n-th element.
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
     * @return the new {@code IntStream}
     * @throws IllegalArgumentException if {@code stepWidth} is zero or negative
     * @see Stream#sample(int)
     */
    @NotNull
    public IntStream sample(final int stepWidth) {
        if (stepWidth <= 0) throw new IllegalArgumentException("stepWidth cannot be zero or negative");
        if (stepWidth == 1) return this;
        return new IntStream(params, new IntSample(iterator, stepWidth));
    }

    /**
     * Returns a stream consisting of the elements of this stream, additionally
     * performing the provided action on each element as elements are consumed
     * from the resulting stream. Handy method for debugging purposes.
     *
     * <p>This is an intermediate operation.
     *
     * @param action the action to be performed on each element
     * @return the new stream
     */
    @NotNull
    public IntStream peek(@NotNull final IntConsumer action) {
        return new IntStream(params, new IntPeek(iterator, action));
    }

    /**
     * Returns a {@code IntStream} produced by iterative application of a accumulation function
     * to reduction value and next element of the current stream.
     * Produces a {@code IntStream} consisting of {@code value1}, {@code acc(value1, value2)},
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
    public IntStream scan(@NotNull final IntBinaryOperator accumulator) {
        Objects.requireNonNull(accumulator);
        return new IntStream(params, new IntScan(iterator, accumulator));
    }

    /**
     * Returns a {@code IntStream} produced by iterative application of a accumulation function
     * to an initial element {@code identity} and next element of the current stream.
     * Produces a {@code IntStream} consisting of {@code identity}, {@code acc(identity, value1)},
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
    public IntStream scan(final int identity,
                          @NotNull final IntBinaryOperator accumulator) {
        Objects.requireNonNull(accumulator);
        return new IntStream(params, new IntScanIdentity(iterator, identity, accumulator));
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
     * @return the new {@code IntStream}
     */
    @NotNull
    public IntStream takeWhile(@NotNull final IntPredicate predicate) {
        return new IntStream(params, new IntTakeWhile(iterator, predicate));
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
     * @return the new {@code IntStream}
     * @since 1.1.6
     */
    @NotNull
    public IntStream takeUntil(@NotNull final IntPredicate stopPredicate) {
        return new IntStream(params, new IntTakeUntil(iterator, stopPredicate));
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
     * @return the new {@code IntStream}
     */
    @NotNull
    public IntStream dropWhile(@NotNull final IntPredicate predicate) {
        return new IntStream(params, new IntDropWhile(iterator, predicate));
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
     * @param maxSize the number of elements the stream should be limited to
     * @return the new stream
     * @throws IllegalArgumentException if {@code maxSize} is negative
     */
    @NotNull
    public IntStream limit(final long maxSize) {
        if (maxSize < 0) {
            throw new IllegalArgumentException("maxSize cannot be negative");
        }
        if (maxSize == 0) {
            return IntStream.empty();
        }
        return new IntStream(params, new IntLimit(iterator, maxSize));
    }

    /**
     * Returns a stream consisting of the remaining elements of this stream
     * after discarding the first {@code n} elements of the stream.
     * If this stream contains fewer than {@code n} elements then an
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
     * @param n the number of leading elements to skip
     * @return the new stream
     * @throws IllegalArgumentException if {@code n} is negative
     */
    @NotNull
    public IntStream skip(final long n) {
        if (n < 0) {
            throw new IllegalArgumentException("n cannot be negative");
        } else if (n == 0) {
            return this;
        } else {
            return new IntStream(params, new IntSkip(iterator, n));
        }
    }

    /**
     * Performs an action for each element of this stream.
     *
     * <p>This is a terminal operation.
     *
     * @param action a non-interfering action to perform on the elements
     */
    public void forEach(@NotNull IntConsumer action) {
        while(iterator.hasNext()) {
            action.accept(iterator.nextInt());
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
    public void forEachIndexed(@NotNull IndexedIntConsumer action) {
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
                               @NotNull IndexedIntConsumer action) {
        int index = from;
        while (iterator.hasNext()) {
            action.accept(index, iterator.nextInt());
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
     * @param identity the identity value for the accumulating function
     * @param op an associative non-interfering stateless function for
     *           combining two values
     * @return the result of the reduction
     * @see #sum()
     * @see #min()
     * @see #max()
     */
    public int reduce(int identity, @NotNull IntBinaryOperator op) {
        int result = identity;
        while(iterator.hasNext()) {
            int value = iterator.nextInt();
            result = op.applyAsInt(result, value);
        }
        return result;
    }

    /**
     * Performs a reduction on the elements of this stream, using an
     * associative accumulation function, and returns an {@code OptionalInt}
     * describing the reduced value, if any.
     *
     * <p>The {@code op} function must be an associative function.
     *
     * <p>This is a terminal operation.
     *
     * @param op an associative, non-interfering, stateless function for
     *           combining two values
     * @return the result of the reduction
     * @see #reduce(int, IntBinaryOperator)
     */
    @NotNull
    public OptionalInt reduce(@NotNull IntBinaryOperator op) {
        boolean foundAny = false;
        int result = 0;
        while(iterator.hasNext()) {
            int value = iterator.nextInt();

            if(!foundAny) {
                foundAny = true;
                result = value;
            } else {
                result = op.applyAsInt(result, value);
            }
        }
        return foundAny ? OptionalInt.of(result) : OptionalInt.empty();
    }

    /**
     * Returns an array containing the elements of this stream.
     *
     * <p>This is a terminal operation.
     *
     * @return an array containing the elements of this stream
     */
    @NotNull
    public int[] toArray() {
        return Operators.toIntArray(iterator);
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
                         @NotNull ObjIntConsumer<R> accumulator) {
        R result = supplier.get();
        while (iterator.hasNext()) {
            final int value = iterator.nextInt();
            accumulator.accept(result, value);
        }
        return result;
    }

    /**
     * Returns the sum of elements in this stream.
     *
     * @return the sum of elements in this stream
     */
    public int sum() {
        int sum = 0;
        while(iterator.hasNext()) {
            sum += iterator.nextInt();
        }

        return sum;
    }

    /**
     * Returns an {@code OptionalInt} describing the minimum element of this
     * stream, or an empty optional if this stream is empty.
     *
     * <p>This is a terminal operation.
     *
     * @return an {@code OptionalInt} containing the minimum element of this
     *         stream, or an empty {@code OptionalInt} if the stream is empty
     */
    @NotNull
    public OptionalInt min() {
        return reduce(new IntBinaryOperator() {
            @Override
            public int applyAsInt(int left, int right) {
                return left < right ? left : right;
            }
        });
    }

    /**
     * Returns an {@code OptionalInt} describing the maximum element of this
     * stream, or an empty optional if this stream is empty.
     *
     * <p>This is a terminal operation.
     *
     * @return an {@code OptionalInt} containing the maximum element of this
     *         stream, or an empty {@code OptionalInt} if the stream is empty
     */
    @NotNull
    public OptionalInt max() {
        return reduce(new IntBinaryOperator() {
            @Override
            public int applyAsInt(int left, int right) {
                return left > right ? left : right;
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
        while(iterator.hasNext()) {
            iterator.nextInt();
            count++;
        }
        return count;
    }

    /**
     * Returns whether any elements of this stream match the provided
     * predicate. May not evaluate the predicate on all elements if not
     * necessary for determining the result.  If the stream is empty then
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
     * @param predicate a non-interfering stateless predicate to apply
     *                  to elements of this stream
     * @return {@code true} if any elements of the stream match the provided
     *         predicate, otherwise {@code false}
     */
    public boolean anyMatch(@NotNull IntPredicate predicate) {
        while(iterator.hasNext()) {
            if(predicate.test(iterator.nextInt()))
                return true;
        }

        return false;
    }

    /**
     * Returns whether all elements of this stream match the provided predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result.  If the stream is empty then {@code true} is
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
     * @param predicate a non-interfering stateless predicate to apply to
     *                  elements of this stream
     * @return {@code true} if either all elements of the stream match the
     *         provided predicate or the stream is empty, otherwise {@code false}
     */
    public boolean allMatch(@NotNull IntPredicate predicate) {
        while(iterator.hasNext()) {
            if(!predicate.test(iterator.nextInt()))
                return false;
        }

        return true;
    }

    /**
     * Returns whether no elements of this stream match the provided predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result.  If the stream is empty then {@code true} is
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
     * @param predicate a non-interfering stateless predicate to apply to
     *                  elements of this stream
     * @return {@code true} if either no elements of the stream match the
     *         provided predicate or the stream is empty, otherwise {@code false}
     */
    public boolean noneMatch(@NotNull IntPredicate predicate) {
        while (iterator.hasNext()) {
            if (predicate.test(iterator.nextInt()))
                return false;
        }
        return true;
    }

    /**
     * Returns an {@link OptionalInt} describing the first element of this
     * stream, or an empty {@code OptionalInt} if the stream is empty.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * @return an {@code OptionalInt} describing the first element of this stream,
     *         or an empty {@code OptionalInt} if the stream is empty
     */
    @NotNull
    public OptionalInt findFirst() {
        if (iterator.hasNext()) {
            return OptionalInt.of(iterator.nextInt());
        } else {
            return OptionalInt.empty();
        }
    }

    /**
     * Returns the last element wrapped by {@code OptionalInt} class.
     * If stream is empty, returns {@code OptionalInt.empty()}.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * @return an {@code OptionalInt} with the last element
     *         or {@code OptionalInt.empty()} if the stream is empty
     * @since 1.1.8
     */
    @NotNull
    public OptionalInt findLast() {
        return reduce(new IntBinaryOperator() {
            @Override
            public int applyAsInt(int left, int right) {
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
     * @since 1.1.3
     */
    public int single() {
        if (iterator.hasNext()) {
            int singleCandidate = iterator.nextInt();
            if (iterator.hasNext()) {
                throw new IllegalStateException("IntStream contains more than one element");
            } else {
                return singleCandidate;
            }
        } else {
            throw new NoSuchElementException("IntStream contains no element");
        }
    }

    /**
     * Returns the single element wrapped by {@code OptionalInt} class.
     * If stream is empty, returns {@code OptionalInt.empty()}.
     * If stream contains more than one element, throws {@code IllegalStateException}.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * <p>Example:
     * <pre>
     * stream: []
     * result: OptionalInt.empty()
     *
     * stream: [1]
     * result: OptionalInt.of(1)
     *
     * stream: [1, 2, 3]
     * result: IllegalStateException
     * </pre>
     *
     * @return an {@code OptionalInt} with single element or {@code OptionalInt.empty()} if stream is empty
     * @throws IllegalStateException if stream contains more than one element
     * @since 1.1.3
     */
    @NotNull
    public OptionalInt findSingle() {
        if (iterator.hasNext()) {
            int singleCandidate = iterator.nextInt();
            if (iterator.hasNext()) {
                throw new IllegalStateException("IntStream contains more than one element");
            } else {
                return OptionalInt.of(singleCandidate);
            }
        } else {
            return OptionalInt.empty();
        }
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
    public IntStream onClose(@NotNull final Runnable closeHandler) {
        Objects.requireNonNull(closeHandler);
        final Params newParams = Params.wrapWithCloseHandler(params, closeHandler);
        return new IntStream(newParams, iterator);
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


    private static final ToIntFunction<Integer> UNBOX_FUNCTION = new ToIntFunction<Integer>() {
        @Override
        public int applyAsInt(Integer t) {
            return t;
        }
    };
}