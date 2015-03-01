package org.datalorax.populace.populator.graph.walkers;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.field.visitor.FieldVisitor;
import org.datalorax.populace.populator.graph.WalkerConfig;
import org.datalorax.populace.populator.graph.WalkerException;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * The default walker when all else fails. This walker will iterate around all the fields in the instance, except thosed
 * explicitly excluded by any field filter.
 *
 * @author datalorax - 28/02/2015.
 */
public class StdWalker implements Walker {
    public static final Walker INSTANCE = new StdWalker();

    @Override
    public void walk(final Type type, final Object instance, final FieldVisitor visitor, final WalkerConfig config) {
        final Class<?> rawType = instance.getClass();
        Validate.isTrue(TypeUtils.isArrayType(rawType), "Array type not supported: " + rawType);

        for (Field field : rawType.getDeclaredFields()) {
            if (!isExcluded(field, config)) {
                walkField(field, instance, visitor, config);
            }
        }
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

    private void walkField(final Field field, final Object instance, final FieldVisitor visitor, final WalkerConfig config) {
        visitor.visit(field, instance);

        final Object value = getValue(instance, field);
        if (value == null) {
            return;
        }

        final Walker walker = config.getWalker(field.getGenericType());
        walker.walk(instance.getClass(), value, visitor, config);
    }

    private static boolean isExcluded(final Field field, final WalkerConfig config) {
        return config.isExcludedField(field);
    }

    private static Object getValue(final Object instance, final Field field) {
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new WalkerException("Failed to walk field: " + field, e);
        }
    }
}
