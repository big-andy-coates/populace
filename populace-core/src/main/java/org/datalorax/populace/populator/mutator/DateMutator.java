package org.datalorax.populace.populator.mutator;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorConfig;


import java.lang.reflect.Type;
import java.util.Date;

/**
 * Mutator for dates
 * @author datalorax - 26/02/2015.
 */
public class DateMutator implements Mutator {
    @Override
    public Object mutate(Type type, Object currentValue, PopulatorConfig config) {
        if (type.equals(Date.class)) {
            return currentValue == null ? new Date() : new Date(((Date) currentValue).getTime() + 10000);
        }

        throw new IllegalArgumentException("Unsupported type: " + type);
    }
}