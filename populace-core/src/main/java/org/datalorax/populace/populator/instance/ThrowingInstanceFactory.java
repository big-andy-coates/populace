package org.datalorax.populace.populator.instance;

/**
 * Instance factory that throws an UnsupportedOperationException.
 * @author datalorax - 02/03/2015.
 */
public class ThrowingInstanceFactory implements InstanceFactory {
    public static final ThrowingInstanceFactory INSTANCE = new ThrowingInstanceFactory();

    @Override
    public <T> T createInstance(Class<? extends T> rawType, final Object parent) {
        throw new UnsupportedOperationException("Unsupported type: " + rawType);
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
