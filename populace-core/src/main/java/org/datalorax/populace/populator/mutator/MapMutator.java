package org.datalorax.populace.populator.mutator;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.Set;

/**
 * Mutator for maps
 *
 * @author datalorax - 27/02/2015.
 */
public class MapMutator implements Mutator {
    private static final TypeVariable<Class<Map>>[] MAP_TYPE_VARIABLES = Map.class.getTypeParameters();

    private final Constructor<? extends Map> defaultMapConstructor;

    public MapMutator(Class<? extends Map> defaultMapType) {
        try {
            this.defaultMapConstructor = defaultMapType.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No default constructor existed for type: " + defaultMapType);
        }
    }

    @Override
    public Map<?, ?> mutate(Type type, Object currentValue, PopulatorConfig config) {
        if (!TypeUtils.isAssignable(type, Map.class)) {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }

        final Map map = currentValue == null ? createNewMap(type) : (Map) currentValue;
        _mutate(type, map, config);
        return map;
    }

    public Map<?, ?> _mutate(Type type, Map map, PopulatorConfig config) {
        final Type valueType = getValueType(type);
        final Mutator valueMutator = config.getMutatorConfig().getMutator(valueType);

        if (map.isEmpty()) {
            //noinspection unchecked
            map.put(createNewKey(type, config), null); // Mutate will populate value.
        }

        //noinspection unchecked
        for (Map.Entry entry : (Set<Map.Entry>) map.entrySet()) {
            final Object original = entry.getValue();
            final Object mutated = valueMutator.mutate(valueType, original, config);
            //noinspection unchecked
            entry.setValue(mutated);
        }
        return map;
    }

    private Type getKeyType(Type type) {
        final Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(type, Map.class);
        return typeArguments.get(MAP_TYPE_VARIABLES[0]);
    }

    private Type getValueType(Type type) {
        final Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(type, Map.class);
        return typeArguments.get(MAP_TYPE_VARIABLES[1]);
    }

    private Object createNewKey(Type type, PopulatorConfig config) {
        final Type keyType = getKeyType(type);
        final Mutator keyMutator = config.getMutatorConfig().getMutator(keyType);
        return keyMutator.mutate(keyType, null, config);
    }

    private Map createNewMap(Type type) {
        // Todo(ac: support type being specific impl - list has example - extract and reuse.
        try {
            return defaultMapConstructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to instantiate new empty map", e);
        }
    }
}
