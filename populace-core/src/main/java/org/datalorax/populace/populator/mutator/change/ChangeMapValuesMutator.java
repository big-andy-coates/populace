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

package org.datalorax.populace.populator.mutator.change;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;
import org.datalorax.populace.type.TypeUtils;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

/**
 * A mutator the mutates each value in a map.
 * <p>
 * If the currentValue is null or empty then this mutator does nothing. Consider using
 * {@link org.datalorax.populace.populator.mutator.ensure.EnsureMutator} to first ensure the current value is not null,
 * and/or {@link org.datalorax.populace.populator.mutator.ensure.EnsureMapNotEmptyMutator} to ensure the map is not empty, if
 * the required behaviour is to always ensure a non-null, populated map instance.
 *
 * @author Andrew Coates - 27/02/2015.
 */
// Todo(ac): Given that the system knows about containers... why do we need special mutators for maps?
// Todo(ac): Just mutate the values in the map.... and does this mean we're mutating sets?
public class ChangeMapValuesMutator implements Mutator {
    private static final TypeVariable<Class<Map>>[] MAP_TYPE_VARIABLES = Map.class.getTypeParameters();
    public static final Mutator INSTANCE = new ChangeMapValuesMutator();

    @Override
    public Map<?, ?> mutate(Type type, Object currentValue, final Object parent, PopulatorContext config) {
        Validate.isAssignableFrom(Map.class, TypeUtils.getRawType(type, Map.class), "Mutator only supports map types");

        if (currentValue == null) {
            return null;
        }

        //noinspection unchecked
        final Map<Object, Object> map = (Map) currentValue;
        final Type defaultValueType = getValueType(type);
        final Mutator defaultValueMutator = config.getMutator(defaultValueType);

        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            final Object original = entry.getValue();
            final Type valueType = original == null ? defaultValueType : original.getClass();                           // Todo(ac): same for array
            final Mutator mutator = original == null ? defaultValueMutator : config.getMutator(original.getClass());    // Todo(ac): same for array
            final Object mutated = mutator.mutate(valueType, original, null, config);   //
            entry.setValue(mutated);
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

    private Type getValueType(Type type) {
        return TypeUtils.getTypeArgument(type, Map.class, MAP_TYPE_VARIABLES[1]);
    }
}
