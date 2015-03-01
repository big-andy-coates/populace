package org.datalorax.populace.populator.graph;

import org.datalorax.populace.populator.field.filter.FieldFilter;
import org.datalorax.populace.populator.graph.walkers.Walker;
import org.datalorax.populace.populator.typed.TypedCollection;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author datalorax - 28/02/2015.
 */
public class WalkerConfig {
    private final FieldFilter fieldFilter;
    private final TypedCollection<Walker> walkers;

    public WalkerConfig(final FieldFilter fieldFilter, final TypedCollection<Walker> walkers) {
        this.fieldFilter = fieldFilter;
        this.walkers = walkers;
    }

    public boolean isExcludedField(final Field field) {
        return !fieldFilter.evaluate(field);
    }

    public Walker getWalker(final Type type) {
        return walkers.get(type);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final WalkerConfig that = (WalkerConfig) o;
        return fieldFilter.equals(that.fieldFilter) && walkers.equals(that.walkers);
    }

    @Override
    public int hashCode() {
        int result = fieldFilter.hashCode();
        result = 31 * result + walkers.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "WalkerConfig{" +
                "fieldFilter=" + fieldFilter +
                ", walkers=" + walkers +
                '}';
    }
}
