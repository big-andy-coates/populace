package org.datalorax.populace.populator;

import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.typed.TypedCollection;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class PopulatorContextTest {
    @Mock
    private FieldFilter fieldFilter;
    @Mock
    private TypedCollection<Mutator> mutators;
    private Field field;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        field = getClass().getDeclaredField("field");

        config = new PopulatorContext(fieldFilter, mutators);
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
}