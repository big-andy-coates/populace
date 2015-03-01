package org.datalorax.populace.field.filter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * A field filter to excludes any static fields
 * @author datalorax - 28/02/2015.
 */
public class ExcludeStaticFieldsFilter implements FieldFilter {
    public static final FieldFilter INSTANCE = new ExcludeStaticFieldsFilter();

    @Override
    public boolean evaluate(final Field field) {
        return !Modifier.isStatic(field.getModifiers());
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
