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

package org.datalorax.populace.core.populate.mutator.ensure;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.populate.Mutator;
import org.datalorax.populace.core.populate.PopulatorContext;
import org.datalorax.populace.core.util.TypeUtils;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

/**
 * A mutator that ensures a map has at least one key/value pair. If it is, then the mutator adds a single entry with
 * a non-null key and value.
 * <p>
 * If the currentValue is null then this mutator does nothing. Consider using
 * {@link EnsureMutator} to first ensure the current value is not null
 * the required behaviour is to always ensure a non-null, populated map instance.
 *
 * @author Andrew Coates - 01/03/2015.
 */
public class EnsureMapNotEmptyMutator implements Mutator {
    public static final Mutator INSTANCE = new EnsureMapNotEmptyMutator();
    private static final TypeVariable<Class<Map>>[] MAP_TYPE_VARIABLES = Map.class.getTypeParameters();

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

    private Type getKeyType(Type type) {
        return TypeUtils.getTypeArgument(type, Map.class, MAP_TYPE_VARIABLES[0]);
    }

    private Type getValueType(Type type) {
        return TypeUtils.getTypeArgument(type, Map.class, MAP_TYPE_VARIABLES[1]);
    }
}

