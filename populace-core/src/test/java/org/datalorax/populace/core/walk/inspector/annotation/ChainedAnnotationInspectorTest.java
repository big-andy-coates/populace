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

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChainedAnnotationInspectorTest {
    private final Field field;
    private final Test annotation1;
    private final Test annotation2;
    private final Method[] methods;
    private AnnotationInspector first;
    private AnnotationInspector second;
    private ChainedAnnotationInspector inspector;

    public ChainedAnnotationInspectorTest() throws Exception {
        this.field = getClass().getDeclaredField("field");
        this.annotation1 = getClass().getDeclaredMethod("dummyTest").getAnnotation(Test.class);
        this.annotation2 = getClass().getDeclaredMethod("shouldTestEqualsAndHashCode").getAnnotation(Test.class);
        this.methods = new Method[1];
    }

    @BeforeMethod
    public void setUp() throws Exception {
        first = mock(AnnotationInspector.class);
        second = mock(AnnotationInspector.class);

        inspector = new ChainedAnnotationInspector(first, second);
    }

    @Test
    public void shouldGetFieldAnnotationFromFirstInspectorIfPresent() throws Exception {
        // Given:
        when(first.getAnnotation(field, Test.class)).thenReturn(annotation1);
        when(second.getAnnotation(field, Test.class)).thenReturn(annotation2);

        // When:
        final Test annotation = inspector.getAnnotation(field, Test.class);

        // Then:
        assertThat(annotation, is(annotation1));
    }

    @Test
    public void shouldGetFieldAnnotationFromSecondInspectorIfFirstReturnsNull() throws Exception {
        // Given:
        when(first.getAnnotation(field, Test.class)).thenReturn(null);
        when(second.getAnnotation(field, Test.class)).thenReturn(annotation2);

        // When:
        final Test annotation = inspector.getAnnotation(field, Test.class);

        // Then:
        assertThat(annotation, is(annotation2));
    }

    @Test
    public void shouldGetMethodAnnotationFromFirstInspectorIfPresent() throws Exception {
        // Given:
        when(first.getAnnotation(Test.class, methods)).thenReturn(annotation1);
        when(second.getAnnotation(Test.class, methods)).thenReturn(annotation2);

        // When:
        final Test annotation = inspector.getAnnotation(Test.class, methods);

        // Then:
        assertThat(annotation, is(annotation1));
    }

    @Test
    public void shouldGetMethodAnnotationFromSecondInspectorIfFirstReturnsNull() throws Exception {
        // Given:
        when(first.getAnnotation(Test.class, methods)).thenReturn(null);
        when(second.getAnnotation(Test.class, methods)).thenReturn(annotation2);

        // When:
        final Test annotation = inspector.getAnnotation(Test.class, methods);

        // Then:
        assertThat(annotation, is(annotation2));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                new ChainedAnnotationInspector(first, second),
                new ChainedAnnotationInspector(first, second))
            .addEqualityGroup(
                new ChainedAnnotationInspector(second, first))
            .addEqualityGroup(
                new ChainedAnnotationInspector(mock(AnnotationInspector.class, "2"), second))
            .addEqualityGroup(
                new ChainedAnnotationInspector(first, mock(AnnotationInspector.class, "2")))
            .testEquals();
    }

    @Test
    public void shouldThrowNPEsOnConstructorParams() throws Exception {
        new NullPointerTester()
            .setDefault(AnnotationInspector.class, mock(AnnotationInspector.class))
            .testAllPublicConstructors(ChainedAnnotationInspector.class);
    }

    @Test(enabled = false)
    public void dummyTest() {

    }
}