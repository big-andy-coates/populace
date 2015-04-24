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

package org.datalorax.populace.core.populate.mutator;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.populate.Mutator;
import org.datalorax.populace.core.populate.mutator.commbination.ChainedMutator;
import org.datalorax.populace.core.util.ImmutableTypeMap;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Helper functions for working with {@link org.datalorax.populace.core.populate.Mutator mutators}
 *
 * @author Andrew Coates - 01/03/2015.
 */
public class Mutators {
    private final ImmutableTypeMap<Mutator> mutators;

    Mutators(final ImmutableTypeMap<Mutator> mutators) {
        Validate.notNull(mutators, "mutators null");
        this.mutators = mutators;
    }

    /**
     * @return the default set of {@link org.datalorax.populace.core.populate.Mutator mutators} defined by the system
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

    /**
     * Chain to mutators together. All mutators will always be called, with the output from one mutator becoming the
     * input value to the next.
     * @param first the first mutator to call
     * @param second the second mutator to call
     * @param additional additional mutators to call
     * @return A {@link Mutator} encapsulating all the supplied mutators.
     */
    public static Mutator chain(final Mutator first, final Mutator second, final Mutator... additional) {
        return ChainedMutator.chain(first, second, additional);
    }

    /**
     * Get the mutator most specific to the provided {@code type}.
     *
     * @param type the specific type to find
     * @return the mutator
     * @see org.datalorax.populace.core.util.ImmutableTypeMap#get(java.lang.reflect.Type) for details
     */
    public Mutator get(final Type type) {
        return mutators.get(type);
    }

    /**
     * Get the mutator registered against the specific {@code type} provided, is present.
     *
     * @param type the specific type to find.
     * @return the mutator if found, else Optional.empty()
     * @see org.datalorax.populace.core.util.ImmutableTypeMap#getSpecific(java.lang.reflect.Type)
     */
    public Optional<Mutator> getSpecific(final Type type) {
        return Optional.ofNullable(mutators.getSpecific(type));
    }

    /**
     * Get the most specific super mutator registered for the {@code type} provided, is present.
     *
     * @param type the super type to find.
     * @return the mutator if found, else Optional.empty()
     * @see org.datalorax.populace.core.util.ImmutableTypeMap#getSuper(Class)
     */
    public Optional<Mutator> getSuper(final Class<?> type) {
        return Optional.ofNullable(mutators.getSuper(type));
    }

    /**
     * Get the default mutator for array types.
     *
     * @return the mutator
     * @see org.datalorax.populace.core.util.ImmutableTypeMap#getArrayDefault()
     */
    public Mutator getArrayDefault() {
        return mutators.getArrayDefault();
    }

    /**
     * Get the default mutator for none-array types.
     *
     * @return the mutator
     * @see org.datalorax.populace.core.util.ImmutableTypeMap#getDefault()
     */
    public Mutator getDefault() {
        return mutators.getDefault();
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

    public interface Builder {
        Builder withSpecificMutator(final Type type, final Mutator mutator);

        Builder withSuperMutator(final Class<?> baseClass, final Mutator mutator);

        Builder withArrayDefaultMutator(final Mutator mutator);

        Builder withDefaultMutator(final Mutator mutator);

        Mutators build();
    }
}
