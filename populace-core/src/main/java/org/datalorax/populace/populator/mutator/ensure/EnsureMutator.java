package org.datalorax.populace.populator.mutator.ensure;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;

import java.lang.reflect.Type;

/**
 * A mutator that ensures the current value is not null
 *
 * @author datalorax - 01/03/2015.
 */
public class EnsureMutator implements Mutator {
    public static final Mutator INSTANCE = new EnsureMutator();

    @Override
    public Object mutate(final Type type, final Object currentValue, final Object parent, final PopulatorContext config) {
        return currentValue == null ? config.createInstance(type, parent) : currentValue;
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
