package org.datalorax.populace.populator;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.populator.field.filter.FieldFilter;

import java.lang.reflect.Field;

/**
 * Holds details of a populator's configuration
 *
 * @author datalorax - 26/02/2015.
 */
public class PopulatorConfig {
    private final FieldFilter fieldFilter;
    private final MutatorConfig mutatorConfig;

    public PopulatorConfig(final FieldFilter fieldFilter, final MutatorConfig mutatorConfig) {
        Validate.notNull(fieldFilter, "fieldFilter null");
        Validate.notNull(mutatorConfig, "mutatorConfig null");
        this.fieldFilter = fieldFilter;
        this.mutatorConfig = mutatorConfig;
    }

    public boolean isExcludedField(final Field field) {
        return !fieldFilter.evaluate(field);
    }

    public MutatorConfig getMutatorConfig() {
        return mutatorConfig;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PopulatorConfig that = (PopulatorConfig) o;
        return fieldFilter.equals(that.fieldFilter) && mutatorConfig.equals(that.mutatorConfig);
    }

    @Override
    public int hashCode() {
        int result = fieldFilter.hashCode();
        result = 31 * result + mutatorConfig.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PopulatorConfig{" +
                "fieldFilter=" + fieldFilter +
                ", mutatorConfig=" + mutatorConfig +
                '}';
    }
}
