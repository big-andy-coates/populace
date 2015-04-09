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

import org.datalorax.populace.core.util.TypeResolver;
import org.datalorax.populace.core.util.TypeUtils;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

public class FieldInfoTest {
    private FieldInfo fieldInfo;
    @Mock
    private RawField field;
    @Mock
    private Object owningInstance;
    @Mock
    private TypeResolver typeResolver;
    @Mock
    private PathProvider pathProvider;
    private Type genericType;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        genericType = TypeUtils.parameterise(Map.class, Map.class.getTypeParameters());

        fieldInfo = new FieldInfo(field, owningInstance, typeResolver, pathProvider);
    }

    @Test
    public void shouldGetNameFromField() throws Exception {
        // Given:
        when(field.getName()).thenReturn("bob");

        // When:
        final String name = fieldInfo.getName();

        // Then:
        assertThat(name, is("bob"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetDeclaringClassFromField() throws Exception {
        // Given:
        when(field.getDeclaringClass()).thenReturn((Class) this.getClass());

        // When:
        final Class<?> type = fieldInfo.getDeclaringClass();

        // Then:
        assertThat(type, is(equalTo(this.getClass())));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetTypeFromField() throws Exception {
        // Given:
        when(field.getType()).thenReturn((Class) this.getClass());

        // When:
        final Class<?> type = fieldInfo.getType();

        // Then:
        assertThat(type, is(equalTo(this.getClass())));
    }

    @Test
    public void shouldGetValueFromField() throws Exception {
        // Given:
        when(field.getValue(anyObject())).thenReturn("expected");

        // When:
        final Object value = fieldInfo.getValue();

        // Then:
        assertThat(value, is("expected"));
    }

    @Test
    public void shouldPassOwningInstanceWhenGettingValue() throws Exception {
        // Given:
        when(field.getValue(anyObject())).thenReturn("expected");

        // When:
        fieldInfo.getValue();

        // Then:
        verify(field).getValue(owningInstance);
    }

    @Test(expectedExceptions = FieldAccessException.class)
    public void shouldThrowFieldAccessExceptionIfNoAccessWhenGettingValue() throws Exception {
        // Given:
        when(field.getValue(anyObject())).thenThrow(new IllegalAccessException());

        // When:
        fieldInfo.getValue();
    }

    @Test
    public void shouldSetValueOnField() throws Exception {
        // When:
        fieldInfo.setValue("expected");

        // Then:
        verify(field).setValue(anyObject(), eq("expected"));
    }

    @Test
    public void shouldPassOwningInstanceWhenSettingValue() throws Exception {
        // When:
        fieldInfo.setValue(this);

        // Then:
        verify(field).setValue(eq(owningInstance), anyObject());
    }

    @Test(expectedExceptions = FieldAccessException.class)
    public void shouldThrowFieldAccessExceptionIfNoAccessWhenSettingValue() throws Exception {
        // Given:
        doThrow(new IllegalAccessException()).when(field).setValue(anyObject(), anyObject());

        // When:
        fieldInfo.setValue(this);
    }

    @Test
    public void shouldGetAnnotationFromField() throws Exception {
        // Given:
        final Test expected = getClass().getMethod("shouldGetAnnotationFromField").getAnnotation(Test.class);
        when(field.getAnnotation(Test.class)).thenReturn(expected);

        // When:
        final Test annotation = fieldInfo.getAnnotation(Test.class);

        // Then:
        assertThat(annotation, is(expected));
    }

    @Test
    public void shouldGetTransientFlagFromField() throws Exception {
        // Given:
        when(field.isTransient()).thenReturn(true);

        // When:
        assertThat(fieldInfo.isTransient(), is(true));

        // Given:
        when(field.isTransient()).thenReturn(false);

        // When:
        assertThat(fieldInfo.isTransient(), is(false));
    }

    @Test
    public void shouldGetStaticFlagFromField() throws Exception {
        // Given:
        when(field.isStatic()).thenReturn(true);

        // When:
        assertThat(fieldInfo.isStatic(), is(true));

        // Given:
        when(field.isStatic()).thenReturn(false);

        // When:
        assertThat(fieldInfo.isStatic(), is(false));
    }

    @Test
    public void shouldGetFinalFlagFromField() throws Exception {
        // Given:
        when(field.isFinal()).thenReturn(true);

        // When:
        assertThat(fieldInfo.isFinal(), is(true));

        // Given:
        when(field.isFinal()).thenReturn(false);

        // When:
        assertThat(fieldInfo.isFinal(), is(false));
    }

    @Test
    public void shouldReturnGenericTypeFromResolver() throws Exception {
        // Given:
        givenFieldHasValue("value");
        when(typeResolver.resolve(any(Type.class))).thenReturn(genericType);

        // When:
        final Type genericType = fieldInfo.getGenericType();

        // Then:
        assertThat(genericType, is(genericType));
    }

    @Test
    public void shouldGetGenericTypeFromFieldIfValueNull() throws Exception {
        // Given:
        givenFieldHasValue(null, Map.class);
        when(field.getGenericType()).thenReturn(genericType);

        // When:
        fieldInfo.getGenericType();

        // Then:
        verify(typeResolver).resolve(genericType);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetGenericTypeFromValueIfNotNull() throws Exception {
        // Given:
        givenFieldHasValue("value");

        // When:
        fieldInfo.getGenericType();

        // Then:
        verify(typeResolver).resolve(String.class);
    }

    @Test
    public void shouldDetectPrimitiveGenericTypes() throws Exception {
        // Given:
        givenFieldHasValue(41L, long.class);

        // When:
        final Type result = fieldInfo.getGenericType();

        // Then:
        assertThat(result, is(equalTo((Type) long.class)));
    }

    @Test
    public void shouldNotInvokeResolverForPrimitiveTypes() throws Exception {
        // Given:
        givenFieldHasValue(41L, long.class);

        // When:
        fieldInfo.getGenericType();

        // Then:
        verify(typeResolver, never()).resolve(any(Type.class));
    }

    @Test
    public void shouldNotGetCurrentValueForPimitiveTypes() throws Exception {
        // Given:
        givenFieldHasValue(41L, long.class);

        // When:
        fieldInfo.getGenericType();

        // Then:
        verify(field, never()).getValue(anyObject());
    }

    @Test
    public void shouldOnlyTakePathIntoAccountInEqualsAsPathIsUnique() throws Exception {
        // Given:
        when(pathProvider.getPath()).thenReturn("some/path");
        final FieldInfo another = new FieldInfo(field, owningInstance, typeResolver, pathProvider);

        // When:
        final boolean result = fieldInfo.equals(another);

        // Then:
        verify(pathProvider, times(2)).getPath();
        assertThat(result, is(true));
        verifyNoMoreInteractions(field, typeResolver);
    }

    @Test
    public void shouldNotBeEqualIfDifferentPaths() throws Exception {
        // Given:
        when(pathProvider.getPath()).thenReturn("some/path");
        final PathProvider otherPath = mock(PathProvider.class);
        when(otherPath.getPath()).thenReturn("different/path");
        final FieldInfo another = new FieldInfo(field, owningInstance, typeResolver, otherPath);

        // When:
        final boolean result = fieldInfo.equals(another);

        // Then:
        assertThat(result, is(false));
    }

    @Test
    public void shouldOnlyUsePathInHashCode() throws Exception {
        // Given:
        when(pathProvider.getPath()).thenReturn("some/path");

        // When:
        final int hashCode = fieldInfo.hashCode();

        // Then:
        assertThat(hashCode, is("some/path".hashCode()));
    }

    @Test
    public void shouldIncludePathInToString() throws Exception {
        // Given:
        when(pathProvider.getPath()).thenReturn("some/path");

        // When:
        final String string = fieldInfo.toString();

        // Then:
        assertThat(string, containsString("some/path"));
    }

    @Test
    public void shouldEnsureFieldAccessible() throws Exception {
        // When:
        fieldInfo.ensureAccessible();

        // Then:
        verify(field).ensureAccessible();
    }

    private void givenFieldHasValue(final Object value) throws IllegalAccessException {
        givenFieldHasValue(value, value.getClass());
    }

    @SuppressWarnings("unchecked")
    private void givenFieldHasValue(final Object value, Class type) throws IllegalAccessException {
        when(field.getValue(anyObject())).thenReturn(value);
        when(field.getType()).thenReturn(type);
    }
}