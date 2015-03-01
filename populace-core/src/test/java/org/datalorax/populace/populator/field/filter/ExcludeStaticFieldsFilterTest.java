package org.datalorax.populace.populator.field.filter;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ExcludeStaticFieldsFilterTest {
    @Test
    public void shouldExcludeStaticField() throws Exception {
        assertThat(ExcludeStaticFieldsFilter.INSTANCE.evaluate(SomeClass.class.getDeclaredField("_static")), is(false));
    }

    @Test
    public void shouldIncludeNonStaticField() throws Exception {
        assertThat(ExcludeStaticFieldsFilter.INSTANCE.evaluate(SomeClass.class.getDeclaredField("_nonStatic")), is(true));
    }

    @SuppressWarnings("UnusedDeclaration")
    private static final class SomeClass {
        private static String _static;
        private String _nonStatic;
    }
}