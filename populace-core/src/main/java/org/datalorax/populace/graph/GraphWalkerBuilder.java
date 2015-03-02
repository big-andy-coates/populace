package org.datalorax.populace.graph;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.field.filter.ExcludeStaticFieldsFilter;
import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.graph.inspector.Inspector;
import org.datalorax.populace.graph.inspector.InspectorUtils;
import org.datalorax.populace.typed.TypeMap;

/**
 * @author datalorax - 01/03/2015.
 */
public class GraphWalkerBuilder implements GraphWalker.Builder {
    private static final FieldFilter DEFAULT_FIELD_FILTER = ExcludeStaticFieldsFilter.INSTANCE;

    private FieldFilter fieldFilter = DEFAULT_FIELD_FILTER;
    private TypeMap inspectors;

    @Override
    public GraphWalkerBuilder withFieldFilter(final FieldFilter filter) {
        Validate.notNull(filter, "filter null");
        fieldFilter = filter;
        return this;
    }

    @Override
    public GraphWalkerBuilder withInspectors(final TypeMap<Inspector> inspectors) {
        Validate.notNull(inspectors, "inspectors null");
        Validate.notNull(inspectors.getDefault(), "No default inspector provided");
        Validate.notNull(inspectors.getArrayDefault(), "No default inspector provided for array types");
        this.inspectors = inspectors;
        return this;
    }

    @Override
    public GraphWalker build() {
        return new GraphWalker(buildConfig());
    }

    private WalkerContext buildConfig() {
        return new WalkerContext(fieldFilter, getWalkers());
    }

    private TypeMap getWalkers() {
        return inspectors == null ? InspectorUtils.defaultInspectors().build() : inspectors;
    }
}
