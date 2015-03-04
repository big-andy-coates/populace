package org.datalorax.populace.type;

import org.apache.commons.lang3.Validate;

import java.util.*;

/**
 * @author datalorax - 04/03/2015.
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
