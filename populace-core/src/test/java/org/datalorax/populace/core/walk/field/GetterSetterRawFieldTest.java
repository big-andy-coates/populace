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
import org.datalorax.populace.core.util.GetterSetterValidator;
import org.datalorax.populace.core.walk.inspector.annotation.AnnotationInspector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.*;

public class GetterSetterRawFieldTest {
    private Method getter;
    private Method setter;
    private Method otherGetter;
    private Method otherSetter;
    private AnnotationInspector annotationInspector;
    private GetterSetterRawField rawField;
    private String currentValue;
    private GetterSetterValidator validator;

    @BeforeMethod
    public void setUp() throws Exception {
        getter = getClass().getDeclaredMethod("getCurrentValue");
        setter = getClass().getDeclaredMethod("setCurrentValue", String.class);
        otherGetter = getClass().getDeclaredMethod("getOther");
        otherSetter = getClass().getDeclaredMethod("setOther", String.class);
        annotationInspector = mock(AnnotationInspector.class);
        validator = mock(GetterSetterValidator.class);

        rawField = new GetterSetterRawField("name", getter, setter, annotationInspector, validator);
    }

    @Test
    public void shouldGetName() throws Exception {
        assertThat(rawField.getName(), is("name"));
    }

    @SuppressWarnings("UnusedDeclaration")
    @Test
    public void shouldGetMostDerivedDeclaringClass() throws Exception {
        // Given:
        class BaseType {
            int getIt() {
                return 0;
            }
        }
        class DerivedType extends BaseType {
            void setIt(int v) {
            }
        }
        rawField = new GetterSetterRawField("it",
            BaseType.class.getDeclaredMethod("getIt"),
            DerivedType.class.getDeclaredMethod("setIt", int.class),
            annotationInspector);

        // Then:
        assertThat(rawField.getDeclaringClass(), is(equalTo(DerivedType.class)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetTypeFromGetter() throws Exception {
        assertThat(rawField.getType(), is(equalTo((Class) getter.getReturnType())));
    }

    @Test
    public void shouldGetGenericTypeFromGetter() throws Exception {
        assertThat(rawField.getGenericType(), is(getter.getGenericReturnType()));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIfNotValidGetterSetterPair() throws Exception {
        // Given:
        doThrow(IllegalArgumentException.class).when(validator).validate(getter, setter);

        // When:
        new GetterSetterRawField("name", getter, setter, annotationInspector, validator);
    }

    @Test
    public void shouldGetValue() throws Exception {
        assertThat(rawField.getValue(this), is(currentValue));
    }

    @Test
    public void shouldSetValue() throws Exception {
        // When:
        rawField.setValue(this, "new value");

        // Then:
        assertThat(currentValue, is("new value"));
    }

    @Test
    public void shouldGetAnnotationViaInspector() throws Exception {
        // When:
        rawField.getAnnotation(Override.class);

        // Then:
        verify(annotationInspector).getAnnotation(Override.class, getter, setter);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnValueFromAnnotationInspector() throws Exception {
        // Given:
        final Override expected = mock(Override.class);
        when(annotationInspector.getAnnotation(any(Class.class), anyVararg())).thenReturn(expected);

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
    public void shouldReturnInaccessibleIfSetterInaccessible() throws Exception {
        // Given:
        setter.setAccessible(false);

        // Then:
        assertThat("should be inaccessible if field is inaccessible", rawField.isAccessible(), is(false));
    }

    @Test
    public void shouldReturnAccessibleIfBothAccessible() throws Exception {
        // Given:
        getter.setAccessible(true);
        setter.setAccessible(true);

        // Then:
        assertThat("should be accessible if field is accessible", rawField.isAccessible(), is(true));
    }

    @Test
    public void shouldEnsureBothAccessible() throws Exception {
        // Given:
        getter.setAccessible(false);
        setter.setAccessible(false);

        // When:
        rawField.ensureAccessible();

        // Then:
        assertThat(getter.isAccessible(), is(true));
        assertThat(setter.isAccessible(), is(true));
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
                new GetterSetterRawField("someName", getter, setter, annotationInspector),
                new GetterSetterRawField("someName", getter, setter, annotationInspector, validator),
                new GetterSetterRawField("someName", getter, setter, mock(AnnotationInspector.class, "other")))
            .addEqualityGroup(
                new GetterSetterRawField("otherName", getter, setter, annotationInspector))
            .addEqualityGroup(
                new GetterSetterRawField("someName", otherGetter, setter, annotationInspector, validator))
            .addEqualityGroup(
                new GetterSetterRawField("someName", getter, otherSetter, annotationInspector, validator))
            .testEquals();
    }

    @Test
    public void shouldThrowNPEsOnConstructorParams() throws Exception {
        new NullPointerTester()
            .setDefault(String.class, "name")
            .setDefault(Method.class, getter)
            .setDefault(AnnotationInspector.class, annotationInspector)
            .setDefault(GetterSetterValidator.class, validator)
            .testConstructor(GetterSetterRawField.class.getDeclaredConstructor(String.class,
                Method.class, Method.class, AnnotationInspector.class,
                GetterSetterValidator.class));
    }

    // Getters and setters used in the test:
    @SuppressWarnings("UnusedDeclaration")
    public String getCurrentValue() {
        return currentValue;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setCurrentValue(String v) {
        currentValue = v;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getOther() {
        return "";
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setOther(String v) {
    }
}