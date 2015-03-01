package org.datalorax.populace.field.visitor;

/**
 * Utils class containing help methods for working with and combining
 * {@link org.datalorax.populace.field.visitor.FieldVisitor field visitors}
 * @author datalorax - 28/02/2015.
 */
public final class FieldVisitorUtils {
    public static FieldVisitor chain(final FieldVisitor first, final FieldVisitor second) {
        return new FieldVisitorPair(first, second);
    }

    private FieldVisitorUtils() {}
}
