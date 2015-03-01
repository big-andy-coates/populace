package org.datalorax.populace.populator;


import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// Todo(ac): switch to using TypedCollection.
/**
 * Holds details of all configured {@link org.datalorax.populace.populator.Mutator Mutator} instances.
 * Mutators can be registered as one of:
 * <ul>
 * <li>
 * Specific - meaning they match an exact {@link java.lang.reflect.Type type}. This includes specific array types and
 * generic types. Use {@link org.datalorax.populace.populator.MutatorConfig.Builder#withSpecificMutator(java.lang.reflect.Type, Mutator)}.
 * </li>
 * <li>
 * Subtype - meaning they match any sub-type of the raw {@link java.lang.reflect.Type type}
 * Use {@link org.datalorax.populace.populator.MutatorConfig.Builder#withSubTypeMutator(Class, Mutator)}.
 * </li>
 * <li>
 * Default - meaning they will be used if no other mutator matches and the {@link java.lang.reflect.Type type} is not an array type
 * Use {@link org.datalorax.populace.populator.MutatorConfig.Builder#withDefaultMutator(Mutator)}
 * </li>
 * <li>
 * Default Array - meaning they will be used if no other mutator matches and the {@link java.lang.reflect.Type type} is an array type
 * Use {@link org.datalorax.populace.populator.MutatorConfig.Builder#withDefaultArrayMutator(Mutator)}
 * </li>
 * </ul>
 *
 * @author datalorax - 27/02/2015.
 */
public class MutatorConfig {
    private final Map<Type, Mutator> specificMutators;
    private final Map<Class<?>, Mutator> subTypeMutators;
    private final Mutator defaultArrayMutator;
    private final Mutator defaultMutator;

    public static Builder newBuilder() {
        return new MutatorConfigBuilder();
    }

    public interface Builder {
        Builder withSpecificMutator(final Type type, final Mutator mutator);

        Builder withSubTypeMutator(final Class<?> baseClass, final Mutator mutator);

        Builder withDefaultArrayMutator(final Mutator mutator);

        Builder withDefaultMutator(final Mutator mutator);

        MutatorConfig build();
    }

    public Mutator getMutator(final Type type) {
        Validate.notNull(type, "type null");
        Mutator mutator = getSpecificMutator(type);
        if (mutator != null) {
            return mutator;
        }

        if (TypeUtils.isArrayType(type)) {
            return defaultArrayMutator;
        }

        mutator = getBaseMutator(type);
        return mutator == null ? defaultMutator : mutator;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final MutatorConfig config = (MutatorConfig) o;
        return defaultArrayMutator.equals(config.defaultArrayMutator) &&
                defaultMutator.equals(config.defaultMutator) &&
                specificMutators.equals(config.specificMutators) &&
                subTypeMutators.equals(config.subTypeMutators);
    }

    @Override
    public int hashCode() {
        int result = specificMutators.hashCode();
        result = 31 * result + subTypeMutators.hashCode();
        result = 31 * result + defaultArrayMutator.hashCode();
        result = 31 * result + defaultMutator.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MutatorConfig{" +
                "specificMutators=" + specificMutators +
                ", subTypeMutators=" + subTypeMutators +
                ", defaultArrayMutator=" + defaultArrayMutator +
                ", defaultMutator=" + defaultMutator +
                '}';
    }

    MutatorConfig(final Map<Type, Mutator> specificMutators, final Map<Class<?>, Mutator> subTypeMutators,
                  final Mutator defaultMutator, final Mutator defaultArrayMutator) {
        Validate.notNull(subTypeMutators, "subTypeMutators null");
        Validate.notNull(specificMutators, "specificMutators null");
        Validate.notNull(defaultMutator, "defaultMutator null");
        Validate.notNull(defaultArrayMutator, "defaultArrayMutator null");
        this.subTypeMutators = Collections.unmodifiableMap(new HashMap<Class<?>, Mutator>(subTypeMutators));
        this.specificMutators = Collections.unmodifiableMap(new HashMap<Type, Mutator>(specificMutators));
        this.defaultMutator = defaultMutator;
        this.defaultArrayMutator = defaultArrayMutator;
    }

    private Mutator getSpecificMutator(final Type type) {
        return specificMutators.get(type);
    }

    private Mutator getBaseMutator(final Type type) {
        Map.Entry<Class<?>, Mutator> bestMatch = null;

        for (Map.Entry<Class<?>, Mutator> entry : subTypeMutators.entrySet()) {
            if (!TypeUtils.isAssignable(type, entry.getKey())) {
                continue;
            }

            if (bestMatch == null || TypeUtils.isAssignable(entry.getKey(), bestMatch.getKey())) {
                // First, or more specific match found:
                bestMatch = entry;
            }
        }

        return bestMatch == null ? null : bestMatch.getValue();
    }
}