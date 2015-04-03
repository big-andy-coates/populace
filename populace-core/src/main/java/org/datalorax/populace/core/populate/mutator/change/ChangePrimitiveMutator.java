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

import org.datalorax.populace.core.populate.Mutator;
import org.datalorax.populace.core.populate.PopulatorContext;

import java.lang.reflect.Type;

/**
 * Populator for primitive and boxed primitive types
 *
 * @author Andrew Coates - 27/02/2015.
 */
public class ChangePrimitiveMutator implements Mutator {
    public static final Mutator INSTANCE = new ChangePrimitiveMutator();

    @Override
    public Object mutate(Type type, Object currentValue, final Object parent, PopulatorContext config) {
        if (currentValue == null) {
            return null;
        }

        if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            return !((Boolean) currentValue);
        }
        if (type.equals(byte.class) || type.equals(Byte.class)) {
            return (byte) (((Byte) currentValue) + 42);
        }
        if (type.equals(char.class) || type.equals(Character.class)) {
            return (char) (((Character) currentValue) + 1);
        }
        if (type.equals(short.class) || type.equals(Short.class)) {
            return (short) (((Short) currentValue) + 42);
        }
        if (type.equals(int.class) || type.equals(Integer.class)) {
            return ((Integer) currentValue) + 42;
        }
        if (type.equals(long.class) || type.equals(Long.class)) {
            return ((Long) currentValue) + 42L;
        }
        if (type.equals(float.class) || type.equals(Float.class)) {
            return ((Float) currentValue) * 4.2f;
        }
        if (type.equals(double.class) || type.equals(Double.class)) {
            return ((Double) currentValue) * 4.2;
        }

        throw new IllegalArgumentException("Unsupported type: " + type);
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