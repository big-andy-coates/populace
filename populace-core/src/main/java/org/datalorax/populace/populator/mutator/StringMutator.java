package org.datalorax.populace.populator.mutator;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;

import java.lang.reflect.Type;

/**
 * Mutator for dates
 * @author datalorax - 27/02/2015.
 */
public class StringMutator implements Mutator {
    public static final Mutator INSTANCE = new StringMutator();

    @Override
    public Object mutate(Type type, Object currentValue, final Object parent, PopulatorContext config) {
        if (!type.equals(String.class)) {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }

        return currentValue == null ? "dead parrot" : currentValue + " - dead parrot";
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
