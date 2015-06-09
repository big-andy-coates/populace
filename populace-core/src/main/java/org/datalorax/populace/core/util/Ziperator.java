/*
 * Copyright (c) 2015 Andrew Coates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.datalorax.populace.core.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

/**
 * Zips two iterators together, creating an iterator of pairs composed of one value from each iterator.
 *
 * @author Andrew Coates - 18/05/2015.
 */
public class Ziperator<TIn1, TIn2, TOut1, TOut2> implements Iterator<Pair<TOut1, TOut2>> {
    private final Iterator<TIn1> first;
    private final Iterator<TIn2> second;
    private final boolean outer;
    private final Function<Iterator<TIn1>, TOut1> adapter1;
    private final Function<Iterator<TIn2>, TOut2> adapter2;

    private Ziperator(final Iterator<TIn1> first, final Iterator<TIn2> second, final boolean outer,
                      final Function<Iterator<TIn1>, TOut1> adapter1, final Function<Iterator<TIn2>, TOut2> adapter2) {
        this.first = first;
        this.second = second;
        this.outer = outer;
        this.adapter1 = adapter1;
        this.adapter2 = adapter2;
    }

    /**
     * Create an inner join Ziperator. An inner join means that the resulting iteration sequence will terminate as soon
     * as either input iterators run out of elements.
     *
     * @param first  the first iterator to zip together
     * @param second the second iterator to zip together
     * @param <T1>   The input type of the first iterator
     * @param <T2>   the input type of the second iterator
     * @return An iterator over the zipped sequence of both input iterators.
     */
    public static <T1, T2> Iterator<Pair<T1, T2>> innerZip(final Iterator<T1> first,
                                                           final Iterator<T2> second) {
        class InnerJoin<T> implements Function<Iterator<T>, T> {
            @Override
            public T apply(final Iterator<T> it) {
                if (!it.hasNext()) {
                    throw new NoSuchElementException();
                }

                return it.next();
            }
        }

        return new Ziperator<>(first, second, false, new InnerJoin<>(), new InnerJoin<>());
    }

    /**
     * Create an outer join Ziperator. An outer join means that the resulting iteration sequence will continue as long
     * as one of the input iterators continues.
     *
     * @param first  the first iterator to zip together
     * @param second the second iterator to zip together
     * @param <T1>   The input type of the first iterator
     * @param <T2>   the input type of the second iterator
     * @return An iterator over the zipped sequence of both input iterators.
     */
    public static <T1, T2> Iterator<Pair<Optional<T1>, Optional<T2>>> outerZip(final Iterator<T1> first,
                                                                               final Iterator<T2> second) {
        class OuterJoin<T> implements Function<Iterator<T>, Optional<T>> {
            @Override
            public Optional<T> apply(final Iterator<T> it) {
                return it.hasNext() ? Optional.of(it.next()) : Optional.<T>empty();
            }
        }

        return new Ziperator<>(first, second, true, new OuterJoin<>(), new OuterJoin<>());
    }

    /**
     * Returns {@code true} if the iteration has more elements. (In other words, returns {@code true} if {@link #next}
     * would return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        return (outer && (first.hasNext() || second.hasNext())) || first.hasNext() && second.hasNext();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public Pair<TOut1, TOut2> next() {
        final TOut1 one = adapter1.apply(first);
        final TOut2 two = adapter2.apply(second);
        return new Pair<>(one, two);
    }
}
