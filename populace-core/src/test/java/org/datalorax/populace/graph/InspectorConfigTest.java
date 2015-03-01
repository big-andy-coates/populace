package org.datalorax.populace.graph;

import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.graph.inspector.Inspector;
import org.datalorax.populace.typed.TypedCollection;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InspectorConfigTest {
    @Mock
    private FieldFilter fieldFilter;
    @Mock
    private TypedCollection<Inspector> walkers;
    private Field field;
    private WalkerContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        field = getClass().getDeclaredField("field");

        config = new WalkerContext(fieldFilter, walkers);
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
        final Inspector expected = mock(Inspector.class);
        when(walkers.get(type)).thenReturn(expected);

        // Then:
        final Inspector inspector = config.getInspector(type);

        assertThat(inspector, is(expected));
    }
}