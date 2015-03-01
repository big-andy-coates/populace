package org.datalorax.populace.graph.inspector;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An inspector that exposes arrays as having no fields, just a collection of child elements
 *
 * @author datalorax - 27/02/2015.
 */
public class ArrayInspector implements Inspector {
    public static final Inspector INSTANCE = new ArrayInspector();

    @Override
    public Iterable<Field> getFields(final Object instance) {
        return ImmutableSet.of();
    }

    @Override
    public boolean typeIsCollection() {
        return true;
    }

    @Override
    public Iterable<?> getChildren(final Object array) {
        Validate.isTrue(array.getClass().isArray(), "Expected array type, got: " + array.getClass());

        return new Iterable<Object>() {
            @Override
            public Iterator<Object> iterator() {
                final int length = Array.getLength(array);
                return new Iterator<Object>() {
                    int index = 0;

                    @Override
                    public boolean hasNext() {
                        return index != length;
                    }

                    @Override
                    public Object next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return Array.get(array, index++);
                    }
                };
            }
        };
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
