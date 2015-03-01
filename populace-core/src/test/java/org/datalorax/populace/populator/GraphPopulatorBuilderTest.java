package org.datalorax.populace.populator;

import org.datalorax.populace.field.filter.ExcludeStaticFieldsFilter;
import org.datalorax.populace.field.filter.ExcludeTransientFieldsFilter;
import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.field.filter.FieldFilterUtils;
import org.datalorax.populace.populator.mutator.MutatorUtils;
import org.datalorax.populace.typed.TypedCollection;
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
        assertThat(populator.getConfig(), is(new PopulatorContext(defaultFieldFilter(), defaultMutatorConfig())));
    }

    @Test
    public void shouldCreatePopulatorWithSpecificFieldFilter() throws Exception {
        // Given:
        final FieldFilter customFilter = mock(FieldFilter.class);
        builder.withFieldFilter(customFilter);

        // When:
        final GraphPopulator populator = builder.build();

        // Then:
        assertThat(populator.getConfig(), is(new PopulatorContext(customFilter, defaultMutatorConfig())));
    }

    @Test
    public void shouldCreatePopulatorWithSpecificMutatorConfig() throws Exception {
        // Given:
        //noinspection unchecked
        final TypedCollection<Mutator> customConfig = mock(TypedCollection.class);
        builder.withMutators(customConfig);

        // When:
        final GraphPopulator populator = builder.build();

        // Then:
        assertThat(populator.getConfig(), is(new PopulatorContext(defaultFieldFilter(), customConfig)));
    }

    private static FieldFilter defaultFieldFilter() {
        return FieldFilterUtils.and(ExcludeStaticFieldsFilter.INSTANCE, ExcludeTransientFieldsFilter.INSTANCE);
    }

    private TypedCollection<Mutator> defaultMutatorConfig() {
        return MutatorUtils.defaultMutators().build();
    }
}