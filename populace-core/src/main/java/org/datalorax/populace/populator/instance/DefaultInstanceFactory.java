package org.datalorax.populace.populator.instance;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.populator.PopulatorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * Instance factory that uses a classes default constructor to create a new instance of the type.
 * @author datalorax - 02/03/2015.
 */
public class DefaultInstanceFactory implements InstanceFactory {
    public static final InstanceFactory INSTANCE = new DefaultInstanceFactory();

    @Override
    public <T> T createInstance(Class<? extends T> rawType, final Object parent) {
        try {
            if (isInnerClass(rawType)) {
                return createNewInnerClass(rawType, parent);
            } else {
                return createNewTopLevel(rawType);
            }
        } catch (ReflectiveOperationException e) {
            throw new PopulatorException("Failed to create new instance of type via default constructor. Type: " + rawType, e);
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

    private static boolean isInnerClass(final Class<?> rawType) {
        return rawType.getEnclosingClass() != null && !Modifier.isStatic(rawType.getModifiers());
    }

    private <T> T createNewTopLevel(final Class<? extends T> rawType) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        final Constructor<?> defaultConstructor = getDefaultConstructor(rawType);

        //noinspection unchecked
        return (T) defaultConstructor.newInstance();
    }

    private <T> T createNewInnerClass(final Class<? extends T> rawType, final Object parent) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Validate.notNull(parent, "Parent of inner class was null");
        final Constructor<?> defaultConstructor = getDefaultConstructor(rawType, parent.getClass());

        //noinspection unchecked
        return (T) defaultConstructor.newInstance(parent);
    }

    private <T> Constructor<? extends T> getDefaultConstructor(final Class<? extends T> rawType, Class<?>... parameterTypes) {
        try {
            final Constructor<? extends T> defaultConstructor = rawType.getDeclaredConstructor(parameterTypes);
            defaultConstructor.setAccessible(true);
            return defaultConstructor;
        } catch (NoSuchMethodException e) {
            throw new PopulatorException("No default constructor could be found for type: " + rawType +
                ", consider adding a custom InstanceFactory to handle this type", e);
        }
    }
}
