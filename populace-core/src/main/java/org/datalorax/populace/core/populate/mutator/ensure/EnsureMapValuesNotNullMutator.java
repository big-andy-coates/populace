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
 * Ensure mutator for {@link java.util.Map maps}
 *
 * If the current value is null or empty then this mutator does nothing. For non-empty {@code maps} this mutator will
 * replace any null values with new instances.
 *
 * @author Andrew Coates - 27/02/2015.
 */
public class EnsureMapValuesNotNullMutator implements Mutator {
    public static final EnsureMapValuesNotNullMutator INSTANCE = new EnsureMapValuesNotNullMutator();
    private static final TypeVariable<Class<Map>>[] MAP_TYPE_VARIABLES = Map.class.getTypeParameters();

    @Override
    public Map<?, ?> mutate(Type type, Object currentValue, final Object parent, PopulatorContext config) {
        final Map<Object, Object> map = ensureMap(type, currentValue);
        if (map == null || map.isEmpty()) {
            return map;
        }

        final Type valueType = getValueType(type);

        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                continue;
            }

            final Object instance = config.createInstance(valueType, null);
            entry.setValue(instance);
        }
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

    @SuppressWarnings("unchecked")
    private static Map<Object, Object> ensureMap(final Type type, final Object currentValue) {
        Validate.isAssignableFrom(Map.class, TypeUtils.getRawType(type, Map.class), "Mutator only supports map types");
        return (Map<Object, Object>) currentValue;
    }

    private static Type getValueType(Type type) {
        return TypeUtils.getTypeArgument(type, MAP_TYPE_VARIABLES[1]);
    }
}
