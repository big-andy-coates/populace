package org.datalorax.populace.graph;

import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.graph.inspector.Inspector;
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

public class WalkerContextTest {
    @Mock
    private FieldFilter fieldFilter;
    @Mock
    private TypeMap<Inspector> inspectors;
    private Field field;
    private WalkerContext context;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // Need to setup default for inspectors and instanceFactories else constructor throws.
        when(inspectors.getDefault()).thenReturn(mock(Inspector.class, "default"));
        when(inspectors.getArrayDefault()).thenReturn(mock(Inspector.class, "array default"));

        field = getClass().getDeclaredField("field");

        context = new WalkerContext(fieldFilter, inspectors);
    }

    @Test
    public void shouldExcludeFieldIfFilterReturnsFalse() throws Exception {
        // Given:
        when(fieldFilter.evaluate(field)).thenReturn(false);

        // Then:
        assertThat(context.isExcludedField(field), is(true));
    }

    @Test
    public void shouldNotExcludeFieldIfFilterReturnsTrue() throws Exception {
        // Given:
        when(fieldFilter.evaluate(field)).thenReturn(true);

        // Then:
        assertThat(context.isExcludedField(field), is(false));
    }

    // Todo(ac): more tests
}