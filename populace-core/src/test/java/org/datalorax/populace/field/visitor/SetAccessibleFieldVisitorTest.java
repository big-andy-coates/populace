package org.datalorax.populace.field.visitor;

import org.testng.annotations.Test;

import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SetAccessibleFieldVisitorTest {
    @SuppressWarnings("UnusedDeclaration")  // Accessed via reflection
    private String inaccessibleField;

    @Test
    public void shouldSetFieldAccessible() throws Exception {
        // Given:
        final Field field = getClass().getDeclaredField("inaccessibleField");
        assertThat("precondition violated", field.isAccessible(), is(false));

        // When:
        SetAccessibleFieldVisitor.INSTANCE.visit(field, null);

        // Then:
        assertThat(field.isAccessible(), is(true));
    }
}