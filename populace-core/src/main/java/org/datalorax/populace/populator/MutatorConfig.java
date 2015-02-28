package org.datalorax.populace.populator;


import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds details of all configured {@link org.datalorax.populace.populator.Mutator Mutator} instances.
 * @author datalorax - 27/02/2015.
 */
public class MutatorConfig {
    private final Map<Type, Mutator> specificMutators;
    private final Map<Class<?>, Mutator> baseMutators;
    private final Mutator defaultArrayMutator;
    private final Mutator defaultMutator;

    public MutatorConfig(Map<Type, Mutator> specificMutators, Map<Class<?>, Mutator> baseMutators,
                         Mutator defaultMutator, Mutator defaultArrayMutator) {
        Validate.notNull(baseMutators, "baseMutators null");
        Validate.notNull(specificMutators, "specificMutators null");
        Validate.notNull(defaultMutator, "defaultMutator null");
        Validate.notNull(defaultArrayMutator, "defaultArrayMutator null");
        this.baseMutators = Collections.unmodifiableMap(new HashMap<Class<?>, Mutator>(baseMutators));
        this.specificMutators = Collections.unmodifiableMap(new HashMap<Type, Mutator>(specificMutators));
        this.defaultMutator = defaultMutator;
        this.defaultArrayMutator = defaultArrayMutator;
    }

    public interface Builder {
        Builder withSpecificMutator(Type type, Mutator mutator);

        Builder withBaseMutator(Class<?> baseClass, Mutator mutator);

        Builder withDefaultArrayMutator(Mutator mutator);

        Builder withDefaultMutator(Mutator mutator);

        MutatorConfig build();
    }

    public Mutator getMutator(Type type) {
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

    private Mutator getSpecificMutator(Type type) {
        return specificMutators.get(type);
    }

    private Mutator getBaseMutator(Type type) {
        Map.Entry<Class<?>, Mutator> bestMatch = null;

        for (Map.Entry<Class<?>, Mutator>
                entry :
                baseMutators.entrySet()) {
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