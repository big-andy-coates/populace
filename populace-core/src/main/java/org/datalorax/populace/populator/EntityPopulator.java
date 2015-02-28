package org.datalorax.populace.populator;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.populator.mutator.*;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Given an instance, it will populate all fields with values.
 *
 * @author datalorax - 25/02/2015.
 */
public final class EntityPopulator {
    private final PopulatorConfig config;

    public static Builder newBuilder() {
        return new Builder();
    }

    private EntityPopulator(final PopulatorConfig config) {
        this.config = config;
    }

    public <T> T populate(final T instance) {
        //noinspection unchecked
        final Class<T> type = (Class<T>) instance.getClass();
        return _populate(type, instance);
    }

    public <T> T populate(final Class<T> type) {
        return _populate(type, null);
    }

    private <T> T _populate(final Class<T> type, final T instance) {
        final Mutator mutator = config.getMutatorConfig().getMutator(type);
        //noinspection unchecked
        return (T) mutator.mutate(type, instance, config);
    }

    public static class Builder {
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
        private final Set<String> fieldExclusions = new HashSet<String>();

        public Builder withFieldExclusions(Set<String> exclusions) {
            fieldExclusions.addAll(exclusions);
            return this;
        }

        public Builder withSpecificMutator(Type type, Mutator mutator) {
            Validate.notNull(type, "type null");
            Validate.notNull(mutator, "mutator null");
            specificMutators.put(type, mutator);
            return this;
        }

        public Builder withBaseMutator(Class<?> baseClass, Mutator mutator) {
            Validate.notNull(baseClass, "baseClass null");
            Validate.notNull(mutator, "mutator null");
            baseMutators.put(baseClass, mutator);
            return this;
        }

        public Builder withDefaultArrayMutator(Mutator mutator) {
            Validate.notNull(mutator, "mutator null");
            defaultArrayMutator = mutator;
            return this;
        }

        public Builder withDefaultMutator(Mutator mutator) {
            Validate.notNull(mutator, "mutator null");
            defaultMutator = mutator;
            return this;
        }

        MutatorConfig buildMutatorConfig() {
            return new MutatorConfig(baseMutators, specificMutators, defaultMutator, defaultArrayMutator);
        }

        PopulatorConfig buildPopulatorConfig() {
            return new PopulatorConfig(fieldExclusions, buildMutatorConfig());
        }

        public EntityPopulator build() {
            return new EntityPopulator(buildPopulatorConfig());
        }
    }
}