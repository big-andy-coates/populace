package org.datalorax.populace.populator;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.populator.field.filter.FieldFilter;

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

    public interface Builder {
        Builder withFieldFilter(final FieldFilter filter);

        Builder withMutatorConfig(final MutatorConfig config);

        GraphPopulator build();
    }

    public <T> T populate(final T instance) {
        //noinspection unchecked
        final Class<T> type = (Class<T>) instance.getClass();
        return _populate(type, instance);
    }

    public <T> T populate(final Class<T> type) {
        return _populate(type, null);
    }

    public PopulatorConfig getConfig() {
        return config;
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