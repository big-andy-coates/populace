package org.datalorax.populace.populator;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.populator.mutator.*;

import java.lang.reflect.Type;
import java.util.*;

/**
*  Builder implementation for MutatorConfig
 * @author datalorax - 28/02/2015.
*/
class MutatorConfigBuilder implements MutatorConfig.Builder {
    private static final Map<Type, Mutator> DEFAULT_SPECIFIC_MUTATORS;
    private static final Map<Class<?>, Mutator> DEFAULT_BASE_MUTATORS;

    static {
        final Map<Type, Mutator> specificMutators = new HashMap<Type, Mutator>();
        final Mutator primitivesMutator = new PrimitiveMutator();
        final Type[] primitiveTypes = {boolean.class, byte.class, char.class, short.class, int.class, long.class, float.class, double.class,
                Boolean.class, Byte.class, Character.class, Short.class, Integer.class, Long.class, Float.class, Double.class};
        for (Type primitiveType : primitiveTypes) {
            specificMutators.put(primitiveType, primitivesMutator);
        }

        specificMutators.put(String.class, new StringMutator());
        specificMutators.put(Date.class, new DateMutator());

        DEFAULT_SPECIFIC_MUTATORS = Collections.unmodifiableMap(specificMutators);

        Map<Class<?>, Mutator> baseMutators = new HashMap<Class<?>, Mutator>();
        baseMutators.put(List.class, new ListMutator(ArrayList.class));
        baseMutators.put(Set.class, new SetMutator(HashSet.class));
        baseMutators.put(Map.class, new MapMutator(HashMap.class));

        DEFAULT_BASE_MUTATORS = Collections.unmodifiableMap(baseMutators);
    }

    private Mutator defaultArrayMutator = new StdArrayMutator();
    private Mutator defaultMutator = new StdMutator();
    private final Map<Type, Mutator> specificMutators = new HashMap<Type, Mutator>(DEFAULT_SPECIFIC_MUTATORS);
    private final Map<Class<?>, Mutator> baseMutators = new HashMap<Class<?>, Mutator>(DEFAULT_BASE_MUTATORS);

    @Override
    public MutatorConfigBuilder withSpecificMutator(Type type, Mutator mutator) {
        Validate.notNull(type, "type null");
        Validate.notNull(mutator, "mutator null");
        specificMutators.put(type, mutator);
        return this;
    }

    @Override
    public MutatorConfigBuilder withBaseMutator(Class<?> baseClass, Mutator mutator) {
        Validate.notNull(baseClass, "baseClass null");
        Validate.notNull(mutator, "mutator null");
        baseMutators.put(baseClass, mutator);
        return this;
    }

    @Override
    public MutatorConfigBuilder withDefaultArrayMutator(Mutator mutator) {
        Validate.notNull(mutator, "mutator null");
        defaultArrayMutator = mutator;
        return this;
    }

    @Override
    public MutatorConfigBuilder withDefaultMutator(Mutator mutator) {
        Validate.notNull(mutator, "mutator null");
        defaultMutator = mutator;
        return this;
    }

    @Override
    public MutatorConfig build() {
        return new MutatorConfig(specificMutators, baseMutators, defaultMutator, defaultArrayMutator);
    }
}
