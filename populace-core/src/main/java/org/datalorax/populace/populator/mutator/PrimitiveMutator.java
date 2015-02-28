package org.datalorax.populace.populator.mutator;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorConfig;


import java.lang.reflect.Type;

/**
 * Populator for primitive and boxed primitive types
 * @author datalorax - 27/02/2015.
 */
public class PrimitiveMutator implements Mutator {
    @Override
    public Object mutate(Type type, Object currentValue, PopulatorConfig config) {
        if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            return currentValue == null || !((Boolean) currentValue);
        }
        if (type.equals(byte.class) || type.equals(Byte.class)) {
            return (byte) (currentValue == null ? 42 : ((Byte) currentValue) + 42);
        }
        if (type.equals(char.class) || type.equals(Character.class)) {
            return (char) (currentValue == null ? 'c' : ((Character) currentValue) + 1);
        }
        if (type.equals(short.class) || type.equals(Short.class)) {
            return (short) (currentValue == null ? 42 : ((Short) currentValue) + 42);
        }
        if (type.equals(int.class) || type.equals(Integer.class)) {
            return currentValue == null ? 42 : ((Integer) currentValue) + 42;
        }
        if (type.equals(long.class) || type.equals(Long.class)) {
            return currentValue == null ? 42L : ((Long) currentValue) + 42L;
        }
        if (type.equals(float.class) || type.equals(Float.class)) {
            return currentValue == null ? 4.2f : ((Float) currentValue) * 4.2f;
        }
        if (type.equals(double.class) || type.equals(Double.class)) {
            return currentValue == null ? 4.2 : ((Double) currentValue) * 4.2;
        }

        throw new IllegalArgumentException("Unsupported type: " + type);
    }
}

// Todo(ac): split & add generics e.g. NumberMutator<Number>
