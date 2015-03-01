package org.datalorax.populace.populator.graph;

import org.datalorax.populace.populator.field.filter.FieldFilter;
import org.datalorax.populace.populator.field.visitor.FieldVisitor;
import org.datalorax.populace.populator.graph.walkers.Walker;
import org.datalorax.populace.populator.typed.TypedCollection;

/**
 * Type that walks an object graph
 *
 * @author datalorax - 28/02/2015.
 */
public class GraphWalker {
    private final WalkerConfig config;

    public static Builder newBuilder() {
        return new GraphWalkerBuilder();
    }

    public interface Builder {
        Builder withFieldFilter(final FieldFilter filter);

        Builder withCustomWalkers(final TypedCollection<Walker> walkers);

        GraphWalker build();
    }

    public void walk(final Object instance, final FieldVisitor visitor) {
        final Walker walker = config.getWalker(instance.getClass());
        walker.walk(instance.getClass(), instance, visitor, config);
    }

    GraphWalker(final WalkerConfig config) {
        this.config = config;
    }
}
