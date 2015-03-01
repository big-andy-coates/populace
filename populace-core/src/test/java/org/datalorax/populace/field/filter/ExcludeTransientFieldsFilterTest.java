package org.datalorax.populace.field.filter;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ExcludeTransientFieldsFilterTest {
    @Test
    public void shouldExcludeTransientField() throws Exception {
        assertThat(ExcludeTransientFieldsFilter.INSTANCE.evaluate(SomeClass.class.getDeclaredField("_transient")), is(false));
    }

    @Test
    public void shouldIncludeNonTransientField() throws Exception {
        assertThat(ExcludeTransientFieldsFilter.INSTANCE.evaluate(SomeClass.class.getDeclaredField("_nonTransient")), is(true));
    }

    @SuppressWarnings("UnusedDeclaration")
    private static final class SomeClass {
        private transient String _transient;
        private String _nonTransient;
    }
}