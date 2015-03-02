package org.datalorax.populace.populator;

import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.populator.instance.InstanceFactory;
import org.datalorax.populace.typed.TypeMap;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PopulatorContextTest {
    @Mock
    private FieldFilter fieldFilter;
    @Mock
    private TypeMap<Mutator> mutators;
    @Mock
    private TypeMap<InstanceFactory> instanceFactories;
    private Field field;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // Need to setup default for mutators and instanceFactories else constructor throws.
        when(mutators.getDefault()).thenReturn(mock(Mutator.class, "default"));
        when(mutators.getArrayDefault()).thenReturn(mock(Mutator.class, "array default"));
        when(instanceFactories.getDefault()).thenReturn(mock(InstanceFactory.class, "default"));
        when(instanceFactories.getArrayDefault()).thenReturn(mock(InstanceFactory.class, "array default"));

        field = getClass().getDeclaredField("field");

        config = new PopulatorContext(fieldFilter, mutators, instanceFactories);
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