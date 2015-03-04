package org.datalorax.populace.graph;

import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.field.visitor.FieldVisitor;
import org.datalorax.populace.graph.inspector.Inspector;
import org.datalorax.populace.typed.ImmutableTypeMap;

import java.lang.reflect.Field;

/**
 * Type that walks an object graph
 *
 * @author datalorax - 28/02/2015.
 */
public class GraphWalker {
    private final WalkerContext config;

    public static Builder newBuilder() {
        return new GraphWalkerBuilder();
    }

    public interface Builder {
        Builder withFieldFilter(final FieldFilter filter);

        FieldFilter getFieldFilter();

        Builder withInspectors(final ImmutableTypeMap<Inspector> walkers);

        ImmutableTypeMap.Builder<Inspector> inspectorsBuilder();

        GraphWalker build();
    }

    public void walk(final Object instance, final FieldVisitor visitor) {
        final Inspector inspector = config.getInspector(instance.getClass());

        for (Field field : inspector.getFields(instance)) {
            if (config.isExcludedField(field)) {
                continue;
            }

            visitor.visit(field, instance);

            final Object value = getValue(field, instance);
            if (value != null) {
                walk(value, visitor);
            }
        }

        for (Object child : inspector.getChildren(instance)) {
            if (child != null) {
                walk(child, visitor);
            }
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final GraphWalker that = (GraphWalker) o;
        return config.equals(that.config);
    }

    @Override
    public int hashCode() {
        return config.hashCode();
    }

    @Override
    public String toString() {
        return "GraphWalker{" +
            "config=" + config +
            '}';
    }

    GraphWalker(final WalkerContext config) {
        this.config = config;
    }

    private static Object getValue(final Field field, final Object instance) {
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new WalkerException("Failed to get field value - consider using SetAccessibleFieldVisitor or similar", e);
        }
    }
}

// Todo(ac): Add stack of visited values & fields.