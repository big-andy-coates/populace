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
import org.datalorax.populace.core.util.GetterValidator;
import org.datalorax.populace.core.walk.inspector.annotation.AnnotationInspector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ImmutableGetterRawFieldTest {
    private Method getter;
    private Method otherGetter;
    private AnnotationInspector annotationInspector;
    private ImmutableGetterRawField rawField;
    private String currentValue = "bob";
    private GetterValidator validator;

    @BeforeMethod
    public void setUp() throws Exception {
        getter = getClass().getDeclaredMethod("getCurrentValue");
        otherGetter = getClass().getDeclaredMethod("getOther");
        annotationInspector = mock(AnnotationInspector.class);
        validator = mock(GetterValidator.class);

        rawField = new ImmutableGetterRawField("name", getter, annotationInspector, validator);
    }

    @Test
    public void shouldGetName() throws Exception {
        assertThat(rawField.getName(), is("name"));
    }

    @Test
    public void shouldGetDeclaringClass() throws Exception {
        assertThat(rawField.getDeclaringClass(), is(equalTo(getter.getDeclaringClass())));
    }

    @Test
    public void shouldGetType() throws Exception {
        assertThat(rawField.getType(), is(equalTo(getter.getReturnType())));
    }

    @Test
    public void shouldGetGenericType() throws Exception {
        assertThat(rawField.getGenericType(), is(getter.getGenericReturnType()));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIfNotValidGetter() throws Exception {
        // Given:
        doThrow(IllegalArgumentException.class).when(validator).validate(getter);

        // When:
        new ImmutableGetterRawField("name", getter, annotationInspector, validator);
    }

    @Test
    public void shouldGetValue() throws Exception {
        assertThat(rawField.getValue(this), is(currentValue));
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void shouldThrowOnSetValue() throws Exception {
        // When:
        rawField.setValue(this, "new value");
    }

    @Test
    public void shouldGetAnnotationViaInspector() throws Exception {
        // When:
        rawField.getAnnotation(Override.class);

        // Then:
        verify(annotationInspector).getAnnotation(Override.class, getter);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnValueFromAnnotationInspector() throws Exception {
        // Given:
        final Override expected = mock(Override.class);
        when(annotationInspector.getAnnotation(any(Class.class), any(Method.class))).thenReturn(expected);

        // When:
        final Override actual = rawField.getAnnotation(Override.class);

        // Then:
        assertThat(actual, is(expected));
    }

    @Test
    public void shouldReturnInaccessibleIfGetterInaccessible() throws Exception {
        // Given:
        getter.setAccessible(false);

        // Then:
        assertThat("should be inaccessible if field is inaccessible", rawField.isAccessible(), is(false));
    }

    @Test
    public void shouldEnsureAccessible() throws Exception {
        // Given:
        getter.setAccessible(false);

        // When:
        rawField.ensureAccessible();

        // Then:
        assertThat(getter.isAccessible(), is(true));
    }

    @Test
    public void shouldAlwaysReturnFalseFromIsTransient() throws Exception {
        assertThat(rawField.isTransient(), is(false));
    }

    @Test
    public void shouldAlwaysReturnFalseFromIsStatic() throws Exception {
        assertThat(rawField.isStatic(), is(false));
    }

    @Test
    public void shouldAlwaysReturnFalseFromIsFinal() throws Exception {
        assertThat(rawField.isFinal(), is(false));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                new ImmutableGetterRawField("someName", getter, annotationInspector),
                new ImmutableGetterRawField("someName", getter, annotationInspector, validator),
                new ImmutableGetterRawField("someName", getter, mock(AnnotationInspector.class, "other")))
            .addEqualityGroup(
                new ImmutableGetterRawField("otherName", getter, annotationInspector))
            .addEqualityGroup(
                new ImmutableGetterRawField("someName", otherGetter, annotationInspector, validator))
            .testEquals();
    }

    @Test
    public void shouldThrowNPEsOnConstructorParams() throws Exception {
        new NullPointerTester()
            .setDefault(String.class, "name")
            .setDefault(Method.class, getter)
            .setDefault(AnnotationInspector.class, annotationInspector)
            .testAllPublicConstructors(ImmutableGetterRawField.class);
    }

    // Getters used in the test:
    @SuppressWarnings("UnusedDeclaration")
    public String getCurrentValue() {
        return currentValue;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getOther() {
        return "";
    }
}