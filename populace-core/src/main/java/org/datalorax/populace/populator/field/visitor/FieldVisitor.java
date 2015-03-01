package org.datalorax.populace.populator.field.visitor;

import java.lang.reflect.Field;

/**
 * Visitor pattern interface for fields
 *
 * @author datalorax - 28/02/2015.
 */
public interface FieldVisitor {
    /**
     * Called on visiting a simple field
     *
     * @param field the field being visited
     * @param value the current value of the field
     */
    void visit(Field field, Object value);

    /**
     * Called on visiting the element of an array
     * @param field the array field
     * @param index the index into the array
     * @param value the value held at that index
     */
    void visitArrayElement(Field field, int index, Object value);
}
