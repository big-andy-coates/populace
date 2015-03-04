package org.datalorax.populace.populator.mutator.ensure;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Map;

/**
 * A mutator that ensures a collection has at least valueIf it is, then the mutator adds a non-null single entry.
 * <p>
 * If the currentValue is null then this mutator does nothing. Consider using
 * {@link EnsureMutator} to first ensure the current value is not null
 * the required behaviour is to always ensure a non-null, populated collection instance.
 *
 * @author datalorax - 01/03/2015.
 */
public class EnsureCollectionNotEmptyMutator implements Mutator {
    private static final TypeVariable<Class<Collection>> COLLECTION_TYPE_VARIABLE = Collection.class.getTypeParameters()[0];
    public static final EnsureCollectionNotEmptyMutator INSTANCE = new EnsureCollectionNotEmptyMutator();

    @Override
    public Collection<?> mutate(final Type type, final Object currentValue, final Object parent, final PopulatorContext config) {
        Validate.isAssignableFrom(Collection.class, TypeUtils.getRawType(type, Collection.class), "Mutator only supports Collection types");
        if (currentValue == null) {
            return null;
        }

        //noinspection unchecked
        final Collection<Object> collection = (Collection) currentValue;
        if (!collection.isEmpty()) {
            return collection;
        }

        final Object value = createEntry(type, parent, config);
        collection.add(value);
        return collection;
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
        final Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(type, Collection.class);
        final Type componentType = typeArguments.get(COLLECTION_TYPE_VARIABLE);
        return componentType == null ? Object.class : componentType;
    }

    private Object createEntry(Type collectionType, final Object parent, PopulatorContext config) {
        final Type componentType = getComponentType(collectionType);
        final Object value = config.createInstance(componentType, parent);

        final Mutator mutator = config.getMutator(componentType);
        return mutator.mutate(componentType, value, parent, config);
    }
}
// Todo(ac): test
