package org.datalorax.populace.populator;

import org.apache.commons.lang3.Validate;

import java.lang.reflect.Type;
import java.util.Set;

/**
 * Given an instance, it will populate all fields, recursively, with values.
 *
 * @author datalorax - 25/02/2015.
 */
public final class GraphPopulator {
    private final PopulatorConfig config;

    public static Builder newBuilder() {
        return new GraphPopulatorBuilder();
    }

    public <T> T populate(final T instance) {
        //noinspection unchecked
        final Class<T> type = (Class<T>) instance.getClass();
        return _populate(type, instance);
    }

    public <T> T populate(final Class<T> type) {
        return _populate(type, null);
    }

    public interface Builder {
        Builder withFieldExclusions(Set<String> exclusions);    // Todo(ac): switch to FieldFilter interface

        Builder withSpecificMutator(Type type, Mutator mutator);

        Builder withBaseMutator(Class<?> baseClass, Mutator mutator);

        Builder withDefaultArrayMutator(Mutator mutator);

        Builder withDefaultMutator(Mutator mutator);

        GraphPopulator build();
    }

    GraphPopulator(final PopulatorConfig config) {
        Validate.notNull(config, "config null");
        this.config = config;
    }

    private <T> T _populate(final Class<T> type, final T instance) {
        final Mutator mutator = config.getMutatorConfig().getMutator(type);
        //noinspection unchecked
        return (T) mutator.mutate(type, instance, config);
    }
}