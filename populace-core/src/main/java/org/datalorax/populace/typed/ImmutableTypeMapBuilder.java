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

package org.datalorax.populace.typed;

import org.apache.commons.lang3.Validate;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * The builder of typed collection
 * @author Andrew Coates - 28/02/2015.
 */
public class ImmutableTypeMapBuilder<T> implements ImmutableTypeMap.Builder<T> {
    private T defaultValue = null;
    private T arrayDefaultValue = null;
    private final Map<Type, T> specificValues = new HashMap<Type, T>();
    private final Map<Class<?>, T> superValues = new HashMap<Class<?>, T>();
    private final Map<Package, T> packageValues = new HashMap<Package, T>();

    @Override
    public ImmutableTypeMapBuilder<T> withSpecificTypes(final Map<Type, ? extends T> handlers) {
        Validate.notNull(handlers, "handlers null");
        for (Map.Entry<Type, ? extends T> entry : handlers.entrySet()) {
            withSpecificType(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public ImmutableTypeMapBuilder<T> withSpecificType(final Type type, final T handler) {
        Validate.notNull(type, "type null");
        Validate.notNull(handler, "handler null");
        specificValues.put(type, handler);
        return this;
    }

    @Override
    public ImmutableTypeMap.Builder<T> withSuperTypes(final Map<Class<?>, ? extends T> handlers) {
        Validate.notNull(handlers, "handlers null");
        for (Map.Entry<Class<?>, ? extends T> entry : handlers.entrySet()) {
            withSuperType(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public ImmutableTypeMapBuilder<T> withSuperType(final Class<?> baseClass, final T handler) {
        Validate.notNull(baseClass, "baseClass null");
        Validate.notNull(handler, "handler null");
        superValues.put(baseClass, handler);
        return this;
    }

    @Override
    public ImmutableTypeMap.Builder<T> withPackageType(final Package thePackage, final T handler) {
        Validate.notNull(thePackage, "package null");
        Validate.notNull(handler, "handler null");
        packageValues.put(thePackage, handler);
        return this;
    }

    @Override
    public ImmutableTypeMapBuilder<T> withArrayDefault(final T handler) {
        Validate.notNull(handler, "handler null");
        arrayDefaultValue = handler;
        return this;
    }

    @Override
    public ImmutableTypeMapBuilder<T> withDefault(final T handler) {
        Validate.notNull(handler, "handler null");
        defaultValue = handler;
        return this;
    }

    @Override
    public ImmutableTypeMap<T> build() {
        return new ImmutableTypeMap<T>(specificValues, superValues, packageValues, arrayDefaultValue, defaultValue);
    }

    /**
     * Construct via {@link org.datalorax.populace.typed.ImmutableTypeMap#newBuilder(Object)} or
     * {@link org.datalorax.populace.typed.ImmutableTypeMap#asBuilder(ImmutableTypeMap)}
     */
    ImmutableTypeMapBuilder(final T defaultValue) {
        this.defaultValue = defaultValue;
    }

    ImmutableTypeMapBuilder(final Map<Type, T> specificValues, final Map<Class<?>, T> superValues,
                            final Map<Package, T> packageValues, final T arrayDefaultValue, final T defaultValue) {
        this.specificValues.putAll(specificValues);
        this.superValues.putAll(superValues);
        this.packageValues.putAll(packageValues);
        this.arrayDefaultValue = arrayDefaultValue;
        this.defaultValue = defaultValue;
    }
}