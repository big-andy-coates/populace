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

    public static Mutator chain(final Mutator first, final Mutator second, final Mutator... additional) {
        ChainMutator chain = new ChainMutator(first, second);
        for (Mutator mutator : additional) {
            chain = new ChainMutator(chain, mutator);
        }
        return chain;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ChainMutator that = (ChainMutator) o;
        return first.equals(that.first) && second.equals(that.second);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ChainMutator{" +
            "first=" + first +
            ", second=" + second +
            '}';
    }
}
