package org.datalorax.populace.populator.mutator.change;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.Set;

/**
 * Change mutator for {@link java.util.Set Sets}.
 *
 * If the currentValue is null or empty then this mutator does nothing. Consider using
 * {@link org.datalorax.populace.populator.mutator.ensure.EnsureMutator} to first ensure the current value is not null,
 * and/or {@link org.datalorax.populace.populator.mutator.ensure.EnsureMapNotEmptyMutator} to ensure the set is not empty,
 * if the required behaviour is to always ensure a non-null, populated set instance.
 *
 * Non null values are modified by adding an additional instance to the set of the required type.
 * This is done as mutating entries of a Set can invalidate the set and lead to undefined behaviour.
 *
 * @author datalorax - 27/02/2015.
 */
public class ChangeSetElementsMutator implements Mutator {
    private static final TypeVariable<Class<Set>> SET_TYPE_VARIABLE = Set.class.getTypeParameters()[0];

    public static final ChangeSetElementsMutator INSTANCE = new ChangeSetElementsMutator();

    @Override
    public Set<?> mutate(Type type, Object currentValue, final Object parent, PopulatorContext config) {
        Validate.isAssignableFrom(Set.class, TypeUtils.getRawType(type, null), "Unsupported type %s", type);

        if (currentValue == null) {
            return null;
        }

        //noinspection unchecked
        final Set<Object> set = (Set)currentValue;
        final Type defaultComponentType = getComponentType(type);
        final Object element = findNonNullElement(set);
        final Type componentType = element == null ? defaultComponentType : element.getClass();
        final Mutator componentMutator = element == null ? config.getMutator(defaultComponentType) : config.getMutator(element.getClass());

        Object newItem = config.createInstance(componentType, null);
        boolean added;
        do {
            newItem = componentMutator.mutate(componentType, newItem, null, config);

            //noinspection unchecked
            added = set.add(newItem);
        } while (!added);

        return set;
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
        final Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(type, Set.class);
        final Type componentType = typeArguments.get(SET_TYPE_VARIABLE);
        return componentType == null ? Object.class : componentType;
    }

    private Object findNonNullElement(final Set<Object> set) {
        for (Object o : set) {
            if (o != null) {
                return o;
            }
        }

        return null;
    }
}
