package org.datalorax.populace.populator.mutator;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * The default mutator when all else fails. This mutator implementation attempts to create an instance of the type and
 * then populate it.
 *
 * @author datalorax - 28/02/2015.
 */
public class StdMutator implements Mutator {
    @Override
    public Object mutate(final Type type, final Object currentValue, final PopulatorConfig config) {
        final Class<?> rawType = TypeUtils.getRawType(type, null);
        final Object value = currentValue == null ? createInstance(rawType) : currentValue;

        for (Field field : rawType.getDeclaredFields()) {
            populateField(field, value, config);
        }

        return value;
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

    private Object createInstance(final Class<?> type) {
        try {
            final Constructor<?> noArgConstructor = type.getDeclaredConstructor();
            noArgConstructor.setAccessible(true);
            return noArgConstructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new UnsupportedOperationException("No default constructor found for type. " + type); // Todo(ac):
        }
    }

    private void populateField(final Field field, final Object instance, final PopulatorConfig config) {
        if (isExcluded(field, config)) {
            return;
        }

        try {
            field.setAccessible(true);
            final Object currentValue = field.get(instance);
            final Object newValue = populateInstance(field.getGenericType(), currentValue, config);
            field.set(instance, newValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to populate due to: ", e);
        }
    }

    private static boolean isExcluded(final Field field, final PopulatorConfig config) {
        return config.isExcludedField(field);
    }

    private static Object populateInstance(final Type type, final Object currentValue, final PopulatorConfig config) {
        final Mutator mutator = config.getMutatorConfig().getMutator(type);
        return mutator.mutate(type, currentValue, config);
    }
}
