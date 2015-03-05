package org.datalorax.populace.field.visitor;

import org.datalorax.populace.field.FieldInfo;

/**
 * Visitor pattern interface for fields
 *
 * @author datalorax - 28/02/2015.
 */
public interface FieldVisitor {
    /**
     * Called on visiting a simple field
     * @param field the field being visited     *
     */
    void visit(final FieldInfo field);
}
