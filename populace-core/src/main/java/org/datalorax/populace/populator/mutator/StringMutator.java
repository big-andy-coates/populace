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
}
