package org.datalorax.populace.populator.mutator;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorConfig;

import java.lang.reflect.*;

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

    private boolean isExcluded(Field field, final PopulatorConfig config) {
        // Todo(ac): consider moving this method impl into config - maybe rename to context?
        return Modifier.isStatic(field.getModifiers()) ||
                Modifier.isTransient(field.getModifiers()) ||
                config.isExcludedField(field.getName());
    }

    private Object populateInstance(Type type, Object currentValue, final PopulatorConfig config) {
        final Mutator mutator = config.getMutatorConfig().getMutator(type);
        return mutator.mutate(type, currentValue, config);
    }
}
