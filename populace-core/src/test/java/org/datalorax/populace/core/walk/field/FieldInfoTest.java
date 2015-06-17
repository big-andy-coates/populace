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
import org.datalorax.populace.core.util.TypeResolver;
import org.datalorax.populace.core.util.TypeUtils;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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
        givenFieldIsAccessible();
        givenFieldHasValue(null, Map.class);
        when(field.getGenericType()).thenReturn(genericType);

        // When:
        fieldInfo.getGenericType();

        // Then:
        verify(typeResolver).resolve(genericType);
    }

    @Test
    public void shouldGetGenericTypeFromFieldIfFieldInaccessible() throws Exception {
        // Given:
        givenFieldIsNotAccessible();
        givenFieldHasValue("some-value");
        when(field.getGenericType()).thenReturn(genericType);

        // When:
        fieldInfo.getGenericType();

        // Then:
        verify(typeResolver).resolve(genericType);
    }

    @Test
    public void shouldGetGenericTypeFromValueIfNotNullAndAccessible() throws Exception {
        // Given:
        givenFieldIsAccessible();
        givenFieldHasValue("some-value");

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
    public void shouldNotGetCurrentValueForPrimitiveTypes() throws Exception {
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
        givenFieldHasValue("value");

        // When:
        final String string = fieldInfo.toString();

        // Then:
        assertThat(string, containsString("some/path"));
    }

    @Test
    public void shouldIncludePrimitiveTypeInPath() throws Exception {
        // Given:
        givenFieldIsAccessible();
        givenFieldHasType(long.class);

        // When:
        final String string = fieldInfo.toString();

        // Then:
        assertThat(string, containsString("long"));
    }

    @Test
    public void shouldIncludeGenericTypeInPathIfAccessible() throws Exception {
        // Given:
        givenFieldIsAccessible();
        givenFieldHasValue(new ArrayList(), List.class);
        when(typeResolver.resolve(any(Type.class))).thenReturn(genericType);

        // When:
        final String string = fieldInfo.toString();

        // Then:
        assertThat(string, containsString("type=java.util.Map<K, V>"));
    }

    @Test
    public void shouldIncludeNonGenericTypeOInPathIfInaccessible() throws Exception {
        // Given:
        givenFieldIsNotAccessible();
        givenFieldHasType(Map.class);
        when(typeResolver.resolve(any(Type.class))).thenReturn(genericType);

        // When:
        final String string = fieldInfo.toString();

        // Then:
        assertThat(string, containsString("type=java.util.Map<K, V>"));
    }

    @Test
    public void shouldReturnInaccessible() throws Exception {
        // Given:
        givenFieldIsNotAccessible();

        // Then:
        assertThat("should be inaccessible if field is inaccessible", fieldInfo.isAccessible(), is(false));
    }

    @Test
    public void shouldReturnAccessible() throws Exception {
        // Given:
        givenFieldIsAccessible();

        // Then:
        assertThat("should be accessible if field is accessible", fieldInfo.isAccessible(), is(true));
    }

    @Test
    public void shouldEnsureFieldAccessible() throws Exception {
        // When:
        fieldInfo.ensureAccessible();

        // Then:
        verify(field).ensureAccessible();
    }

    @Test
    public void shouldReportDepth() throws Exception {
        // Given:
        when(pathProvider.getDepth()).thenReturn(43);

        // Then:
        assertThat(fieldInfo.getDepth(), is(43));
    }

    @Test
    public void shouldNotPropagateOwningInstanceToStringExceptions() throws Exception {
        // Given:
        givenFieldHasType(Map.class);
        when(owningInstance.toString()).thenThrow(new RuntimeException("BANG!"));

        // When:
        final String string = fieldInfo.toString();

        // Then:
        // Didn't go pop!
        assertThat(string, is(not("")));
    }

    @Test
    public void shouldNotPropagateRawFieldToStringExceptions() throws Exception {
        // Given:
        givenFieldHasType(String.class);
        when(field.toString()).thenThrow(new RuntimeException("BANG!"));

        // When:
        final String string = fieldInfo.toString();

        // Then:
        // Didn't go pop!
        assertThat(string, is(not("")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        // Given:
        givenFieldHasValue("value");

        final PathProvider otherPathProvider = mock(PathProvider.class, "other");
        when(pathProvider.getPath()).thenReturn("somePath");
        when(otherPathProvider.getPath()).thenReturn("differentPath");

        final RawField otherField = mock(RawField.class, "other");
        when(otherField.getType()).thenReturn((Class) long.class);

        // Then:
        new EqualsTester()
            .addEqualityGroup(
                new FieldInfo(field, owningInstance, typeResolver, pathProvider),
                new FieldInfo(field, owningInstance, typeResolver, pathProvider),
                new FieldInfo(otherField, owningInstance, typeResolver, pathProvider),
                new FieldInfo(field, new Object(), typeResolver, pathProvider),
                new FieldInfo(field, owningInstance, mock(TypeResolver.class, "other"), pathProvider))
            .addEqualityGroup(
                new FieldInfo(field, owningInstance, typeResolver, otherPathProvider))
            .testEquals();
    }

    @Test
    public void shouldThrowNPEsOnConstructorParams() throws Exception {
        new NullPointerTester()
            .setDefault(RawField.class, field)
            .setDefault(TypeResolver.class, typeResolver)
            .setDefault(PathProvider.class, pathProvider)
            .testAllPublicConstructors(FieldInfo.class);
    }

    private void givenFieldHasValue(final Object value) throws ReflectiveOperationException {
        givenFieldHasValue(value, value.getClass());
    }

    private void givenFieldHasValue(final Object value, final Class type) throws ReflectiveOperationException {
        when(field.getValue(anyObject())).thenReturn(value);
        givenFieldHasType(type);
    }

    @SuppressWarnings("unchecked")
    private void givenFieldHasType(final Class type) {
        when(field.getType()).thenReturn(type);
    }

    private void givenFieldIsAccessible() {
        when(field.isAccessible()).thenReturn(true);
    }

    private void givenFieldIsNotAccessible() {
        when(field.isAccessible()).thenReturn(false);
    }
}