package org.datalorax.populace.graph.inspector;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * An inspector that exposes collections as having no fields, just a collection of child elements
 *
 * @author datalorax - 01/03/2015.
 */
public class CollectionInspector implements Inspector {
    public static final Inspector INSTANCE = new CollectionInspector();

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
        Validate.isInstanceOf(Collection.class, instance);
        //noinspection unchecked
        return (Collection<Object>) instance;
    }
}

// Todo(ac): all these inspectors need tests and hardening
