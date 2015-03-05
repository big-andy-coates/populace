package org.datalorax.populace.field.visitor;

import org.datalorax.populace.field.FieldInfo;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SetAccessibleFieldVisitorTest {
    @SuppressWarnings("UnusedDeclaration")  // Accessed via reflection
    private String inaccessibleField;

    @Test
    public void shouldSetFieldAccessible() throws Exception {
        // Given:
        final Field field = getClass().getDeclaredField("inaccessibleField");
        assertThat("precondition violated", field.isAccessible(), is(false));
        final FieldInfo fieldInfo = mock(FieldInfo.class);
        when(fieldInfo.getField()).thenReturn(field);

        // When:
        SetAccessibleFieldVisitor.INSTANCE.visit(fieldInfo);

        // Then:
        assertThat(field.isAccessible(), is(true));
    }
}