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

package org.datalorax.populace.type;

import org.apache.commons.lang3.Validate;

import java.util.*;

/**
 * @author Andrew Coates - 04/03/2015.
 */
public final class TypeUtils {
    private static final List<Class<?>> PRIMITIVE_TYPES = Collections.unmodifiableList(Arrays.asList(
        boolean.class, byte.class, char.class, short.class, int.class, long.class, float.class, double.class));

    private static final List<Class<?>> BOXED_PRIMITIVE_TYPES = Collections.unmodifiableList(Arrays.asList(
        Boolean.class, Byte.class, Character.class, Short.class, Integer.class, Long.class, Float.class, Double.class));

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_BOXED_TYPES = new HashMap<Class<?>, Class<?>>() {{
        put(boolean.class, Boolean.class);
        put(byte.class, Byte.class);
        put(char.class, Character.class);
        put(short.class, Short.class);
        put(int.class, Integer.class);
        put(long.class, Long.class);
        put(float.class, Float.class);
        put(double.class, Double.class);
    }};

    public static List<Class<?>> getPrimitiveTypes() {
        return PRIMITIVE_TYPES;
    }

    public static List<Class<?>> getBoxedPrimitiveTypes() {
        return BOXED_PRIMITIVE_TYPES;
    }

    public static Class<?> getBoxedTypeForPrimitive(Class<?> primitiveType) {
        final Class<?> boxed = PRIMITIVE_TO_BOXED_TYPES.get(primitiveType);
        Validate.notNull(boxed, "Not a primitive type: %s", primitiveType);
        return boxed;
    }
}
