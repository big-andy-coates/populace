package org.datalorax.populace.populator.mutator;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.Set;

/**
 * Mutator for {@link java.util.Set Sets}.
 * <p>
 * Null {@link java.util.Set} fields are populated using an instance of the <code>defaultType</code> parameter passed to
 * the {@link SetMutator#SetMutator(Class) constructor}
 * <p>
 * None null {@link java.util.Set} fields are modified by adding an additional instance to the set of the required type.
 * This is done as mutating entries of a Set can invalidate the set and lead to
 * undefined behaviour.
 *
 * @author datalorax - 27/02/2015.
 */
public class SetMutator implements Mutator {
    private static final TypeVariable<Class<Set>> SET_TYPE_VARIABLE = Set.class.getTypeParameters()[0];

    private final Constructor<? extends Set> defaultConstructor;

    public SetMutator(Class<? extends Set> defaultType) {
        try {
            this.defaultConstructor = defaultType.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No default constructor existed for type: " + defaultType);
        }
    }

    @Override
    public Set<?> mutate(Type type, Object currentValue, PopulatorContext config) {
        if (!TypeUtils.isAssignable(type, Set.class)) {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }

        final Set set = currentValue == null ? createNewSet(type) : (Set) currentValue;
        _mutate(type, set, config);
        return set;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SetMutator that = (SetMutator) o;
        return defaultConstructor.equals(that.defaultConstructor);
    }

    @Override
    public int hashCode() {
        return defaultConstructor.hashCode();
    }

    @Override
    public String toString() {
        return "SetMutator{" +
                "defaultType=" + defaultConstructor.getDeclaringClass() +
                '}';
    }

    private Set<?> _mutate(Type type, Set set, PopulatorContext config) {
        final Type componentType = getComponentType(type);
        final Mutator componentMutator = config.getMutator(componentType);

        Object newItem = null;
        boolean added;
        do {
            newItem = componentMutator.mutate(componentType, newItem, config);
            //noinspection unchecked
            added = set.add(newItem);
        } while (!added);

        return set;
    }

    private Type getComponentType(Type type) {
        final Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(type, Set.class);
        return typeArguments.get(SET_TYPE_VARIABLE);
    }

    private Set createNewSet(Type type) {
        // Todo(ac: support type being specific impl of set.
        try {
            return defaultConstructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to instantiate new empty set", e);
        }
    }
}
