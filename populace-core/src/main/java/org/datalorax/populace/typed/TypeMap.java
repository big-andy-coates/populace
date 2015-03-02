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
 * parameterised types. Use {@link TypeMap.Builder#withSpecificType} to register.
 * </li>
 * <li>
 * Super - meaning they match any sub-type of the registered raw {@link java.lang.Class class}
 * Use {@link TypeMap.Builder#withSuperType} to register.
 * </li>
 * <li>
 * Default - meaning they will be returned if no other key matches the requested {@link java.lang.reflect.Type type}
 * and the requested {@link java.lang.reflect.Type type} is not an array type. Use {@link TypeMap.Builder#withDefault}
 * to register.
 * </li>
 * <li>
 * Default Array - meaning they will be used if no other key matches the requested {@link java.lang.reflect.Type type}
 * and the requested type is an array type.  * Use {@link TypeMap.Builder#withDefaultArray} to register.
 * </li>
 * </ul>
 * <p>
 * Values can be retrieved via {@link TypeMap#get(java.lang.reflect.Type)}}
 *
 * @author datalorax - 28/02/2015.
 */
public class TypeMap<V> {
    private final Map<Type, V> specificValues;
    private final Map<Class<?>, V> superValues;
    private final V defaultArrayValue;
    private final V defaultValue;

    public static <T> Builder<T> newBuilder() {
        return new TypedMapBuilder<T>();
    }

    public interface Builder<T> {
        Builder<T> withSpecificTypes(final Map<Type, ? extends T> handlers);

        Builder<T> withSpecificType(final Type type, final T handler);

        Builder<T> withSuperTypes(final Map<Class<?>, ? extends T> handlers);

        Builder<T> withSuperType(final Class<?> baseClass, final T handler);

        Builder<T> withDefaultArray(final T handler);

        Builder<T> withDefault(final T handler);

        TypeMap<T> build();
    }

    /**
     * Returns the most specific value for the provided key. Matches are found in the following priority:
     * specific, super, default, where default is either array or non-array depending on if the key is an array type.
     * @param key to look up
     * @return the most specific value found, or null if no value found.
     */
    public V get(final Type key) {
        V value = getSpecific(key);
        if (value != null) {
            return value;
        }

        if (TypeUtils.isArrayType(key)) {
            return getArrayDefault();
        }

        value = getSuper(TypeUtils.getRawType(key, null));
        if (value != null) {
            return value;
        }

        return getDefault();
    }

    /**
     * Return the value matching this specific key. Only a type registered with this specific key will be found.
     * @param key the key to lookup
     * @return the value matching this specific key, if found, else null.
     */
    public V getSpecific(final Type key) {
        Validate.notNull(key, "key null");
        return specificValues.get(key);
    }

    /**
     * Return the best match for this super key. The value for the most specific super key will be returned.
     * @param key the key to lookup
     * @return the most value for the most specific super key, if found, else null.
     */
    public V getSuper(final Class<?> key) {
        Validate.notNull(key, "key null");
        Map.Entry<Class<?>, V> bestMatch = null;

        for (Map.Entry<Class<?>, V> entry : superValues.entrySet()) {
            if (!TypeUtils.isAssignable(key, entry.getKey())) {
                continue;
            }

            if (bestMatch == null || TypeUtils.isAssignable(entry.getKey(), bestMatch.getKey())) {
                // First, or more specific match found:
                bestMatch = entry;
            }
        }

        return bestMatch == null ? null : bestMatch.getValue();
    }

    /**
     * @return the default value for array types if one is present, else null.
     */
    public V getArrayDefault() {
        return defaultArrayValue;
    }

    /**
     * @return the default value for non-array types if one if present, else null.
     */
    public V getDefault() {
        return defaultValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final TypeMap config = (TypeMap) o;
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
        return "TypedMap{" +
                "specificValues=" + specificValues +
                ", superValues=" + superValues +
                ", defaultArrayValue=" + defaultArrayValue +
                ", defaultValue=" + defaultValue +
                '}';
    }

    TypeMap(final Map<Type, V> specificValues, final Map<Class<?>, V> superValues,
            final V defaultValue, final V defaultArrayValue) {
        Validate.notNull(superValues, "superValues null");
        Validate.notNull(specificValues, "specificValues null");
        this.superValues = Collections.unmodifiableMap(new HashMap<Class<?>, V>(superValues));
        this.specificValues = Collections.unmodifiableMap(new HashMap<Type, V>(specificValues));
        this.defaultValue = defaultValue;
        this.defaultArrayValue = defaultArrayValue;
    }
}
