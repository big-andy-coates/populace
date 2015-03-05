package org.datalorax.populace.field.visitor;

import org.datalorax.populace.field.FieldInfo;

/**
 * A visitor which ensures the field is accessible by calling {@link java.lang.reflect.Field#setAccessible(boolean) setAccessible(true)}
 * @author datalorax - 28/02/2015.
 */
public class SetAccessibleFieldVisitor implements FieldVisitor {
    public static final FieldVisitor INSTANCE = new SetAccessibleFieldVisitor();

    @Override
    public void visit(final FieldInfo fieldInfo) {
        fieldInfo.getField().setAccessible(true);
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
