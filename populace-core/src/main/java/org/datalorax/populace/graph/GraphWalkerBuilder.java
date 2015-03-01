package org.datalorax.populace.graph;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.field.filter.ExcludeStaticFieldsFilter;
import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.graph.inspector.Inspector;
import org.datalorax.populace.graph.inspector.InspectorUtils;
import org.datalorax.populace.typed.TypedCollection;

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
    public GraphWalkerBuilder withCustomInspectors(final TypedCollection<Inspector> walkers) {
        Validate.notNull(walkers, "customWalkers null");
        this.customWalkers = walkers;
        // Todo(ac): ensure walkers has defaults...
        return this;
    }

    @Override
    public GraphWalker build() {
        return new GraphWalker(buildConfig());
    }

    private WalkerContext buildConfig() {
        return new WalkerContext(fieldFilter, getWalkers());
    }

    private TypedCollection getWalkers() {
        return customWalkers == null ? InspectorUtils.defaultInspectors().build() : customWalkers;
    }
}
