package org.datalorax.populace.populator;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.populator.field.filter.ExcludeStaticFieldsFilter;
import org.datalorax.populace.populator.field.filter.ExcludeTransientFieldsFilter;
import org.datalorax.populace.populator.field.filter.FieldFilter;
import org.datalorax.populace.populator.field.filter.FieldFilterUtils;

/**
 * Builder implementation for the GraphPopulator
 *
 * @author datalorax - 28/02/2015.
 */
class GraphPopulatorBuilder implements GraphPopulator.Builder {
    private static final FieldFilter DEFAULT_FIELD_FILTER = FieldFilterUtils.and(ExcludeStaticFieldsFilter.INSTANCE, ExcludeTransientFieldsFilter.INSTANCE);

    private MutatorConfig mutatorConfig;
    private FieldFilter fieldFilter = DEFAULT_FIELD_FILTER;

    @Override
    public GraphPopulatorBuilder withFieldFilter(final FieldFilter filter) {
        Validate.notNull(filter, "filter null");
        fieldFilter = filter;
        return this;
    }

    @Override
    public GraphPopulatorBuilder withMutatorConfig(final MutatorConfig config) {
        Validate.notNull(config, "config null");
        mutatorConfig = config;
        return this;
    }

    @Override
    public GraphPopulator build() {
        return new GraphPopulator(buildPopulatorConfig());
    }

    private MutatorConfig buildMutatorConfig() {
        return mutatorConfig == null ? new MutatorConfigBuilder().build() : mutatorConfig;
    }

    private PopulatorConfig buildPopulatorConfig() {
        return new PopulatorConfig(fieldFilter, buildMutatorConfig());
    }
}
