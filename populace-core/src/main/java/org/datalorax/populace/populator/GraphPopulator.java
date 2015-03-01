package org.datalorax.populace.populator;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.field.visitor.FieldVisitor;
import org.datalorax.populace.field.visitor.FieldVisitorUtils;
import org.datalorax.populace.field.visitor.SetAccessibleFieldVisitor;
import org.datalorax.populace.graph.GraphWalker;
import org.datalorax.populace.typed.TypedCollection;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Given an instance, it will populate all fields, recursively, with values.
 *
 * @author datalorax - 25/02/2015.
 */
public final class GraphPopulator {
    private final GraphWalker walker;
    private final PopulatorContext config;

    public static Builder newBuilder() {
        return new GraphPopulatorBuilder();
    }

    public interface Builder {
        Builder withFieldFilter(final FieldFilter filter);

        Builder withMutators(final TypedCollection<Mutator> mutators);

        GraphPopulator build();
    }

    // Todo(ac): needs a TypeReference<T> parameter...
    public <T> T populate(final T instance) {
        walker.walk(instance, FieldVisitorUtils.chain(SetAccessibleFieldVisitor.INSTANCE, new Visitor()));
        return instance;
    }

    public <T> T populate(final Class<T> type) {
        final Mutator mutator = config.getMutator(type); //  Todo(ac): this should not be mutator, but a factory of some sort.
        //noinspection unchecked
        final T instance = (T) mutator.mutate(type, null, config);
        return populate(instance);
    }

    public PopulatorContext getConfig() {
        return config;
    }

    GraphPopulator(final GraphWalker walker, final PopulatorContext config) {
        Validate.notNull(walker, "walker null");
        Validate.notNull(config, "config null");
        this.config = config;
        this.walker = walker;
    }

    private class Visitor implements FieldVisitor {
        @Override
        public void visit(final Field field, final Object instance) {
            try {
                final Type type = field.getGenericType();
                final Object currentValue = field.get(instance);
                final Mutator mutator = config.getMutator(type);
                final Object mutated = mutator.mutate(type, currentValue, config);
                if (mutated != currentValue) {
                    field.set(instance, mutated);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);  // todo(ac): throw specific
            }
        }
    }
}