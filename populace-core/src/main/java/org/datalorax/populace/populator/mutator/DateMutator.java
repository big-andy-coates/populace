package org.datalorax.populace.populator.mutator;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Mutator for dates
 * @author datalorax - 26/02/2015.
 */
public class DateMutator implements Mutator {
    public static final Mutator INSTANCE = new DateMutator();

    @Override
    public Object mutate(Type type, Object currentValue, PopulatorContext config) {
        if (!type.equals(Date.class)) {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }

        return currentValue == null ? new Date() : new Date(((Date) currentValue).getTime() + 10000);
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