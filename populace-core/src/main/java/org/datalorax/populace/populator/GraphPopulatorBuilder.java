package org.datalorax.populace.populator;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.field.filter.ExcludeStaticFieldsFilter;
import org.datalorax.populace.field.filter.ExcludeTransientFieldsFilter;
import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.field.filter.FieldFilters;
import org.datalorax.populace.graph.GraphWalker;
import org.datalorax.populace.graph.inspector.Inspector;
import org.datalorax.populace.populator.instance.InstanceFactories;
import org.datalorax.populace.populator.instance.InstanceFactoriesBuilder;
import org.datalorax.populace.populator.mutator.Mutators;
import org.datalorax.populace.typed.ImmutableTypeMap;

/**
 * Builder implementation for the GraphPopulator
 *
 * @author datalorax - 28/02/2015.
 */
class GraphPopulatorBuilder implements GraphPopulator.Builder {
    private static final FieldFilter DEFAULT_FIELD_FILTER = FieldFilters.and(ExcludeStaticFieldsFilter.INSTANCE, ExcludeTransientFieldsFilter.INSTANCE);

    private ImmutableTypeMap<Mutator> mutators = Mutators.defaultMutators().build();
    private InstanceFactories instanceFactories = InstanceFactoriesBuilder.defaults().build();
    private GraphWalker.Builder walkerBuilder = GraphWalker.newBuilder().withFieldFilter(DEFAULT_FIELD_FILTER);

    @Override
    public GraphPopulatorBuilder withFieldFilter(final FieldFilter filter) {
        Validate.notNull(filter, "filter null");
        walkerBuilder.withFieldFilter(filter);
        return this;
    }

    @Override
    public FieldFilter getFieldFilter() {
        return walkerBuilder.getFieldFilter();
    }

    @Override
    public GraphPopulator.Builder withInspectors(final ImmutableTypeMap<Inspector> inspectors) {
        walkerBuilder.withInspectors(inspectors);
        return this;
    }

    @Override
    public ImmutableTypeMap.Builder<Inspector> inspectorsBuilder() {
        return walkerBuilder.inspectorsBuilder();
    }

    @Override
    public GraphPopulatorBuilder withMutators(final ImmutableTypeMap<Mutator> mutators) {
        Validate.notNull(mutators, "config null");
        Validate.notNull(mutators.getDefault(), "No default mutator provided");
        Validate.notNull(mutators.getArrayDefault(), "No default mutator provided for array types");
        this.mutators = mutators;
        return this;
    }

    @Override
    public ImmutableTypeMap.Builder<Mutator> mutatorsBuilder() {
        return ImmutableTypeMap.asBuilder(mutators);
    }

    @Override
    public GraphPopulatorBuilder withInstanceFactories(final InstanceFactories instanceFactories) {
        Validate.notNull(instanceFactories, "instanceFactories null");
        this.instanceFactories = instanceFactories;
        return this;
    }

    @Override
    public InstanceFactories.Builder instanceFactoriesBuilder() {
        return InstanceFactories.asBuilder(instanceFactories);
    }

    @Override
    public GraphPopulator build() {
        final GraphWalker walker = walkerBuilder.build();
        return new GraphPopulator(walker, buildPopulatorContext());
    }

    private PopulatorContext buildPopulatorContext() {
        return new PopulatorContext(mutators, instanceFactories);
    }
}
