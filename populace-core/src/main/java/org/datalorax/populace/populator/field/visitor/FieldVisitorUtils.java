package org.datalorax.populace.populator.field.visitor;

/**
 * Utils class containing help methods for working with and combining
 * {@link org.datalorax.populace.populator.field.visitor.FieldVisitor field visitors}
 * @author datalorax - 28/02/2015.
 */
public final class FieldVisitorUtils {
    public static FieldVisitor combine(final FieldVisitor first, final FieldVisitor second) {
        return new FieldVisitorPair(first, second);
    }

    private FieldVisitorUtils() {}
}
