package org.datalorax.populace.typed;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Immutable collection of handlers for specific and more generic types. Values can be registered against a
 * {@link java.lang.reflect.Type} key as one of:
 * <ul>
 * <li>
 * Specific - meaning they match the exact registered {@link java.lang.reflect.Type type}. This includes specific array types and
 * parameterised types. Use {@link TypedCollection.Builder#withSpecificType(java.lang.reflect.Type, V)} to register.
 * </li>
 * <li>
 * Super - meaning they match any sub-type of the registered raw {@link java.lang.Class class}
 * Use {@link TypedCollection.Builder#withSuperType(Class, V)} to register.
 * </li>
 * <li>
 * Default - meaning they will be returned if no other key matches the requested {@link java.lang.reflect.Type type}
 * and the requested {@link java.lang.reflect.Type type} is not an array type. Use {@link TypedCollection.Builder#withDefault(V)}
 * to register.
 * </li>
 * <li>
 * Default Array - meaning they will be used if no other key matches the requested {@link java.lang.reflect.Type type}
 * and the requested type is an array type.  * Use {@link TypedCollection.Builder#withDefaultArray(V)} to register.
 * </li>
 * </ul>
 * <p/>
 * Values can be retrieved via {@link org.datalorax.populace.typed.TypedCollection#get(java.lang.reflect.Type)}}
 *
 * @author datalorax - 28/02/2015.
 */
public class TypedCollection<V> {
    private final Map<Type, V> specificValues;
    private final Map<Class<?>, V> superValues;
    private final V defaultArrayValue;
    private final V defaultValue;

    public static <T> Builder<T> newBuilder() {
        return new TypedCollectionBuilder<T>();
    }

    public interface Builder<T> {
        Builder<T> withSpecificTypes(final Map<Type, ? extends T> handlers);

        Builder<T> withSpecificType(final Type type, final T handler);

        Builder<T> withSuperTypes(final Map<Class<?>, ? extends T> handlers);

        Builder<T> withSuperType(final Class<?> baseClass, final T handler);

        Builder<T> withDefaultArray(final T handler);

        Builder<T> withDefault(final T handler);

        TypedCollection<T> build();
    }

    public V get(final Type type) {
        V mutator = getSpecificValue(type);
        if (mutator != null) {
            return mutator;
        }

        if (TypeUtils.isArrayType(type)) {
            return defaultArrayValue;
        }

        mutator = getBaseValue(type);
        return mutator == null ? defaultValue : mutator;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final TypedCollection config = (TypedCollection) o;
        return defaultArrayValue.equals(config.defaultArrayValue) &&
                defaultValue.equals(config.defaultValue) &&
                specificValues.equals(config.specificValues) &&
                superValues.equals(config.superValues);
    }

    @Override
    public int hashCode() {
        int result = specificValues.hashCode();
        result = 31 * result + superValues.hashCode();
        result = 31 * result + defaultArrayValue.hashCode();
        result = 31 * result + defaultValue.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TypedCollection{" +
                "specificValues=" + specificValues +
                ", superValues=" + superValues +
                ", defaultArrayValue=" + defaultArrayValue +
                ", defaultValue=" + defaultValue +
                '}';
    }

    TypedCollection(final Map<Type, V> specificValues, final Map<Class<?>, V> superValues,
                    final V defaultValue, final V defaultArrayValue) {
        Validate.notNull(superValues, "superValues null");
        Validate.notNull(specificValues, "specificValues null");
        this.superValues = Collections.unmodifiableMap(new HashMap<Class<?>, V>(superValues));
        this.specificValues = Collections.unmodifiableMap(new HashMap<Type, V>(specificValues));
        this.defaultValue = defaultValue;
        this.defaultArrayValue = defaultArrayValue;
    }

    private V getSpecificValue(final Type type) {
        return specificValues.get(type);
    }

    private V getBaseValue(final Type type) {
        Map.Entry<Class<?>, V> bestMatch = null;

        for (Map.Entry<Class<?>, V> entry : superValues.entrySet()) {
            if (!TypeUtils.isAssignable(type, entry.getKey())) {
                continue;
            }

            if (bestMatch == null || TypeUtils.isAssignable(entry.getKey(), bestMatch.getKey())) {
                // First, or more specific match found:
                bestMatch = entry;
            }
        }

        return bestMatch == null ? null : bestMatch.getValue();
    }
}