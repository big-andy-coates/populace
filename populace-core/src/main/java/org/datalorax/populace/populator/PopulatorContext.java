package org.datalorax.populace.populator;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.typed.TypedCollection;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Holds details of a populator's configuration
 *
 * @author datalorax - 26/02/2015.
 */
public class PopulatorContext {
    private final FieldFilter fieldFilter;
    private final TypedCollection<Mutator> mutators;

    public PopulatorContext(final FieldFilter fieldFilter, final TypedCollection<Mutator> mutators) {
        Validate.notNull(fieldFilter, "fieldFilter null");
        Validate.notNull(mutators, "mutators null");
        // Todo(ac): check mutators has defaults.
        this.fieldFilter = fieldFilter;
        this.mutators = mutators;
    }

    public boolean isExcludedField(final Field field) {
        return !fieldFilter.evaluate(field);
    }

    public Mutator getMutator(Type type) {
        return mutators.get(type);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PopulatorContext that = (PopulatorContext) o;
        return fieldFilter.equals(that.fieldFilter) && mutators.equals(that.mutators);
    }

    @Override
    public int hashCode() {
        int result = fieldFilter.hashCode();
        result = 31 * result + mutators.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PopulatorContext{" +
                "fieldFilter=" + fieldFilter +
                ", mutators=" + mutators +
                '}';
    }
}
