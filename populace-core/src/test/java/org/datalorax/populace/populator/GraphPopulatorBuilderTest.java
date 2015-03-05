package org.datalorax.populace.populator;

import org.datalorax.populace.field.filter.ExcludeStaticFieldsFilter;
import org.datalorax.populace.field.filter.ExcludeTransientFieldsFilter;
import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.field.filter.FieldFilters;
import org.datalorax.populace.graph.GraphWalker;
import org.datalorax.populace.graph.inspector.Inspectors;
import org.datalorax.populace.populator.instance.InstanceFactories;
import org.datalorax.populace.populator.mutator.Mutators;
import org.datalorax.populace.typed.ImmutableTypeMap;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
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
        //noinspection unchecked
        final ImmutableTypeMap<Mutator> mutators = mock(ImmutableTypeMap.class);
        givenTypeMapHasDefaults(mutators, mock(Mutator.class));
        builder.withMutators(mutators);

        // When:
        final GraphPopulator populator = builder.build();

        // Then:
        assertThat(populator.getConfig(), is(new PopulatorContext(mutators, defaultInstanceFactories())));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowIfSpecificMutatorsHaveNoDefault() throws Exception {
        // Given:
        //noinspection unchecked
        final ImmutableTypeMap<Mutator> mutators = mock(ImmutableTypeMap.class);
        when(mutators.getArrayDefault()).thenReturn(mock(Mutator.class));

        // When:
        builder.withMutators(mutators);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowIfSpecificMutatorsHaveNoArrayDefault() throws Exception {
        // Given:
        //noinspection unchecked
        final ImmutableTypeMap<Mutator> mutators = mock(ImmutableTypeMap.class);
        when(mutators.getArrayDefault()).thenReturn(mock(Mutator.class));

        // When:
        builder.withMutators(mutators);
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

    private <T> void givenTypeMapHasDefaults(final ImmutableTypeMap<T> typeMap, T defaultValue) {
        when(typeMap.getDefault()).thenReturn(defaultValue);
        when(typeMap.getArrayDefault()).thenReturn(defaultValue);
    }

    private static FieldFilter defaultFieldFilter() {
        return FieldFilters.and(ExcludeStaticFieldsFilter.INSTANCE, ExcludeTransientFieldsFilter.INSTANCE);
    }

    private static ImmutableTypeMap<Mutator> defaultMutatorConfig() {
        return Mutators.defaultMutators().build();
    }

    private InstanceFactories defaultInstanceFactories() {
        return InstanceFactories.newBuilder().build();
    }

    private PopulatorContext defaultPopulatorContext() {
        return new PopulatorContext(defaultMutatorConfig(), defaultInstanceFactories());
    }
}