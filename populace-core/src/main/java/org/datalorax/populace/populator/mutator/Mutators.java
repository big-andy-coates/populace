package org.datalorax.populace.populator.mutator;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.mutator.change.ChangeEnumMutator;
import org.datalorax.populace.populator.mutator.change.ChangeListElementsMutator;
import org.datalorax.populace.populator.mutator.change.ChangeMapValuesMutator;
import org.datalorax.populace.populator.mutator.change.ChangeSetElementsMutator;
import org.datalorax.populace.populator.mutator.commbination.ChainMutator;
import org.datalorax.populace.populator.mutator.ensure.EnsureCollectionNotEmptyMutator;
import org.datalorax.populace.populator.mutator.ensure.EnsureMapNotEmptyMutator;
import org.datalorax.populace.populator.mutator.ensure.EnsureMutator;
import org.datalorax.populace.typed.TypeMap;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Helper functions for working with {@link org.datalorax.populace.populator.Mutator mutators}
 *
 * @author datalorax - 01/03/2015.
 */
public final class Mutators {
    private static final Map<Type, Mutator> DEFAULT_SPECIFIC_MUTATORS;
    private static final Map<Class<?>, Mutator> DEFAULT_SUPER_MUTATORS;

    public static TypeMap.Builder<Mutator> defaultMutators() {
        return setDefaultMutators(TypeMap.<Mutator>newBuilder());
    }

    public static TypeMap.Builder<Mutator> setDefaultMutators(final TypeMap.Builder<Mutator> builder) {
        builder.withSpecificTypes(DEFAULT_SPECIFIC_MUTATORS)
            .withSuperTypes(DEFAULT_SUPER_MUTATORS)
            //.withDefaultArray(Mutators.chainMutators(EnsureArrayMutator.INSTANCE, ArrayMutator.INSTANCE))
            .withDefaultArray(ArrayMutator.INSTANCE)
            .withDefault(EnsureMutator.INSTANCE);
        return builder;
    }

    public static Mutator chainMutators(final Mutator first, final Mutator second, final Mutator... additional) {
        return ChainMutator.chain(first, second, additional);
    }

    static {
        final Map<Type, Mutator> specificMutators = new HashMap<Type, Mutator>();
        final Type[] primitiveTypes = {boolean.class, byte.class, char.class, short.class, int.class, long.class, float.class, double.class,
            Boolean.class, Byte.class, Character.class, Short.class, Integer.class, Long.class, Float.class, Double.class};
        for (Type primitiveType : primitiveTypes) {
            specificMutators.put(primitiveType, PrimitiveMutator.INSTANCE);
        }

        // Todo(ac): what about other java lang types..
        specificMutators.put(String.class, StringMutator.INSTANCE);
        specificMutators.put(Date.class, DateMutator.INSTANCE);

        DEFAULT_SPECIFIC_MUTATORS = Collections.unmodifiableMap(specificMutators);

        Map<Class<?>, Mutator> superMutators = new HashMap<Class<?>, Mutator>();
        superMutators.put(Set.class, chainMutators(EnsureMutator.INSTANCE, ChangeSetElementsMutator.INSTANCE));
        superMutators.put(List.class, chainMutators(EnsureMutator.INSTANCE, EnsureCollectionNotEmptyMutator.INSTANCE, ChangeListElementsMutator.INSTANCE));
        superMutators.put(Map.class, chainMutators(EnsureMutator.INSTANCE, EnsureMapNotEmptyMutator.INSTANCE, ChangeMapValuesMutator.INSTANCE));
        superMutators.put(Enum.class, chainMutators(EnsureMutator.INSTANCE, ChangeEnumMutator.INSTANCE));

        DEFAULT_SUPER_MUTATORS = Collections.unmodifiableMap(superMutators);
    }

    private Mutators() {
    }
}
