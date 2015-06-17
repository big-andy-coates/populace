/*
 * Copyright (c) 2015 Andrew Coates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.datalorax.populace.core.walk.field;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;
import org.datalorax.populace.core.walk.inspector.annotation.AnnotationInspector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class StdRawFieldTest {
    @SuppressWarnings("UnusedDeclaration")
    private static final String STATIC_FIELD = "";

    @SuppressWarnings("UnusedDeclaration")
    private final String finalField = "";

    @SuppressWarnings("UnusedDeclaration")
    private transient String transientField = "";

    private Field field;
    private Field otherField;
    private AnnotationInspector annotationInspector;
    private RawField rawField;

    @BeforeMethod
    public void setUp() throws Exception {
        field = getClass().getDeclaredField("field");
        otherField = getClass().getDeclaredField("otherField");
        annotationInspector = mock(AnnotationInspector.class);

        field.setAccessible(true);
        otherField.setAccessible(true);

        rawField = new StdRawField(field, annotationInspector);
    }

    @Test
    public void shouldGetNameFromField() throws Exception {
        assertThat(rawField.getName(), is(field.getName()));
    }

    @Test
    public void shouldGetDeclaringClass() throws Exception {
        assertThat(rawField.getDeclaringClass(), is(equalTo((Class<?>) field.getDeclaringClass())));
    }

    @Test
    public void shouldGetType() throws Exception {
        assertThat(rawField.getType(), is(equalTo((Class<?>) field.getType())));
    }

    @Test
    public void shouldGetGenericType() throws Exception {
        assertThat(rawField.getGenericType(), is(field.getGenericType()));
    }

    @Test
    public void shouldGetValue() throws Exception {
        assertThat(rawField.getValue(this), is(field.get(this)));
    }

    @Test
    public void shouldSetValue() throws Exception {
        // When:
        rawField.setValue(this, otherField);

        // Then:
        assertThat(field.get(this), is(otherField.get(this)));
    }

    @Test
    public void shouldGetAnnotationViaInspector() throws Exception {
        // When:
        rawField.getAnnotation(Override.class);

        // Then:
        verify(annotationInspector).getAnnotation(field, Override.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnValueFromAnnotationInspector() throws Exception {
        // Given:
        final Override expected = mock(Override.class);
        when(annotationInspector.getAnnotation(any(Field.class), any(Class.class))).thenReturn(expected);

        // When:
        final Override actual = rawField.getAnnotation(Override.class);

        // Then:
        assertThat(actual, is(expected));
    }

    @Test
    public void shouldReturnInaccessible() throws Exception {
        // Given:
        field.setAccessible(false);

        // Then:
        assertThat("should be inaccessible if field is inaccessible", rawField.isAccessible(), is(false));
    }

    @Test
    public void shouldReturnAccessible() throws Exception {
        // Given:
        field.setAccessible(true);

        // Then:
        assertThat("should be accessible if field is accessible", rawField.isAccessible(), is(true));
    }

    @Test
    public void shouldEnsureAccessible() throws Exception {
        // Given:
        field.setAccessible(false);

        // When:
        rawField.ensureAccessible();

        // Then:
        assertThat(field.isAccessible(), is(true));
    }

    @Test
    public void shouldDetectTransientFields() throws Exception {
        final Field transientField = getClass().getDeclaredField("transientField");
        assertThat(rawField.isTransient(), is(false));
        assertThat(new StdRawField(transientField, annotationInspector).isTransient(), is(true));
    }

    @Test
    public void shouldDetectStaticFields() throws Exception {
        final Field staticField = getClass().getDeclaredField("STATIC_FIELD");
        assertThat(rawField.isStatic(), is(false));
        assertThat(new StdRawField(staticField, annotationInspector).isStatic(), is(true));
    }

    @Test
    public void shouldDetectFinalFields() throws Exception {
        final Field finalField = getClass().getDeclaredField("finalField");
        assertThat(rawField.isFinal(), is(false));
        assertThat(new StdRawField(finalField, annotationInspector).isFinal(), is(true));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                new StdRawField(field, annotationInspector),
                new StdRawField(field, annotationInspector),
                new StdRawField(field, mock(AnnotationInspector.class, "other")))
            .addEqualityGroup(
                new StdRawField(otherField, annotationInspector))
            .testEquals();
    }

    @Test
    public void shouldThrowNPEsOnConstructorParams() throws Exception {
        new NullPointerTester()
            .setDefault(Field.class, field)
            .setDefault(AnnotationInspector.class, annotationInspector)
            .testAllPublicConstructors(StdRawField.class);
    }
}