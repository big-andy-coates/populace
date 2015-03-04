package org.datalorax.populace.populator.instance;

/**
 * Instance factory that returns null.
 * @author datalorax - 02/03/2015.
 */
public class NullInstanceFactory implements InstanceFactory {
    public static final NullInstanceFactory INSTANCE = new NullInstanceFactory();

    @Override
    public <T> T createInstance(Class<? extends T> rawType, final Object parent) {
        return null;
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
