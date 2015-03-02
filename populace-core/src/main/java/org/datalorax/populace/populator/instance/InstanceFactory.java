package org.datalorax.populace.populator.instance;

/**
 * Extension point to support 'pluggable' factories for creating new instances of types.
 * @author datalorax - 02/03/2015.
 */
public interface InstanceFactory {
    /**
     * @param rawType the type to create
     * @param parent the parent instance, which is needed to create inner class instances. Can be null.
     * @param <T> the type to create
     * @return a new instance
     * @throws org.datalorax.populace.populator.PopulatorException on failure to instantiate new instance
     */
    <T> T createInstance(Class<? extends T> rawType, final Object parent);
}
