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

import java.util.HashMap;
import java.util.Map;

/**
 * Instance factory for primitive types
 *
 * @author Andrew Coates - 03/03/2015.
 */
@SuppressWarnings("unchecked")
public class PrimitiveInstanceFactory implements InstanceFactory {
    public static final PrimitiveInstanceFactory INSTANCE = new PrimitiveInstanceFactory();

    private static final Map<Class<?>, Object> DEFAULT_INSTANCE_MAP = new HashMap<Class<?>, Object>() {{
       put(boolean.class, false);
       put(Boolean.class, true);
       put(byte.class, (byte) 42);
       put(Byte.class, (byte) 42);
       put(char.class, 'c');
       put(Character.class, 'c');
       put(short.class, (short)42);
       put(Short.class, (short)42);
       put(int.class, 42);
       put(Integer.class, 42);
       put(long.class, 42L);
       put(Long.class, 42L);
       put(float.class, 4.2f);
       put(Float.class, 4.2f);
       put(double.class, 4.2);
       put(Double.class, 4.2);
    }};

    @Override
    public <T> T createInstance(final Class<? extends T> type, final Object parent, final InstanceFactories instanceFactories) {
        return (T) DEFAULT_INSTANCE_MAP.get(type);
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
