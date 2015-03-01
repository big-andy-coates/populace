package org.datalorax.populace.populator.field.filter;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AllFieldFilterTest {
    private FieldFilter first;
    private FieldFilter second;
    private FieldFilter third;
    private Field field;
    private FieldFilter filter;

    @BeforeMethod
    public void setUp() throws Exception {
        first = mock(FieldFilter.class, "first");
        second = mock(FieldFilter.class, "second");
        third = mock(FieldFilter.class, "third");
        field = getClass().getDeclaredField("field");

        filter = new AllFieldFilter(first, second, third);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowIfAnyFilterIsNull() throws Exception {
        new AllFieldFilter(first, null, third);
    }

    @Test
    public void shouldReturnFalseIfAnyFilterReturnsFalse() throws Exception {
        // Given:
        when(first.evaluate(field)).thenReturn(true);
        when(second.evaluate(field)).thenReturn(false);
        when(third.evaluate(field)).thenReturn(true);

        // Then:
        assertThat(filter.evaluate(field), is(false));
    }

    @Test
    public void shouldReturnTrueOnlyIfAllReturnTrue() throws Exception {
        // Given:
        when(first.evaluate(field)).thenReturn(true);
        when(second.evaluate(field)).thenReturn(true);
        when(third.evaluate(field)).thenReturn(true);

        // Then:
        assertThat(filter.evaluate(field), is(true));
    }
}