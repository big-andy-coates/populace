package org.datalorax.populace.populator.graph;

import org.datalorax.populace.populator.field.filter.FieldFilter;
import org.datalorax.populace.populator.graph.walkers.Walker;
import org.datalorax.populace.populator.typed.TypedCollection;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WalkerConfigTest {
    @Mock
    private FieldFilter fieldFilter;
    @Mock
    private TypedCollection<Walker> walkers;
    private Field field;
    private WalkerConfig config;

    @BeforeMethod
    public void setUp() throws Exception {
        field = getClass().getDeclaredField("field");

        config = new WalkerConfig(fieldFilter, walkers);
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
    public void shouldReturnWalker() throws Exception {
        // Given:
        final Type type = getClass();
        final Walker expected = mock(Walker.class);
        when(walkers.get(type)).thenReturn(expected);

        // Then:
        final Walker walker = config.getWalker(type);

        assertThat(walker, is(expected));
    }
}