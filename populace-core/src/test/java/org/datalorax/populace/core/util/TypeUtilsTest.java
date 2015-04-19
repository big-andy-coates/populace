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

package org.datalorax.populace.core.util;

import org.testng.annotations.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TypeUtilsTest {
    @Test
    public void shouldResolveTypeVariableOfRawClassToObject() throws Exception {
        // When:
        final Type resolvedType = TypeUtils.getTypeArgument(List.class, List.class, List.class.getTypeParameters()[0]);

        // Then:
        assertThat(resolvedType, is(equalTo(Object.class)));
    }

    @Test
    public void shouldResolveTypeVariableOfDerivedRawClassToObject() throws Exception {
        // When:
        final Type resolvedType = TypeUtils.getTypeArgument(ArrayList.class, List.class, List.class.getTypeParameters()[0]);

        // Then:
        assertThat(resolvedType, is(equalTo(Object.class)));
    }

    @Test
    public void shouldResolveTypeVariableOfParameterisedClassToCorrectType() throws Exception {
        // Given:
        final Type type = TypeUtils.parameterise(Map.class, String.class, Integer.class);

        // When:
        final Type resolvedType = TypeUtils.getTypeArgument(type, Map.class, Map.class.getTypeParameters()[1]);

        // Then:
        assertThat(resolvedType, is(equalTo(Integer.class)));
    }

    @Test
    public void shouldReturnParameterisedTypes() throws Exception {
        // Given:
        final Type valueType = TypeUtils.parameterise(List.class, Double.class);
        final Type type = TypeUtils.parameterise(Map.class, String.class, valueType);

        // When:
        final Type resolvedType = TypeUtils.getTypeArgument(type, Map.class, Map.class.getTypeParameters()[1]);

        // Then:
        assertThat(resolvedType, is(equalTo(valueType)));
    }

    @Test
    public void shouldAbbreviateClassName() throws Exception {
        assertThat(TypeUtils.abbreviatedName(getClass()), is("o.d.p.c.u.TypeUtilsTest"));
    }

    @Test
    public void shouldAbbreviateParameterisedTypeName() throws Exception {
        // Given:
        final ParameterizedType pt = TypeUtils.parameterise(Collection.class, String.class);

        // Then:
        assertThat(TypeUtils.abbreviatedName(pt), is("j.u.Collection<j.l.String>"));
    }
}