package org.datalorax.populace.populator.instance;

import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.Map;

/**
 * Instance factory for primitive types
 *
 * @author datalorax - 03/03/2015.
 */
@SuppressWarnings("unchecked")
public class PrimitiveInstanceFactory implements ChainableInstanceFactory {
    public static final PrimitiveInstanceFactory INSTANCE = new PrimitiveInstanceFactory();

    private static final Map<Class<?>, Object> DEFAULT_INSTANCE_MAP = new HashMap<Class<?>, Object>() {{
       put((Class<?>)boolean.class, false);
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
    public boolean supportsType(final Class<?> rawType) {
        return DEFAULT_INSTANCE_MAP.containsKey(rawType);
    }

    @Override
    public <T> T createInstance(final Class<? extends T> type, final Object parent) {
        final Object instance = DEFAULT_INSTANCE_MAP.get(type);
        Validate.notNull(instance, "Unsupported type %s", type);
        return (T)instance;
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
