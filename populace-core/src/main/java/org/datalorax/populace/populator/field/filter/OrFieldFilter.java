package org.datalorax.populace.populator.field.filter;

import org.apache.commons.lang3.Validate;

import java.lang.reflect.Field;

/**
 * Combine two field filters with logical OR.
 * Field will be handled if either child filters return true from their
 * {@link FieldFilter#evaluate evaluate} call
 * @author datalorax - 28/02/2015.
 */
public class OrFieldFilter implements FieldFilter {
    private final FieldFilter first;
    private final FieldFilter second;

    public OrFieldFilter(final FieldFilter first, final FieldFilter second) {
        Validate.notNull(first, "first null");
        Validate.notNull(second, "second null");
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean evaluate(final Field field) {
        return first.evaluate(field) || second.evaluate(field);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final OrFieldFilter that = (OrFieldFilter) o;
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
        return "OR {" +  first + ", " + second + "}";
    }
}
