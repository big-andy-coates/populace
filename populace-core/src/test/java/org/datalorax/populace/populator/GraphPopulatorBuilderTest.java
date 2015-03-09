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

import org.datalorax.populace.field.filter.ExcludeStaticFieldsFilter;
import org.datalorax.populace.field.filter.ExcludeTransientFieldsFilter;
import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.field.filter.FieldFilters;
import org.datalorax.populace.graph.GraphWalker;
import org.datalorax.populace.graph.inspector.Inspectors;
import org.datalorax.populace.populator.instance.InstanceFactories;
import org.datalorax.populace.populator.mutator.Mutators;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class GraphPopulatorBuilderTest {
    private GraphPopulatorBuilder builder;

    @BeforeMethod
    public void setUp() throws Exception {
        builder = new GraphPopulatorBuilder();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowIfFieldFilterIsNull() throws Exception {
        builder.withFieldFilter(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowIfMutatorConfigIsNull() throws Exception {
        builder.withMutators(null);
    }

    @Test
    public void shouldCreatePopulatorWithDefaults() throws Exception {
        // When:
        final GraphPopulator populator = builder.build();

        // Then:
        assertThat(populator.getConfig(), is(defaultPopulatorContext()));
    }

    @Test
    public void shouldCreatePopulatorWithSpecificMutators() throws Exception {
        // Given:
        //noinspection unchecked
        final Mutators mutators = mock(Mutators.class);
        builder.withMutators(mutators);

        // When:
        final GraphPopulator populator = builder.build();

        // Then:
        assertThat(populator.getConfig(), is(new PopulatorContext(mutators, defaultInstanceFactories())));
    }

    @Test
    public void shouldCreatePopulatorWithSpecificInstanceFactories() throws Exception {
        // Given:
        //noinspection unchecked
        final InstanceFactories factories = mock(InstanceFactories.class);
        builder.withInstanceFactories(factories);

        // When:
        final GraphPopulator populator = builder.build();

        // Then:
        assertThat(populator.getConfig(), is(new PopulatorContext(defaultMutatorConfig(), factories)));
    }

    @Test
    public void shouldCreatePopulatorWithSpecificInspectors() throws Exception {
        // Given:
        //noinspection unchecked
        final Inspectors inspectors = mock(Inspectors.class);
        builder.withInspectors(inspectors);

        // When:
        final GraphPopulator populator = builder.build();

        // Then:
        final GraphWalker walker = GraphWalker.newBuilder().withInspectors(inspectors).withFieldFilter(defaultFieldFilter()).build();
        assertThat(populator, is(new GraphPopulator(walker, defaultPopulatorContext())));
    }

    private static FieldFilter defaultFieldFilter() {
        return FieldFilters.and(ExcludeStaticFieldsFilter.INSTANCE, ExcludeTransientFieldsFilter.INSTANCE);
    }

    private static Mutators defaultMutatorConfig() {
        return Mutators.defaults();
    }

    private InstanceFactories defaultInstanceFactories() {
        return InstanceFactories.defaults();
    }

    private PopulatorContext defaultPopulatorContext() {
        return new PopulatorContext(defaultMutatorConfig(), defaultInstanceFactories());
    }
}