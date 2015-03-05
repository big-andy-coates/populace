package org.datalorax.populace.populator.mutator;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.typed.ImmutableTypeMap;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Helper functions for working with {@link org.datalorax.populace.populator.Mutator mutators}
 *
 * @author datalorax - 01/03/2015.
 */
public class Mutators {
    private final ImmutableTypeMap<Mutator> mutators;

    public static Builder newBuilder() {
        return MutatorsBuilder.defaults();
    }

    public static Builder asBuilder(final Mutators source) {
        return new MutatorsBuilder(source.mutators);
    }

    public interface Builder {
        Builder withSpecificMutators(final Map<Type, ? extends Mutator> mutators);

        Builder withSpecificMutator(final Type type, final Mutator mutator);

        Builder withSuperMutators(final Map<Class<?>, ? extends Mutator> mutators);

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
