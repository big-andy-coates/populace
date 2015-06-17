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

package org.datalorax.populace.core.walk.inspector.annotation;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class SimpleAnnotationInspectorTest {
    private SimpleAnnotationInspector inspector;

    @SuppressWarnings("UnusedDeclaration")
    @Deprecated
    private List<?> deprecatedField;

    @BeforeMethod
    public void setUp() throws Exception {
        inspector = SimpleAnnotationInspector.INSTANCE;
    }

    @Test
    public void shouldGetAnnotationOfField() throws Exception {
        // Given:
        final Field field = getClass().getDeclaredField("deprecatedField");
        final Deprecated expected = field.getAnnotation(Deprecated.class);

        // Then:
        assertThat(inspector.getAnnotation(field, Deprecated.class), is(expected));
    }

    @Test
    public void shouldReturnNullIfAnnotationDoesNotExistOnField() throws Exception {
        // Given:
        final Field field = getClass().getDeclaredField("deprecatedField");

        // Then:
        assertThat(inspector.getAnnotation(field, Test.class), is(nullValue()));
    }

    @Test
    public void shouldGetAnnotationOfFirstMethodIfPresent() throws Exception {
        // Given:
        final Method method1 = getClass().getDeclaredMethod("shouldGetAnnotationOfFirstMethodIfPresent");
        final Method method2 = getClass().getDeclaredMethod("setUp");
        final Test expected = method1.getAnnotation(Test.class);

        // Then:
        assertThat(inspector.getAnnotation(Test.class, method1, method2), is(expected));
    }

    @Test
    public void shouldGetAnnotationOfSecondMethodIfPresentButNotOnFirst() throws Exception {
        // Given:
        final Method method1 = getClass().getDeclaredMethod("setUp");
        final Method method2 = getClass().getDeclaredMethod("shouldGetAnnotationOfSecondMethodIfPresentButNotOnFirst");
        final Test expected = method2.getAnnotation(Test.class);

        // Then:
        assertThat(inspector.getAnnotation(Test.class, method1, method2), is(expected));
    }

    @Test
    public void shouldReturnNullIfNoMethodsHaveAnnotation() throws Exception {
        // Given:
        final Method method1 = getClass().getDeclaredMethod("setUp");
        final Method method2 = getClass().getDeclaredMethod("setUp");

        // Then:
        assertThat(inspector.getAnnotation(Test.class, method1, method2), is(nullValue()));
    }

    @Test
    public void shouldReturnNullOnEmptyArrayOfAccessors() throws Exception {
        assertThat(inspector.getAnnotation(Test.class), is(nullValue()));
    }
}