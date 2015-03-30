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
import java.util.List;

/**
 * Ensure Mutator for {@link java.util.List Lists}.
 *
 * If the currentValue is null or empty then this mutator does nothing.  For non-empty {@code lists} this mutator will
 * replace any null elements with a new instance.
 *
 *  @author Andrew Coates - 27/02/2015.
 */
public class EnsureListElementsNotNullMutator implements Mutator {
    public static final EnsureListElementsNotNullMutator INSTANCE = new EnsureListElementsNotNullMutator();
    private static final TypeVariable<Class<List>> LIST_TYPE_VARIABLE = List.class.getTypeParameters()[0];

    @SuppressWarnings("unchecked")
    private static List<Object> ensureList(final Type type, final Object currentValue) {
        Validate.isAssignableFrom(List.class, TypeUtils.getRawType(type, null), "Unsupported type %s", type);
        return (List<Object>) currentValue;
    }

    private static Type getComponentType(Type type) {
        return TypeUtils.getTypeArgument(type, List.class, LIST_TYPE_VARIABLE);
    }

    @Override
    public List<?> mutate(Type type, Object currentValue, final Object parent, PopulatorContext config) {
        final List<Object> list = ensureList(type, currentValue);
        if (list == null || list.isEmpty()) {
            return list;
        }

        final Type valueType = getComponentType(type);

        final int size = list.size();
        for (int i = 0; i != size; ++i) {
            if (list.get(i) != null) {
                continue;
            }

            final Object instance = config.createInstance(valueType, null);
            list.set(i, instance);
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
}
