package org.datalorax.populace.field.filter;

import java.lang.reflect.Field;

/**
 * Interface for 'pluggable' filtering of fields
 * @author datalorax - 28/02/2015.
 */
public interface FieldFilter {
    /**
     * Called to determine if the field should be handled or excluded.
     * @param field the field to evaluate
     * @return true if the field should be handled, false if it should be skipped / excluded.
     */
    boolean evaluate(final Field field);
}
