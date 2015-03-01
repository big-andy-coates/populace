package org.datalorax.populace.field.filter;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AndFieldFilterTest {
    private FieldFilter first;
    private FieldFilter second;
    private Field field;
    private FieldFilter filter;

    @BeforeMethod
    public void setUp() throws Exception {
        first = mock(FieldFilter.class);
        second = mock(FieldFilter.class);
        field = getClass().getDeclaredField("field");

        filter = new AndFieldFilter(first, second);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowIfFirstFilterIsNull() throws Exception {
        new AndFieldFilter(null, second);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowIfSecondFilterIsNull() throws Exception {
        new AndFieldFilter(first, null);
    }

    @Test
    public void shouldReturnFalseIfBothReturnFalse() throws Exception {
        // Given:
        when(first.evaluate(field)).thenReturn(false);
        when(second.evaluate(field)).thenReturn(false);

        // Then:
        assertThat(filter.evaluate(field), is(false));
    }

    @Test
    public void shouldReturnFalseIfOnlyFirstReturnsTrue() throws Exception {
        // Given:
        when(first.evaluate(field)).thenReturn(true);
        when(second.evaluate(field)).thenReturn(false);

        // Then:
        assertThat(filter.evaluate(field), is(false));
    }

    @Test
    public void shouldReturnFalseIfOnlySecondReturnsTrue() throws Exception {
        // Given:
        when(first.evaluate(field)).thenReturn(false);
        when(second.evaluate(field)).thenReturn(true);

        // Then:
        assertThat(filter.evaluate(field), is(false));
    }

    @Test
    public void shouldReturnTrueOnlyIfBothReturnTrue() throws Exception {
        // Given:
        when(first.evaluate(field)).thenReturn(true);
        when(second.evaluate(field)).thenReturn(true);

        // Then:
        assertThat(filter.evaluate(field), is(true));
    }
}