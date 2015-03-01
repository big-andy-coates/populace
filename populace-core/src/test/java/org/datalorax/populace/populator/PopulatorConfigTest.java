package org.datalorax.populace.populator;

import org.datalorax.populace.populator.field.filter.FieldFilter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PopulatorConfigTest {
    private PopulatorConfig config;
    private FieldFilter fieldFilter;
    private MutatorConfig mutatorConfig;
    private Field field;

    @BeforeMethod
    public void setUp() throws Exception {
        fieldFilter = mock(FieldFilter.class);
        mutatorConfig = mock(MutatorConfig.class);
        field = getClass().getDeclaredField("field");

        config = new PopulatorConfig(fieldFilter, mutatorConfig);
    }
    @Test
    public void shouldExcludeFieldIfFilterReturnsFalse() throws Exception {
        // Given:
        when(fieldFilter.evaluate(field)).thenReturn(false);

        // Then:
        assertThat(config.isExcludedField(field), is(true));
    }

    @Test
    public void shouldNotExcludeFieldIfFilterReturnsTrue() throws Exception {
        // Given:
        when(fieldFilter.evaluate(field)).thenReturn(true);

        // Then:
        assertThat(config.isExcludedField(field), is(false));
    }

    @Test
    public void shouldReturnMutatorConfig() throws Exception {
        assertThat(config.getMutatorConfig(), is(mutatorConfig));
    }
}