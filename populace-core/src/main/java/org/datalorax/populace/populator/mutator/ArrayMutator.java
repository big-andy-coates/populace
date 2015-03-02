package org.datalorax.populace.populator.mutator;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

/**
 * The default mutator used to mutate arrays
 *
 * @author datalorax - 27/02/2015.
 */
public class ArrayMutator implements Mutator {
    public static final Mutator INSTANCE = new ArrayMutator();

    @Override
    public Object mutate(Type type, Object currentValue, final Object parent, PopulatorContext config) {
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

    private Object _mutate(Type componentType, Object currentValue, PopulatorContext config) {
        if (!(componentType instanceof Class)) {
            throw new UnsupportedOperationException(); // Todo(ac):
        }

        final boolean isArray = TypeUtils.isArrayType(componentType);
        final Mutator componentMutator = isArray ? null : config.getMutator(componentType);
        final Type arrayComponentType = isArray ? ((Class<?>) componentType).getComponentType() : null;

        final Object array = currentValue != null ? currentValue : Array.newInstance((Class<?>) componentType, 1);
        final long length = Array.getLength(array);
        for (int i = 0; i != length; ++i) {
            final Object object = Array.get(array, i);
            // Todo(ac): current mutating down the object graph. unfortunately, leaf fields need this code, but its expensive for objects.
            // Could solve by passing through a 'hasChildren' flag in the visitor interface of walker... then only mutate if no children - though... what about empty collections.
            // Todo(ac): Mutator needs to indicate if this is leaf or not.
            // Todo(ac): or maybe, walker could pass through flag indicating if the field type 'isCollection'...
            final Object mutated = isArray ?
                    _mutate(arrayComponentType, object, config) :
                    componentMutator.mutate(componentType, object, null, config);
            Array.set(array, i, mutated);
        }
        return array;
    }
}
