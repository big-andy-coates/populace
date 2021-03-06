/*
 * Copyright (c) 2015 Andrew Coates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.datalorax.populace.core.util;

import org.apache.commons.lang3.Validate;

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
 * parameterised types. Use {@link ImmutableTypeMap.Builder#withSpecificType} to register.
 * </li>
 * <li>
 * Super - meaning they match any sub-type of the registered raw {@link java.lang.Class class}
 * Use {@link ImmutableTypeMap.Builder#withSuperType} to register.
 * </li>
 * <li>
 * Default - meaning they will be returned if no other key matches the requested {@link java.lang.reflect.Type type}
 * and the requested {@link java.lang.reflect.Type type} is not an array type. Use {@link ImmutableTypeMap.Builder#withDefault}
 * to register.
 * </li>
 * <li>
 * Default Array - meaning they will be used if no other key matches the requested {@link java.lang.reflect.Type type}
 * and the requested type is an array type.  * Use {@link ImmutableTypeMap.Builder#withArrayDefault} to register.
 * </li>
 * </ul>
 * <p>
 * Values can be retrieved via {@link ImmutableTypeMap#get(java.lang.reflect.Type)}}
 *
 * @author Andrew Coates - 28/02/2015.
 */
public class ImmutableTypeMap<V> {
    private final Map<Type, V> specificValues;
    private final Map<Class<?>, V> superValues;
    private final Map<String, V> packageValues;    // Todo(ac): perfect candidate for TriMap
    private final V arrayDefaultValue;
    private final V defaultValue;

    ImmutableTypeMap(final Map<Type, V> specificValues, final Map<Class<?>, V> superValues,
                     final Map<String, V> packageValues, final V arrayDefaultValue, final V defaultValue) {
        Validate.notNull(specificValues, "specificValues null");
        Validate.notNull(superValues, "superValues null");
        Validate.notNull(packageValues, "packageValues null");
        Validate.notNull(defaultValue, "defaultValue null");
        this.specificValues = Collections.unmodifiableMap(new HashMap<>(specificValues));
        this.superValues = Collections.unmodifiableMap(new HashMap<>(superValues));
        this.packageValues = Collections.unmodifiableMap(new HashMap<>(packageValues));
        this.arrayDefaultValue = arrayDefaultValue;
        this.defaultValue = defaultValue;
    }

    public static <T> Builder<T> newBuilder(final T defaultHandler) {
        return new ImmutableTypeMapBuilder<>(defaultHandler);
    }

    public static <T> Builder<T> asBuilder(final ImmutableTypeMap<T> source) {
        return new ImmutableTypeMapBuilder<>(source.specificValues, source.superValues, source.packageValues,
            source.arrayDefaultValue, source.defaultValue);
    }

    /**
     * Returns the most specific value for the provided key. Matches are found in the following priority:
     *
     * For <b>non-array key types</b>: specific, super, package, default
     * For <b>array key types</b>: specific, array-default
     *
     * @param key to look up
     * @return the most specific value found, or null if no value found.
     */
    // Todo(ac): If this can return null change to return optional in v2.x
    public V get(final Type key) {
        V value = getSpecific(key);
        if (value != null) {
            return value;
        }

        if (TypeUtils.isArrayType(key)) {
            return arrayDefaultValue == null ? getDefault() : getArrayDefault();
        }

        final Class<?> rawType = TypeUtils.getRawType(key, null);
        if (rawType != null) {
            value = getSuper(rawType);
            if (value != null) {
                return value;
            }

            value = getPackage(rawType);
            if (value != null) {
                return value;
            }
        }

        return getDefault();
    }

    /**
     * Returns the value matching this specific key. Only a type registered with this exact key will be found.
     *
     * @param key the key to lookup
     * @return the value matching this specific key, if found, else null.
     */
    // Todo(ac): Switch to Optional in v2.x
    public V getSpecific(final Type key) {
        Validate.notNull(key, "key null");
        final Type consistentKey = TypeUtils.ensureConsistentType(key);
        return specificValues.get(consistentKey);
    }

    /**
     * Returns the best match for this super key. The value for the most specific super key will be returned.
     *
     * For example, if {@code key} was {@code ArrayList.class} and values were registered for both {@code List} and
     * {@code Collection}, then this method would return the value associated with the {@code List} entry, as this has
     * a closer relationship
     *
     * @param key the key to lookup
     * @return the most value for the most specific super key, if found, else null.
     */
    // Todo(ac): Switch to Optional in v2.x
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
     * Returns the best match for the {@code packageName} provided.
     * <p>
     * For example, if {@code packageName} was {@code org.someone.p1.p2} and values were were registered for both
     * {@code org.someone} and {@code org.someone.p1}, then this method would return the value associated with the
     * {@code org.someone.p1} entry, as this is a closer match.
     *
     * @param packageName the package name to look up
     * @return the most value of the most specific package key, if found, else null.
     */
    // Todo(ac): Switch to Optional in v2.x
    public V getPackage(final String packageName) {
        Validate.notEmpty(packageName, "packageName empty");

        Map.Entry<String, V> bestMatch = null;

        for (Map.Entry<String, V> entry : packageValues.entrySet()) {
            if (!packageName.startsWith(entry.getKey())) {
                continue;
            }

            if (bestMatch == null || entry.getKey().length() > bestMatch.getKey().length()) {
                // First, or more specific match found:
                bestMatch = entry;
            }
        }

        return bestMatch == null ? null : bestMatch.getValue();
    }

    /**
     * Returns the value that will be used for array types, if no specific type is registered.
     * @return the default value for array types if one is present, else null. If present, this will override the
     * {@link ImmutableTypeMap#getDefault() default value} for any array types.
     */
    // Todo(ac): switch to Optional in v2.x
    public V getArrayDefault() {
        return arrayDefaultValue;
    }

    /**
     * Returns the default value that will be used if no others are registered to handle a specific type.
     *
     * @return the default value.
     */
    public V getDefault() {
        return defaultValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ImmutableTypeMap config = (ImmutableTypeMap) o;
        return arrayDefaultValue.equals(config.arrayDefaultValue) &&
            defaultValue.equals(config.defaultValue) &&
            specificValues.equals(config.specificValues) &&
            superValues.equals(config.superValues) &&
            packageValues.equals(config.packageValues);
    }

    @Override
    public int hashCode() {
        int result = specificValues.hashCode();
        result = 31 * result + superValues.hashCode();
        result = 31 * result + packageValues.hashCode();
        result = 31 * result + arrayDefaultValue.hashCode();
        result = 31 * result + defaultValue.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ImmutableTypeMap{" +
            "specificValues=" + specificValues +
            ", superValues=" + superValues +
            ", packageValues=" + packageValues +
            ", arrayDefaultValue=" + arrayDefaultValue +
            ", defaultValue=" + defaultValue +
            '}';
    }

    private V getPackage(final Class<?> rawType) {
        if (rawType.isPrimitive()) {
            return getPackage("java.lang");
        }
        return getPackage(rawType.getPackage().getName());
    }

    public interface Builder<T> {
        Builder<T> withSpecificType(final Type type, final T handler);

        Builder<T> withSuperType(final Class<?> baseClass, final T handler);

        Builder<T> withPackageType(final String thePackage, final T handler);

        Builder<T> withArrayDefault(final T handler);

        Builder<T> withDefault(final T handler);

        ImmutableTypeMap<T> build();
    }
}
