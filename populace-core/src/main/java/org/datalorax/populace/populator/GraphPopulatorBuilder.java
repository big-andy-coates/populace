package org.datalorax.populace.populator;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.field.filter.ExcludeStaticFieldsFilter;
import org.datalorax.populace.field.filter.ExcludeTransientFieldsFilter;
import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.field.filter.FieldFilters;
import org.datalorax.populace.graph.GraphWalker;
import org.datalorax.populace.graph.inspector.Inspector;
import org.datalorax.populace.populator.instance.InstanceFactories;
import org.datalorax.populace.populator.instance.InstanceFactory;
import org.datalorax.populace.populator.mutator.Mutators;
import org.datalorax.populace.typed.TypeMap;

/**
 * Builder implementation for the GraphPopulator
 *
 * @author datalorax - 28/02/2015.
 */
class GraphPopulatorBuilder implements GraphPopulator.Builder {
    private static final FieldFilter DEFAULT_FIELD_FILTER = FieldFilters.and(ExcludeStaticFieldsFilter.INSTANCE, ExcludeTransientFieldsFilter.INSTANCE);

    private TypeMap<Mutator> mutators;
    private TypeMap<InstanceFactory> instanceFactories;
    private GraphWalker.Builder walkerBuilder = GraphWalker.newBuilder().withFieldFilter(DEFAULT_FIELD_FILTER);

    @Override
    public GraphPopulatorBuilder withFieldFilter(final FieldFilter filter) {
        Validate.notNull(filter, "filter null");
        walkerBuilder.withFieldFilter(filter);
        return this;
    }

    @Override
    public GraphPopulator.Builder withInspectors(final TypeMap<Inspector> inspectors) {
        walkerBuilder.withInspectors(inspectors);
        return this;
    }

    @Override
    public GraphPopulatorBuilder withMutators(final TypeMap<Mutator> mutators) {
        Validate.notNull(mutators, "config null");
        Validate.notNull(mutators.getDefault(), "No default mutator provided");
        Validate.notNull(mutators.getArrayDefault(), "No default mutator provided for array types");
        this.mutators = mutators;
        return this;
    }

    @Override
    public GraphPopulatorBuilder withInstanceFactories(final TypeMap<InstanceFactory> instanceFactories) {
        Validate.notNull(instanceFactories, "instanceFactories null");
        Validate.notNull(instanceFactories.getDefault(), "No default instance factory provided");
        Validate.notNull(instanceFactories.getArrayDefault(), "No default instance factory provided for array types");
        this.instanceFactories = instanceFactories;
        return this;
    }

    @Override
    public GraphPopulator build() {
        final GraphWalker walker = walkerBuilder.build();
        return new GraphPopulator(walker, buildPopulatorContext());
    }

    private TypeMap<Mutator> buildMutators() {
        return mutators == null ? Mutators.defaultMutators().build() : mutators;
    }

    private TypeMap<InstanceFactory> buildInstanceFactories() {
        return instanceFactories == null ? InstanceFactories.defaultFactories().build() : instanceFactories;
    }

    private PopulatorContext buildPopulatorContext() {
        return new PopulatorContext(buildMutators(), buildInstanceFactories());
    }
}
