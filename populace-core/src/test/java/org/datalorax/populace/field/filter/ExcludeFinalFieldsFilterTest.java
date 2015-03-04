package org.datalorax.populace.field.filter;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ExcludeFinalFieldsFilterTest {
    @Test
    public void shouldExcludeFinalField() throws Exception {
        assertThat(ExcludeFinalFieldsFilter.INSTANCE.evaluate(SomeClass.class.getDeclaredField("_final")), is(false));
    }

    @Test
    public void shouldIncludeNonFinalField() throws Exception {
        assertThat(ExcludeFinalFieldsFilter.INSTANCE.evaluate(SomeClass.class.getDeclaredField("_nonFinal")), is(true));
    }

    @SuppressWarnings("UnusedDeclaration")
    private static final class SomeClass {
        private transient String _final;
        private String _nonFinal;
    }
}