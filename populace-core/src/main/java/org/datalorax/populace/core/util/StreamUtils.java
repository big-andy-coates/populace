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

import org.apache.commons.lang3.tuple.Pair;

import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Andrew Coates - 18/05/2015.
 */
public final class StreamUtils {

    private StreamUtils() {
    }

    public static <T1, T2> Stream<Pair<Optional<T1>, Optional<T2>>> outerZip(final Stream<T1> s1, final Stream<T2> s2,
                                                                             final int characteristics) {
        final boolean isParallel = s1.isParallel() || s2.isParallel();
        final int actualCharacteristics = characteristics | Spliterator.NONNULL;
        final Iterator<Pair<Optional<T1>, Optional<T2>>> ziperator = Ziperator.outerZip(s1.iterator(), s2.iterator());
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(ziperator, actualCharacteristics), isParallel);
    }

    public static <T1, T2> Stream<Pair<T1, T2>> innerZip(final Stream<T1> s1, final Stream<T2> s2,
                                                         final int characteristics) {
        final boolean isParallel = s1.isParallel() || s2.isParallel();
        final int actualCharacteristics = characteristics | Spliterator.NONNULL;
        final Iterator<Pair<T1, T2>> ziperator = Ziperator.innerZip(s1.iterator(), s2.iterator());
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(ziperator, actualCharacteristics), isParallel);
    }
}
