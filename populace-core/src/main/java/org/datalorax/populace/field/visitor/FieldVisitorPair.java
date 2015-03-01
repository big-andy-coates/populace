package org.datalorax.populace.field.visitor;

import org.apache.commons.lang3.Validate;

import java.lang.reflect.Field;

/**
 * Combination visitor: combines two field visitors. The visitors will be called in order.
 * @author datalorax - 28/02/2015.
 */
public class FieldVisitorPair implements FieldVisitor {
    private final FieldVisitor first;
    private final FieldVisitor second;

    public FieldVisitorPair(final FieldVisitor first, final FieldVisitor second) {
        Validate.notNull(first, "fist null");
        Validate.notNull(second, "second null");
        this.first = first;
        this.second = second;
    }

    @Override
    public void visit(final Field field, final Object instance) {
        first.visit(field, instance);
        second.visit(field, instance);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final FieldVisitorPair that = (FieldVisitorPair) o;
        return first.equals(that.first) && second.equals(that.second);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "[" +  first + ", " + second + "]";
    }
}
