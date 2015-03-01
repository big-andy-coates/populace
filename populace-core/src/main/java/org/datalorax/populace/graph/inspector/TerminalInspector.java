package org.datalorax.populace.graph.inspector;

import com.google.common.collect.ImmutableSet;

import java.lang.reflect.Field;

/**
 * An inspector that exposes its instance as having no fields and no children i.e. stops the graph traversal from looking
 * inside the instance.
 *
 * @author datalorax - 27/02/2015.
 */
public class TerminalInspector implements Inspector {
    public static final Inspector INSTANCE = new TerminalInspector();

    @Override
    public Iterable<Field> getFields(final Object instance) {
        return ImmutableSet.of();
    }

    @Override
    public boolean equals(final Object that) {
        return this == that || (that != null && getClass() == that.getClass());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}


