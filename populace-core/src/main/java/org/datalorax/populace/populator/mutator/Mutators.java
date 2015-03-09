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

package org.datalorax.populace.populator.mutator;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.typed.ImmutableTypeMap;

import java.lang.reflect.Type;

/**
 * Helper functions for working with {@link org.datalorax.populace.populator.Mutator mutators}
 *
 * @author Andrew Coates - 01/03/2015.
 */
public class Mutators {
    private final ImmutableTypeMap<Mutator> mutators;

    /**
     * @return the default set of {@link org.datalorax.populace.populator.Mutator mutators} defined by the system
     */
    public static Mutators defaults() {
        return MutatorsBuilder.defaults();
    }

    /**
     * @return a new Mutators builder, initialised with the defaults in the system.
     */
    public static Builder newBuilder() {
        return asBuilder(defaults());
    }

    /**
     * Convert an existing immutable set of mutators into a new builder instance
     * @param source the source set of mutators. The builder will be pre configured with all the mutators in this set
     * @return a new Mutators builder, initialised with the mutators in {@code source}
     */
    public static Builder asBuilder(final Mutators source) {
        return new MutatorsBuilder(source.mutators);
    }

    public interface Builder {
        Builder withSpecificMutator(final Type type, final Mutator mutator);

        Builder withSuperMutator(final Class<?> baseClass, final Mutator mutator);

        Builder withArrayDefaultMutator(final Mutator mutator);

        Builder withDefaultMutator(final Mutator mutator);

        Mutators build();
    }

    public Mutator get(final Type key) {
        return mutators.get(key);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Mutators that = (Mutators) o;
        return mutators.equals(that.mutators);
    }

    @Override
    public int hashCode() {
        return mutators.hashCode();
    }

    @Override
    public String toString() {
        return "Mutators{" +
            " mutators=" + mutators +
            '}';
    }

    Mutators(final ImmutableTypeMap<Mutator> mutators) {
        Validate.notNull(mutators, "mutators null");
        this.mutators = mutators;
    }
}
