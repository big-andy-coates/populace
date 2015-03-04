package org.datalorax.populace.graph;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.field.filter.FieldFilters;
import org.datalorax.populace.graph.inspector.Inspector;
import org.datalorax.populace.graph.inspector.Inspectors;
import org.datalorax.populace.typed.ImmutableTypeMap;

/**
 * @author datalorax - 01/03/2015.
 */
public class GraphWalkerBuilder implements GraphWalker.Builder {
    private FieldFilter fieldFilter = FieldFilters.defaults();
    private ImmutableTypeMap<Inspector> inspectors = Inspectors.defaults().build();

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
    public GraphWalkerBuilder withInspectors(final ImmutableTypeMap<Inspector> inspectors) {
        Validate.notNull(inspectors, "inspectors null");
        Validate.notNull(inspectors.getDefault(), "No default inspector provided");
        Validate.notNull(inspectors.getArrayDefault(), "No default inspector provided for array types");
        this.inspectors = inspectors;
        return this;
    }

    @Override
    public ImmutableTypeMap.Builder<Inspector> inspectorsBuilder() {
        return ImmutableTypeMap.asBuilder(inspectors);
    }

    @Override
    public GraphWalker build() {
        return new GraphWalker(buildConfig());
    }

    private WalkerContext buildConfig() {
        return new WalkerContext(fieldFilter, inspectors);
    }
}
