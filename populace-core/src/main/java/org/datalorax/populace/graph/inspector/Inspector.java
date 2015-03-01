package org.datalorax.populace.graph.inspector;

import com.google.common.collect.ImmutableSet;

import java.lang.reflect.Field;

/**
 * Interface for 'pluggable' walking of fields. Inspectors for non-collection types should just implement
 * {@link Inspector#getFields(Object)}. Inspectors for collection types e.g. arrays, lists, maps, etc, should
 * override {@link Inspector#typeIsCollection()} to return true and provide implementation for {@link Inspector#getChildren(Object)}.
 *
 * @author datalorax - 28/02/2015.
 */
public interface Inspector {
    /**
     * Return the set of fields this instance supports
     *
     * @param instance the instance
     * @return the set of fields this instance supports
     */
    Iterable<Field> getFields(Object instance);

    /**
     * @return true if the type the inspector supports is a collection type, false otherwise
     */
    default boolean typeIsCollection() {
        return false;
    }

    /**
     * If the type the inspector supports is a collection, the this method will return the collection of its child elements.
     *
     * @param instance the instance.
     * @return the set of child elements, or an empty collection for non-collection types.
     */
    default Iterable<?> getChildren(Object instance) {
        return ImmutableSet.of();
    }
}
