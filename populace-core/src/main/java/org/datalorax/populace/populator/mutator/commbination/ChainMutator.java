package org.datalorax.populace.populator.mutator.commbination;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;

import java.lang.reflect.Type;

/**
 * @author datalorax - 02/03/2015.
 */
public class ChainMutator implements Mutator {
    private final Mutator first;
    private final Mutator second;

    public static Mutator chain(final Mutator first, final Mutator second) {
        return new ChainMutator(first, second);
    }

    public ChainMutator(final Mutator first, final Mutator second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public Object mutate(final Type type, final Object currentValue, final Object parent, final PopulatorContext config) {
        final Object mutated = first.mutate(type, currentValue, parent, config);
        return second.mutate(type, mutated, parent, config);
    }
}
