/*
 * Copyright (c) 2015 Andrew Coates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.datalorax.populace.populator;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.field.filter.ExcludeStaticFieldsFilter;
import org.datalorax.populace.field.filter.ExcludeTransientFieldsFilter;
import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.field.filter.FieldFilters;
import org.datalorax.populace.graph.GraphWalker;
import org.datalorax.populace.graph.inspector.Inspectors;
import org.datalorax.populace.populator.instance.InstanceFactories;
import org.datalorax.populace.populator.mutator.Mutators;

/**
 * Builder implementation for the GraphPopulator
 *
 * @author Andrew Coates - 28/02/2015.
 */
class GraphPopulatorBuilder implements GraphPopulator.Builder {
    private static final FieldFilter DEFAULT_FIELD_FILTER = FieldFilters.and(ExcludeStaticFieldsFilter.INSTANCE, ExcludeTransientFieldsFilter.INSTANCE);

    private Mutators mutators = Mutators.newBuilder().build();
    private InstanceFactories instanceFactories = InstanceFactories.newBuilder().build();
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
    public GraphPopulator.Builder withInspectors(final Inspectors inspectors) {
        walkerBuilder.withInspectors(inspectors);
        return this;
    }

    @Override
    public Inspectors.Builder inspectorsBuilder() {
        return walkerBuilder.inspectorsBuilder();
    }

    @Override
    public GraphPopulatorBuilder withMutators(final Mutators mutators) {
        Validate.notNull(mutators, "config null");
        this.mutators = mutators;
        return this;
    }

    @Override
    public Mutators.Builder mutatorsBuilder() {
        return Mutators.asBuilder(mutators);
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
