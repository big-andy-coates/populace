package org.datalorax.populace.field.filter;

/**
 * Utils class containing help methods for working with and combining
 * {@link FieldFilter fieldFilters}
 * @author datalorax - 28/02/2015.
 */
public final class FieldFilters {
    public static FieldFilter defaults() {
        return and(ExcludeStaticFieldsFilter.INSTANCE, ExcludeTransientFieldsFilter.INSTANCE);
    }

    public static FieldFilter and(final FieldFilter first, final FieldFilter second) {
        return new AndFieldFilter(first, second);
    }

    public static FieldFilter or(final FieldFilter first, final FieldFilter second) {
        return new OrFieldFilter(first, second);
    }

    public static FieldFilter all(final FieldFilter first, final FieldFilter... theRest) {
        if (theRest.length == 0) {
            return first;
        }
        return new AllFieldFilter(first, theRest);
    }

    public static FieldFilter any(final FieldFilter first, final FieldFilter... theRest) {
        if (theRest.length == 0) {
            return first;
        }
        return new AnyFieldFilter(first, theRest);
    }

    private FieldFilters() {}
}
