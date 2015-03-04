package org.datalorax.populace.populator.mutator.change;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

/**
 * Change Mutator for {@link java.util.List Lists}.
 *
 * If the currentValue is null or empty then this mutator does nothing. Consider using
 * {@link org.datalorax.populace.populator.mutator.ensure.EnsureMutator} to first ensure the current value is not null,
 * and/or {@link org.datalorax.populace.populator.mutator.ensure.EnsureMapNotEmptyMutator} to ensure the list is not empty,
 * if the required behaviour is to always ensure a non-null, populated list instance.
 * <p>
 * None null values are modified by mutating each entry in the list.
 *
 * @author datalorax - 27/02/2015.
 */
public class ChangeListElementsMutator implements Mutator {
    private static final TypeVariable<Class<List>> LIST_TYPE_VARIABLE = List.class.getTypeParameters()[0];

    public static final ChangeListElementsMutator INSTANCE = new ChangeListElementsMutator();

    @Override
    public List<?> mutate(Type type, Object currentValue, final Object parent, PopulatorContext config) {
        Validate.isAssignableFrom(List.class, TypeUtils.getRawType(type, null), "Unsupported type %s", type);

        if (currentValue == null) {
            return null;
        }

        //noinspection unchecked
        final List<Object> list = (List)currentValue;
        final Type defaultComponentType = getComponentType(type);
        final Mutator defaultMutator = config.getMutator(defaultComponentType);

        final int size = list.size();
        for (int i = 0; i != size; ++i) {
            final Object original = list.get(i);
            final Type valueType = original == null ? defaultComponentType : original.getClass();
            final Mutator mutator = original == null ? defaultMutator : config.getMutator(original.getClass());
            final Object mutated = mutator.mutate(valueType, original, null, config);
            list.set(i, mutated);
        }
        return list;
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

    private Type getComponentType(Type type) {
        final Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(type, List.class);
        final Type componentType = typeArguments.get(LIST_TYPE_VARIABLE);
        return componentType == null ? Object.class : componentType;
    }
}
