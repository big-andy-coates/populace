package org.datalorax.populace.populator.mutator;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorConfig;

import java.lang.reflect.Type;

/**
 * A no-op mutator i.e. on that doesn't mutate.
 *
 * @author datalorax - 27/02/2015.
 */
public class PassThroughMutator implements Mutator {
    @Override
    public Object mutate(Type type, Object currentValue, PopulatorConfig config) {
        return currentValue;
    }
}
