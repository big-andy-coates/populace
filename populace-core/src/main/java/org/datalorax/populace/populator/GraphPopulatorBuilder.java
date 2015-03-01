package org.datalorax.populace.populator;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.field.filter.ExcludeStaticFieldsFilter;
import org.datalorax.populace.field.filter.ExcludeTransientFieldsFilter;
import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.field.filter.FieldFilterUtils;
import org.datalorax.populace.graph.GraphWalker;
import org.datalorax.populace.populator.mutator.MutatorUtils;
import org.datalorax.populace.typed.TypedCollection;

/**
 * Builder implementation for the GraphPopulator
 *
 * @author datalorax - 28/02/2015.
 */
class GraphPopulatorBuilder implements GraphPopulator.Builder {
    private static final FieldFilter DEFAULT_FIELD_FILTER = FieldFilterUtils.and(ExcludeStaticFieldsFilter.INSTANCE, ExcludeTransientFieldsFilter.INSTANCE);

    private TypedCollection<Mutator> mutators;
    private FieldFilter fieldFilter = DEFAULT_FIELD_FILTER;

    @Override
    public GraphPopulatorBuilder withFieldFilter(final FieldFilter filter) {
        Validate.notNull(filter, "filter null");
        fieldFilter = filter;
        return this;
    }

    @Override
    public GraphPopulatorBuilder withMutators(final TypedCollection<Mutator> mutators) {
        Validate.notNull(mutators, "config null");
        this.mutators = mutators;
        return this;
    }

    @Override
    public GraphPopulator build() {
        // Todo(aC):
        final GraphWalker walker = GraphWalker.newBuilder().withFieldFilter(fieldFilter).build();

        return new GraphPopulator(walker, buildPopulatorContext());
    }

    private TypedCollection<Mutator> buildMutatorConfig() {
        return mutators == null ? MutatorUtils.defaultMutators().build() : mutators;
    }

    private PopulatorContext buildPopulatorContext() {
        return new PopulatorContext(fieldFilter, buildMutatorConfig());
    }
}
