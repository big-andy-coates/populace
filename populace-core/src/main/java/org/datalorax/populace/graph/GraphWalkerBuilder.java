package org.datalorax.populace.graph;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.field.filter.FieldFilters;
import org.datalorax.populace.graph.inspector.Inspectors;

/**
 * @author datalorax - 01/03/2015.
 */
public class GraphWalkerBuilder implements GraphWalker.Builder {
    private FieldFilter fieldFilter = FieldFilters.defaults();
    private Inspectors inspectors = Inspectors.newBuilder().build();

    @Override
    public GraphWalkerBuilder withFieldFilter(final FieldFilter filter) {
        Validate.notNull(filter, "filter null");
        fieldFilter = filter;
        return this;
    }

    @Override
    public FieldFilter getFieldFilter() {
        return fieldFilter;
    }

    @Override
    public GraphWalkerBuilder withInspectors(final Inspectors inspectors) {
        Validate.notNull(inspectors, "inspectors null");
        this.inspectors = inspectors;
        return this;
    }

    @Override
    public Inspectors.Builder inspectorsBuilder() {
        return Inspectors.asBuilder(inspectors);
    }

    @Override
    public GraphWalker build() {
        return new GraphWalker(buildConfig());
    }

    private WalkerContext buildConfig() {
        return new WalkerContext(fieldFilter, inspectors);
    }
}
