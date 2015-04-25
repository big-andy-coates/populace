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

package org.datalorax.populace.core.walk.element;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;
import org.datalorax.populace.core.util.TypeResolver;
import org.datalorax.populace.core.walk.field.PathProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ElementInfoTest {
    private RawElement element;
    private TypeResolver typeResolver;
    private PathProvider pathProvider;
    private ElementInfo elementInfo;

    @BeforeMethod
    public void setUp() throws Exception {
        element = mock(RawElement.class);
        typeResolver = mock(TypeResolver.class);
        pathProvider = mock(PathProvider.class);

        elementInfo = new ElementInfo(element, List.class, typeResolver, pathProvider);
    }

    @Test
    public void shouldGetGenericTypeUsingTypeResolver() throws Exception {
        // Given:
        when(typeResolver.resolve(any(Type.class))).thenReturn(String.class);

        // When:
        final Type genericType = elementInfo.getGenericType();

        // Then:
        assertThat(genericType, is(equalTo(String.class)));
    }

    @Test
    public void shouldGetGenericTypeUsingRawElement() throws Exception {
        // Given:
        when(element.getGenericType(List.class)).thenReturn(String.class);

        // When:
        elementInfo.getGenericType();

        // Then:
        verify(typeResolver).resolve(String.class);
    }

    @Test
    public void shouldGetValue() throws Exception {
        // Given:
        final Object expected = new Object();
        when(element.getValue()).thenReturn(expected);

        // When:
        final Object actual = elementInfo.getValue();

        // Then:
        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSetValue() throws Exception {
        // Given:
        final Object newValue = new Object();

        // When:
        elementInfo.setValue(newValue);

        // Then:
        verify(element).setValue(newValue);
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                new ElementInfo(element, List.class, typeResolver, pathProvider),
                new ElementInfo(element, List.class, typeResolver, pathProvider))
            .addEqualityGroup(
                new ElementInfo(mock(RawElement.class, "different"), List.class, typeResolver, pathProvider))
            .addEqualityGroup(
                new ElementInfo(element, Set.class, mock(TypeResolver.class, "different"), pathProvider))
            .addEqualityGroup(
                new ElementInfo(element, List.class, typeResolver, mock(PathProvider.class, "different")))
            .testEquals();
    }

    @Test
    public void shouldThrowNPEsOnConstructorParams() throws Exception {
        new NullPointerTester()
            .setDefault(TypeResolver.class, typeResolver)
            .setDefault(PathProvider.class, pathProvider)
            .testAllPublicConstructors(ElementInfo.class);
    }
}