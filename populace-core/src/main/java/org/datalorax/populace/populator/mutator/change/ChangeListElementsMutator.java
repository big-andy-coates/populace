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
import java.util.List;

/**
 * Change Mutator for {@link java.util.List Lists}. It is a specialised version of
 * {@link org.datalorax.populace.populator.mutator.change.ChangeCollectionElementsMutator}, which takes advantage of lists
 * extended API.
 *
 * If the currentValue is null then this mutator does nothing. Unlike {@code ChangeCollectionElementsMutator} this
 * mutator also does nothing if the list is empty. Consider using
 * {@link org.datalorax.populace.populator.mutator.ensure.EnsureMutator} to first ensure the current value is not null,
 * and/or {@link org.datalorax.populace.populator.mutator.ensure.EnsureCollectionNotEmptyMutator} to ensure the list is
 * not empty, if the required behaviour is to always ensure a non-null, populated list instance.
 * <p>
 * None null values are modified by mutating each entry in the list.
 *
 * @author Andrew Coates - 27/02/2015.
 */
public class ChangeListElementsMutator implements Mutator {
    private static final TypeVariable<Class<List>> LIST_TYPE_VARIABLE = List.class.getTypeParameters()[0];

    public static final ChangeListElementsMutator INSTANCE = new ChangeListElementsMutator();

    @Override
    public List<?> mutate(Type type, Object currentValue, final Object parent, PopulatorContext config) {
        Validate.isAssignableFrom(List.class, TypeUtils.getRawType(type, null), "Unsupported type %s", type);

        if (currentValue == null) {
            return null;
        }

        //noinspection unchecked
        final List<Object> list = (List)currentValue;
        final Type defaultComponentType = getComponentType(type);
        final Mutator defaultMutator = config.getMutator(defaultComponentType);

        final int size = list.size();
        for (int i = 0; i != size; ++i) {
            final Object original = list.get(i);
            final Type valueType = original == null ? defaultComponentType : original.getClass();
            final Mutator mutator = original == null ? defaultMutator : config.getMutator(original.getClass());
            final Object mutated = mutator.mutate(valueType, original, null, config);
            list.set(i, mutated);
        }
        return list;
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
        return TypeUtils.getTypeArgument(type, List.class, LIST_TYPE_VARIABLE);
    }
}
