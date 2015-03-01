package org.datalorax.populace.graph;

import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.field.visitor.FieldVisitor;
import org.datalorax.populace.graph.inspector.Inspector;
import org.datalorax.populace.typed.TypedCollection;

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

        Builder withCustomInspectors(final TypedCollection<Inspector> walkers);

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
            walk(child, visitor);
        }
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

    /**
     * Collection, as in has child elements, not collection as in {@link java.util.Collection}
     * @param field the field to test
     * @param instance the instance of the fields declaring class
     * @return true if the type of the field or the current value of the field have child elements, false otherwise.
     */
    private boolean isCollection(final Field field, final Object instance) {
        final Object value = getValue(field, instance);
        final Class<?> type = value == null ? field.getType() : value.getClass();
        final Inspector inspector = config.getInspector(type);
        return inspector.typeIsCollection();
    }
}

// Todo(ac): Add stack of visited values & fields.