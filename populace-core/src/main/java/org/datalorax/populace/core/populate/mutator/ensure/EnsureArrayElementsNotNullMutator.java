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

import java.lang.reflect.Array;
import java.lang.reflect.Type;

/**
 * Ensure Mutator for arrays.
 * <p>
 * If the currentValue is null or empty then this mutator does nothing.  For non-empty {@code arrays} this mutator will
 * replace any null elements with a new instance.
 *
 * @author Andrew Coates - 27/02/2015.
 */
public class EnsureArrayElementsNotNullMutator implements Mutator {
    public static final EnsureArrayElementsNotNullMutator INSTANCE = new EnsureArrayElementsNotNullMutator();

    private static Type getComponentType(Type type) {
        return TypeUtils.getArrayComponentType(type);
    }

    @Override
    public Object mutate(Type type, Object currentValue, final Object parent, PopulatorContext config) {
        Validate.isTrue(TypeUtils.isArrayType(type), "Unsupported type %s", type);
        if (currentValue == null) {
            return null;
        }

        final int length = Array.getLength(currentValue);
        if (length == 0) {
            return currentValue;
        }

        final Type valueType = getComponentType(type);

        for (int i = 0; i != length; ++i) {
            if (Array.get(currentValue, i) != null) {
                continue;
            }

            final Object instance = config.createInstance(valueType, null);
            Array.set(currentValue, i, instance);
        }
        return currentValue;
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
