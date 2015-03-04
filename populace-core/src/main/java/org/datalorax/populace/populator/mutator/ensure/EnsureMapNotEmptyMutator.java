package org.datalorax.populace.populator.mutator.ensure;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

/**
 * A mutator that ensures a map has at least one key/value pair. If it is, then the mutator adds a single entry with
 * a non-null key and value.
 * <p>
 * If the currentValue is null then this mutator does nothing. Consider using
 * {@link org.datalorax.populace.populator.mutator.ensure.EnsureMutator} to first ensure the current value is not null
 * the required behaviour is to always ensure a non-null, populated map instance.
 *
 * @author datalorax - 01/03/2015.
 */
public class EnsureMapNotEmptyMutator implements Mutator {
    private static final TypeVariable<Class<Map>>[] MAP_TYPE_VARIABLES = Map.class.getTypeParameters();
    public static final Mutator INSTANCE = new EnsureMapNotEmptyMutator();

    @Override
    public Map<?, ?> mutate(final Type type, final Object currentValue, final Object parent, final PopulatorContext config) {
        Validate.isAssignableFrom(Map.class, TypeUtils.getRawType(type, Map.class), "Mutator only supports map types");
        if (currentValue == null) {
            return null;
        }

        //noinspection unchecked
        final Map<Object, Object> map = (Map) currentValue;
        if (!map.isEmpty()) {
            return map;
        }

        final Object key = createNewKey(type, parent, config);
        final Object value = createNewValue(type, parent, config);
        map.put(key, value);
        return map;
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

    private Type getKeyType(Type type) {
        final Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(type, Map.class);
        final Type componentType = typeArguments.get(MAP_TYPE_VARIABLES[0]);
        return componentType == null ? Object.class : componentType;        // Todo(ac): Copying this pattern alot - move out!
    }

    private Type getValueType(Type type) {
        final Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(type, Map.class);
        final Type keyType = typeArguments.get(MAP_TYPE_VARIABLES[1]);
        return keyType == null ? Object.class : keyType;
    }

    private Object createNewKey(Type mapType, final Object parent, PopulatorContext config) {
        final Type keyType = getKeyType(mapType);
        final Object key = config.createInstance(keyType, parent);

        final Mutator keyMutator = config.getMutator(keyType);
        return keyMutator.mutate(keyType, key, parent, config);
    }

    private Object createNewValue(Type mapType, final Object parent, PopulatorContext config) {
        final Type valueType = getValueType(mapType);
        final Object value = config.createInstance(valueType, parent);

        final Mutator mutator = config.getMutator(valueType);
        return mutator.mutate(valueType, value, parent, config);
    }

    // Todo(ac): test
}

