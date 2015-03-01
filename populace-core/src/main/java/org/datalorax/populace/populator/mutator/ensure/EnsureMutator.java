package org.datalorax.populace.populator.mutator.ensure;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * A mutator that ensures the current value is not null
 * @author datalorax - 01/03/2015.
 */
public class EnsureMutator implements Mutator {
    public static final Mutator INSTANCE = new EnsureMutator();

    @Override
    public Object mutate(final Type type, final Object currentValue, final PopulatorContext config) {
        if (currentValue != null) {
            return currentValue;
        }
        return createNewInstance(type);
    }

    private Object createNewInstance(final Type type) {
        try {
            // Todo(ac): allow the way entities are created to be customised (needed to support interfaces):
            final Class<?> rawType = TypeUtils.getRawType(type, null);
            final Constructor<?> noArgsConstructor = rawType.getDeclaredConstructor();
            noArgsConstructor.setAccessible(true);
            return noArgsConstructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);  // todo(ac): Be more specific with the exception type
        }
    }
}
