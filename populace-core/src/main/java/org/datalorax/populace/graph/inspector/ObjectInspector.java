package org.datalorax.populace.graph.inspector;

import com.google.common.collect.ImmutableSet;

import java.lang.reflect.Field;

/**
 * An inspector that exposes objects as having fields, but no child elements
 *
 * @author datalorax - 28/02/2015.
 */
public class ObjectInspector implements Inspector {
    public static final Inspector INSTANCE = new ObjectInspector();

    @Override
    public Iterable<Field> getFields(final Object instance) {
        return ImmutableSet.copyOf(instance.getClass().getDeclaredFields());
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
