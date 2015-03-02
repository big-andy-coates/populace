package org.datalorax.populace.populator;

import java.lang.reflect.Type;

/**
 * Mutator interface.
 *
 * @author datalorax - 26/02/2015.
 */
public interface Mutator {  // Todo(ac): rename to FieldPopulator (and the main one to GraphPopulator)

    // Todo(ac): consider: split into more than one function. One for 'populating', or for 'mutating' - second part can be turned off. Need to think about default values...

    /**
     * Called to mutate the instance passed in <code>currentValue</code>, or create a new instance if the current value is null.
     *
     * @param type         the type of current value
     * @param currentValue the current value. Implementations are free to mutate this instance and return it, as opposed
     *                     to creating a new instance, if the type is mutable.
     * @param parent       the parent object to current value. This can be of use when working with inner classes.
     * @param config       the populator config
     * @return the mutated version of <code>currentValue</code>. This can be the same instance as
     * <code>currentValue</code> or a new instance.
     */
    Object mutate(final Type type, final Object currentValue, final Object parent, final PopulatorContext config);

    // Todo(ac): wrap type, currentValue and parent in an immuatable type to pass through (allow for extension)
}