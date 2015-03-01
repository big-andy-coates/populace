package org.datalorax.populace.populator.graph.walkers;

import org.datalorax.populace.populator.field.visitor.FieldVisitor;
import org.datalorax.populace.populator.graph.WalkerConfig;

import java.lang.reflect.Type;

/**
 * Interface for 'pluggable' walking of fields
 *
 * @author datalorax - 28/02/2015.
 */
public interface Walker {
    /**
     * @param type     the type of the instance. This is likely to be more precise than what would be returned from
     *                 calling <code>instance.getClass()</code> as it has been obtained from the field being walked
     *                 using reflection.
     * @param instance the instance to walk
     * @param visitor  the visitor to call for each non-excluded field
     * @param config   the walker configuration
     */
    void walk(final Type type, final Object instance, final FieldVisitor visitor, final WalkerConfig config);
}

// Todo(ac): Add stack of visited values & fields.
