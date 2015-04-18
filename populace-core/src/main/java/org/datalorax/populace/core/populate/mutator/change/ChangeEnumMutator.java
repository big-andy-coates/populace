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

package org.datalorax.populace.core.populate.mutator.change;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.populate.Mutator;
import org.datalorax.populace.core.populate.PopulatorContext;
import org.datalorax.populace.core.util.TypeUtils;

import java.lang.reflect.Type;

/**
 * Change mutator for enums. The mutator will change the value of the enum to another valid value in the enumeration,
 * assuming there is one. For enumerations with only a single value the value will not be changed. If the current value
 * is null the value will also not be changed.
 * @author Andrew Coates - 02/03/2015.
 */
public class ChangeEnumMutator implements Mutator {
    public static final ChangeEnumMutator INSTANCE = new ChangeEnumMutator();

    @Override
    public Object mutate(final Type type, final Object currentValue, final Object parent, final PopulatorContext config) {
        Validate.isTrue(TypeUtils.getRawType(type, null).isEnum(), "Enum type expected");
        if (currentValue == null) {
            return null;
        }
        return changeEnum((Enum) currentValue);
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

    private Object changeEnum(final Enum currentValue) {
        final Object[] allValues = currentValue.getDeclaringClass().getEnumConstants();
        if (allValues.length <= 1) {
            return currentValue;
        }

        final int newOrdinal = (currentValue.ordinal() + 1) % allValues.length;
        return allValues[newOrdinal];
    }
}
