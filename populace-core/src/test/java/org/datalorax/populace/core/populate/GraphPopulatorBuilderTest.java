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

import org.datalorax.populace.core.populate.instance.InstanceFactories;
import org.datalorax.populace.core.populate.mutator.Mutators;
import org.datalorax.populace.core.walk.GraphWalker;
import org.datalorax.populace.core.walk.element.ElementInfo;
import org.datalorax.populace.core.walk.field.FieldInfo;
import org.datalorax.populace.core.walk.inspector.Inspector;
import org.datalorax.populace.core.walk.inspector.Inspectors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        final Inspectors inspectors = mock(Inspectors.class);
        builder.withInspectors(inspectors);

        // When:
        final GraphPopulator populator = builder.build();

        // Then:
        final GraphWalker walker = GraphWalker.newBuilder().withInspectors(inspectors).withFieldFilter(defaultFieldFilter()).build();
        assertThat(populator, is(new GraphPopulator(walker, defaultPopulatorContext())));
    }

    @Test
    public void shouldGetFieldFilterBackFromBuilder() throws Exception {
        // Given:
        final Predicate<FieldInfo> filter = getMockFieldFilter();
        when(filter.test(null)).thenReturn(true);
        builder.withFieldFilter(filter);

        // When:
        final Predicate<FieldInfo> returned = builder.getFieldFilter();

        // Then:
        assertThat(returned.test(null), is(true));
    }

    @Test
    public void shouldGetElementFilterBackFromBuilder() throws Exception {
        // Given:
        final Predicate<ElementInfo> filter = getMockElementFilter();
        when(filter.test(null)).thenReturn(true);
        builder.withElementFilter(filter);

        // When:
        final Predicate<ElementInfo> returned = builder.getElementFilter();

        // Then:
        assertThat(returned.test(null), is(true));
    }

    @Test
    public void shouldGetInspectorsBuilderBackFromBuilder() throws Exception {
        // Given:
        final Inspector packageInspector = mock(Inspector.class);
        final Inspectors inspectors = Inspectors.newBuilder().withPackageInspector("some.package", packageInspector).build();
        builder.withInspectors(inspectors);

        // When:
        final Inspectors.Builder returned = builder.inspectorsBuilder();

        // Then:
        assertThat(returned.build(), is(equalTo(inspectors)));
    }

    @Test
    public void shouldGetMutatorsBuilderBackFromBuilder() throws Exception {
        // Given:
        final Mutator specificMutator = mock(Mutator.class);
        final Mutators mutators = Mutators.newBuilder().withSpecificMutator(String.class, specificMutator).build();
        builder.withMutators(mutators);

        // When:
        final Mutators.Builder returned = builder.mutatorsBuilder();

        // Then:
        assertThat(returned.build(), is(equalTo(mutators)));
    }

    private InstanceFactories defaultInstanceFactories() {
        return InstanceFactories.defaults();
    }

    private PopulatorContext defaultPopulatorContext() {
        return new PopulatorContext(defaultMutatorConfig(), defaultInstanceFactories());
    }

    private static Predicate<FieldInfo> defaultFieldFilter() {
        return GraphPopulatorBuilder.DEFAULT_FIELD_FILTER;
    }

    private static Mutators defaultMutatorConfig() {
        return Mutators.defaults();
    }

    @SuppressWarnings("unchecked")
    private static Predicate<ElementInfo> getMockElementFilter() {
        return mock(Predicate.class);
    }

    @SuppressWarnings("unchecked")
    private static Predicate<FieldInfo> getMockFieldFilter() {
        return mock(Predicate.class);
    }
}