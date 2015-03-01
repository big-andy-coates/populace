package org.datalorax.populace.graph.inspector;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * The walker walks each value in the collection, depth first. Keys are not walked.
 *
 * @author datalorax - 01/03/2015.
 */
public class MapValueInspector implements Inspector {
    public static final Inspector INSTANCE = new MapValueInspector();

    @Override
    public Iterable<Field> getFields(final Object instance) {
        return ImmutableSet.of();
    }

    @Override
    public boolean typeIsCollection() {
        return true;
    }

    @Override
    public Iterable<?> getChildren(final Object instance) {
        Validate.isInstanceOf(Map.class, instance);
        //noinspection unchecked
        return ((Map<?, ?>) instance).values();
    }
}
