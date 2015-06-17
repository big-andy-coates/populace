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

package org.datalorax.populace.core.populate;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.populate.inspector.LoggingCollectionInspector;
import org.datalorax.populace.core.populate.instance.InstanceFactories;
import org.datalorax.populace.core.populate.mutator.Mutators;
import org.datalorax.populace.core.walk.GraphWalker;
import org.datalorax.populace.core.walk.element.ElementInfo;
import org.datalorax.populace.core.walk.field.FieldInfo;
import org.datalorax.populace.core.walk.field.filter.FieldFilter;
import org.datalorax.populace.core.walk.field.filter.FieldFilters;
import org.datalorax.populace.core.walk.inspector.Inspectors;
import org.datalorax.populace.core.walk.inspector.MapValueInspector;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Builder implementation for the GraphPopulator
 *
 * @author Andrew Coates - 28/02/2015.
 */
final class GraphPopulatorBuilder implements GraphPopulator.Builder {
    static final Predicate<FieldInfo> DEFAULT_FIELD_FILTER = FieldFilters.excludeStaticFields()
        .and(FieldFilters.excludeTransientFields());
    private final GraphWalker.Builder walkerBuilder = GraphWalker.newBuilder()
        .withFieldFilter(DEFAULT_FIELD_FILTER)
        .withInspectors(Inspectors.newBuilder()
            .withSuperInspector(Map.class, MapValueInspector.INSTANCE)  // Can't mutate keys, so only walk values.
            .withSuperInspector(Collection.class, LoggingCollectionInspector.INSTANCE)  // Log on immutable elements
            .build());
    private Mutators mutators = Mutators.defaults();
    private InstanceFactories instanceFactories = InstanceFactories.defaults();

    @Override
    public GraphPopulatorBuilder withFieldFilter(final Predicate<FieldInfo> filter) {
        Validate.notNull(filter, "filter null");
        walkerBuilder.withFieldFilter(filter);
        return this;
    }

    @SuppressWarnings("deprecation")
    @Override
    public FieldFilter getFieldFilter() {
        return walkerBuilder.getFieldFilter();
    }

    @Override
    public GraphPopulator.Builder withElementFilter(final Predicate<ElementInfo> filter) {
        walkerBuilder.withElementFilter(filter);
        return this;
    }

    @Override
    public Predicate<ElementInfo> getElementFilter() {
        return walkerBuilder.getElementFilter();
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
