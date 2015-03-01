package org.datalorax.populace.populator.mutator;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

/**
 * Mutator for {@link java.util.List Lists}.
 * <p/>
 * Null {@link java.util.List} fields are populated using an instance of the <code>defaultType</code> parameter passed to the {@link ListMutator#ListMutator(Class) constructor}
 * <p/>
 * None null {@link java.util.List} fields are modified by mutating each entry in the list. If the list is empty then a single instance of the correct type is added.
 *
 * @author datalorax - 27/02/2015.
 */
public class ListMutator implements Mutator {
    private static final TypeVariable<Class<List>> LIST_TYPE_VARIABLE = List.class.getTypeParameters()[0];

    private final Constructor<? extends List> defaultConstructor;

    public ListMutator(Class<? extends List> defaultListType) {
        try {
            this.defaultConstructor = defaultListType.getDeclaredConstructor();
            this.defaultConstructor.setAccessible(true);    // Todo(ac): have setAccessible calls optional...
            this.defaultConstructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("No default constructor existed for default list type: " + defaultListType);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Failed to instantiate default list type: " + defaultListType);
        }
    }

    @Override
    public List<?> mutate(Type type, Object currentValue, PopulatorConfig config) {
        if (!TypeUtils.isAssignable(type, List.class)) {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }

        final List list = currentValue == null ? createNewList(type) : (List) currentValue;
        _mutate(type, list, config);
        return list;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ListMutator that = (ListMutator) o;
        return defaultConstructor.equals(that.defaultConstructor);
    }

    @Override
    public int hashCode() {
        return defaultConstructor.hashCode();
    }

    @Override
    public String toString() {
        return "ListMutator{" +
                "defaultType=" + defaultConstructor.getDeclaringClass() +
                '}';
    }

    private List<?> _mutate(Type type, List list, PopulatorConfig config) {
        final Type componentType = getComponentType(type);
        final Mutator componentMutator = config.getMutatorConfig().getMutator(componentType);

        if (list.isEmpty()) {
            //noinspection unchecked
            list.add(null); // Mutate will populate
        }

        final int size = list.size();
        for (int i = 0; i != size; ++i) {
            final Object original = list.get(i);
            final Object mutated = componentMutator.mutate(componentType, original, config);
            //noinspection unchecked
            list.set(i, mutated);
        }
        return list;
    }

    private Type getComponentType(Type type) {
        final Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(type, List.class);
        return typeArguments.get(LIST_TYPE_VARIABLE);
    }

    // Todo(ac): Capture context as the exception ripples up
    // Todo(ac): move all of this into type to be shared...
    private List createNewList(Type type) {
        //noinspection unchecked
        final Class<? extends List> rawType = (Class<? extends List>) TypeUtils.getRawType(type, List.class);

        if (rawType.isInterface() || Modifier.isAbstract(rawType.getModifiers())) {
            if (!rawType.isAssignableFrom(defaultConstructor.getDeclaringClass())) {
                final String classification = rawType.isInterface() ? "an interface" : "an abstract class";
                throw new RuntimeException("Could not instantiate " + rawType.getCanonicalName() + " as it is " +
                        classification + " and not compatible with the default list type: " +
                        defaultConstructor.getDeclaringClass().getCanonicalName() +
                        ". Consider installing a custom mutator for this type or some of its super types");
            }
            return createDefaultListType();
        }

        return createSpecificListType(rawType);
    }

    private List createSpecificListType(final Class<? extends List> rawType) {
        try {
            //noinspection unchecked
            Constructor<? extends List> defaultConstructor = rawType.getConstructor();
            defaultConstructor.setAccessible(true);
            return defaultConstructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No default constructor existed for list type: " + rawType, e);  // Todo(ac): Add specific exception types.
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to instantiate empty list", e);  // Todo(ac): Add specific exception types.
        }// Todo(ac): Add helpful instructions to exceptions e.g. add custom mutator to handle type X
    }

    private List createDefaultListType() {
        try {
            return defaultConstructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to instantiate default empty list", e);  // Todo(ac): Add specific exception types.
        }
    }
}
