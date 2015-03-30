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

package org.datalorax.populace.core.populate.instance;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

/**
 * Instance factory for arrays
 *
 * @author Andrew Coates - 30/03/2015.
 */
public class ArrayInstanceFactory implements InstanceFactory {
    public static final ArrayInstanceFactory INSTANCE = new ArrayInstanceFactory();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T createInstance(Class<? extends T> type, final Object parent, final InstanceFactories instanceFactories) {
        Validate.isTrue(TypeUtils.isArrayType(type), "Unsupported type: " + type);

        final Type componentType = TypeUtils.getArrayComponentType(type);
        return (T) Array.newInstance((Class<?>) componentType, 1);
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
