package org.datalorax.populace.populator.graph.walkers;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.field.visitor.FieldVisitor;
import org.datalorax.populace.populator.graph.WalkerConfig;
import org.datalorax.populace.populator.graph.WalkerException;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

/**
 * The default walker for walking arrays. It visits each element of the array.
 *
 * @author datalorax - 27/02/2015.
 */
public class StdArrayWalker implements Walker {
    public static final Walker INSTANCE = new StdArrayWalker();

    @Override
    public void walk(final Type type, final Object instance, final FieldVisitor visitor, final WalkerConfig config) {
        final Class<?> rawType = instance.getClass();
        Validate.isTrue(TypeUtils.isArrayType(rawType), "Not array type: " + rawType);

        final Type componentType = TypeUtils.getArrayComponentType(type);   // todo(Ac): support GenericArrayType
        if (!(componentType instanceof Class)) {
            throw new WalkerException("Failed to walk array type: " + rawType,
                    new UnsupportedOperationException("Component type not supported yet: " + componentType));
        }

        final Walker componentWalker = config.getWalker(componentType);

        final long length = Array.getLength(instance);
        for (int i = 0; i != length; ++i) {
            final Object value = Array.get(instance, i);

            visitor.visitArrayElement(null  , i, value);    // Todo(ac):

            componentWalker.walk(componentType, value, visitor, config);
        }
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
