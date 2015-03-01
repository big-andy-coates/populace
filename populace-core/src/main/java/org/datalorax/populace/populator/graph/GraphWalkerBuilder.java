package org.datalorax.populace.populator.graph;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.populator.field.filter.ExcludeStaticFieldsFilter;
import org.datalorax.populace.populator.field.filter.FieldFilter;
import org.datalorax.populace.populator.graph.walkers.Walker;
import org.datalorax.populace.populator.typed.TypedCollection;

/**
 * @author datalorax - 01/03/2015.
 */
public class GraphWalkerBuilder implements GraphWalker.Builder {
    private static final FieldFilter DEFAULT_FIELD_FILTER = ExcludeStaticFieldsFilter.INSTANCE;

    private FieldFilter fieldFilter = DEFAULT_FIELD_FILTER;
    private TypedCollection customWalkers;

    @Override
    public GraphWalkerBuilder withFieldFilter(final FieldFilter filter) {
        Validate.notNull(filter, "filter null");
        fieldFilter = filter;
        return this;
    }

    @Override
    public GraphWalkerBuilder withCustomWalkers(final TypedCollection<Walker> walkers) {
        Validate.notNull(walkers, "customWalkers null");
        this.customWalkers = walkers;
        return this;
    }

    @Override
    public GraphWalker build() {
        return new GraphWalker(buildConfig());
    }

    private WalkerConfig buildConfig() {
        return new WalkerConfig(fieldFilter, getWalkers());
    }

    private TypedCollection getWalkers() {
        return customWalkers == null ? getDefaultWalkers() : customWalkers;
    }

    private TypedCollection getDefaultWalkers() {
        // Todo(ac):
        return null;
    }
}
