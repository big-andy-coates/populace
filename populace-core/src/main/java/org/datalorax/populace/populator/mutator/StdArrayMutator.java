package org.datalorax.populace.populator.mutator;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorConfig;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

/**
 * The default mutator used to mutate arrays
 *
 * @author datalorax - 27/02/2015.
 */
public class StdArrayMutator implements Mutator {
    @Override
    public Object mutate(Type type, Object currentValue, PopulatorConfig config) {
        Validate.isTrue(TypeUtils.isArrayType(type), "Not array type: " + type);

        return _mutate(TypeUtils.getArrayComponentType(type), currentValue, config);
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

    private Object _mutate(Type componentType, Object currentValue, PopulatorConfig config) {
        if (!(componentType instanceof Class)) {
            throw new UnsupportedOperationException(); // Todo(ac):
        }

        final boolean isArray = TypeUtils.isArrayType(componentType);
        final Mutator componentMutator = isArray ? null : config.getMutatorConfig().getMutator(componentType);
        final Type arrayComponentType = isArray ? ((Class<?>) componentType).getComponentType() : null;

        final Object array = currentValue != null ? currentValue : Array.newInstance((Class<?>) componentType, 1);
        final long length = Array.getLength(array);
        for (int i = 0; i != length; ++i) {
            final Object object = Array.get(array, i);
            final Object mutated = isArray ?
                    _mutate(arrayComponentType, object, config) :
                    componentMutator.mutate(componentType, object, config);
            Array.set(array, i, mutated);
        }
        return array;
    }
}
