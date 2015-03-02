package org.datalorax.populace.populator.instance;

import org.apache.commons.lang3.Validate;

/**
 * Instance factory for enums. The first value of the enum will be returned. For enums with no values the factory will
 * return null.
 *
 * @author datalorax - 02/03/2015.
 */
public class EnumInstanceFactory implements InstanceFactory {
    public static final InstanceFactory INSTANCE = new EnumInstanceFactory();

    @Override
    public <T> T createInstance(Class<? extends T> rawType, final Object parent) {
        Validate.isTrue(rawType.isEnum(), "Enum type expected");
        //noinspection unchecked
        final T[] allValues = (T[]) rawType.getEnumConstants();
        return allValues.length == 0 ? null : allValues[0];
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
