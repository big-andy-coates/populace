package org.datalorax.populace.populator.instance;

/**
 * Extension to the InstanceFactory interface to allow the factory to be chained.
 *
 * @author datalorax - 03/03/2015.
 */
public interface ChainableInstanceFactory extends InstanceFactory {

    boolean supportsType(final Class<?> rawType);
}
