package org.datalorax.populace.populator.mutator;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorConfig;

import java.lang.reflect.Type;

/**
 * Mutator for dates
 * @author datalorax - 27/02/2015.
 */
public class StringMutator implements Mutator {
    @Override
    public Object mutate(Type type, Object currentValue, PopulatorConfig config) {
        if (type.equals(String.class)) {
            return currentValue == null ? "dead parrot" : currentValue + " - dead parrot";
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
