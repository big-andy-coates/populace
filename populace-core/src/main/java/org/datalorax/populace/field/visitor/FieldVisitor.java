package org.datalorax.populace.field.visitor;

import java.lang.reflect.Field;

/**
 * Visitor pattern interface for fields
 *
 * @author datalorax - 28/02/2015.
 */
public interface FieldVisitor {
    /**
     * Called on visiting a simple field
     * @param field the field being visited
     * @param instance the instance of the declaring class of the field. Call {@link java.lang.reflect.Field#get(Object) field.get(instance)}
     */
    void visit(final Field field, final Object instance);
}
