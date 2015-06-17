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
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

public class TypeUtilsTest {
    @Test
    public void shouldGetTypeVariableOfRawClass() throws Exception {
        // When:
        final Type resolvedType = TypeUtils.getTypeArgument(List.class, List.class.getTypeParameters()[0]);

        // Then:
        assertThat(resolvedType, is(equalTo(List.class.getTypeParameters()[0])));
    }

    @Test
    public void shouldGetTypeVariableOfDerivedRawClass() throws Exception {
        // When:
        final Type resolvedType = TypeUtils.getTypeArgument(ArrayList.class, List.class.getTypeParameters()[0]);

        // Then:
        assertThat(resolvedType, is(equalTo(ArrayList.class.getTypeParameters()[0])));
    }

    @Test
    public void shouldGetTypeVariableOfParameterisedClass() throws Exception {
        // Given:
        final Type type = TypeUtils.parameterise(Map.class, String.class, Integer.class);

        // When:
        final Type resolvedType = TypeUtils.getTypeArgument(type, Map.class.getTypeParameters()[1]);

        // Then:
        assertThat(resolvedType, is(equalTo(Integer.class)));
    }

    @Test
    public void shouldGetParameterisedTypeVariables() throws Exception {
        // Given:
        final Type valueType = TypeUtils.parameterise(List.class, Double.class);
        final Type type = TypeUtils.parameterise(Map.class, String.class, valueType);

        // When:
        final Type resolvedType = TypeUtils.getTypeArgument(type, Map.class.getTypeParameters()[1]);

        // Then:
        assertThat(resolvedType, is(equalTo(valueType)));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIfTypesNotAssignable() throws Exception {
        TypeUtils.getTypeArgument(String.class, Map.class.getTypeParameters()[0]);
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

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowFromAbbreviateNameIfUnsupportedType() throws Exception {
        // Given:
        final Type unsupported = mock(TypeVariable.class);

        // When:
        TypeUtils.abbreviatedName(unsupported);
    }

    @Test
    public void shouldGetBoxedTypeForPrimitive() throws Exception {
        assertThat(TypeUtils.getBoxedTypeForPrimitive(boolean.class), is(equalTo(Boolean.class)));
        assertThat(TypeUtils.getBoxedTypeForPrimitive(byte.class), is(equalTo(Byte.class)));
        assertThat(TypeUtils.getBoxedTypeForPrimitive(char.class), is(equalTo(Character.class)));
        assertThat(TypeUtils.getBoxedTypeForPrimitive(short.class), is(equalTo(Short.class)));
        assertThat(TypeUtils.getBoxedTypeForPrimitive(int.class), is(equalTo(Integer.class)));
        assertThat(TypeUtils.getBoxedTypeForPrimitive(long.class), is(equalTo(Long.class)));
        assertThat(TypeUtils.getBoxedTypeForPrimitive(float.class), is(equalTo(Float.class)));
        assertThat(TypeUtils.getBoxedTypeForPrimitive(double.class), is(equalTo(Double.class)));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowFromEnsureConsistentTypeForUnsupportedType() throws Exception {
        TypeUtils.ensureConsistentType(mock(Type.class));
    }

    @Test
    public void shouldCreateWildcardTypeWithNoLowerBounds() throws Exception {
        // When:
        final WildcardType wildcardType = TypeUtils.wildcardTypeWithUpperBounds(String.class);

        // Then:
        assertThat(wildcardType.getLowerBounds().length, is(0));
    }

    @Test
    public void shouldCreateWildcardTypeWithSuppliedUpperBounds() throws Exception {
        // When:
        final WildcardType wildcardType = TypeUtils.wildcardTypeWithUpperBounds(String.class, Integer.class);

        // Then:
        assertThat(wildcardType.getUpperBounds().length, is(2));
        assertThat(wildcardType.getUpperBounds()[0], is(equalTo(String.class)));
        assertThat(wildcardType.getUpperBounds()[1], is(equalTo(Integer.class)));
    }

    @Test
    public void shouldDefaultToWildcardTypeWithObjectUpperBounds() throws Exception {
        // When:
        final WildcardType wildcardType = TypeUtils.wildcardTypeWithUpperBounds();

        // Then:
        assertThat(wildcardType.getUpperBounds().length, is(1));
        assertThat(wildcardType.getUpperBounds()[0], is(equalTo(Object.class)));
    }

    @Test
    public void shouldBeRelatedIfSameType() throws Exception {
        assertThat(TypeUtils.areRelatedTypes(String.class, String.class), is(true));
    }

    @Test
    public void shouldBeRelatedIfOneIsSubTypeOfOther() throws Exception {
        assertThat(TypeUtils.areRelatedTypes(Number.class, Integer.class), is(true));
        assertThat(TypeUtils.areRelatedTypes(Integer.class, Number.class), is(true));
    }

    @Test
    public void shouldNotBeRelatedIfNotSubTypes() throws Exception {
        assertThat(TypeUtils.areRelatedTypes(String.class, List.class), is(false));
    }

    @Test
    public void shouldReturnMostDerivedType() throws Exception {
        // Given:
        class SomeType {
        }
        class SomeDerivedType extends SomeType {
        }

        // Then:
        assertThat(TypeUtils.getMostDerivedClass(SomeType.class, SomeDerivedType.class), is(equalTo(SomeDerivedType.class)));
        assertThat(TypeUtils.getMostDerivedClass(SomeDerivedType.class, SomeType.class), is(equalTo(SomeDerivedType.class)));
        assertThat(TypeUtils.getMostDerivedClass(SomeType.class, SomeType.class), is(equalTo(SomeType.class)));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIfTypesAreUnrelated() throws Exception {
        // Given:
        class SomeType {
        }
        class SomeUnrealtedType {
        }

        // Then:
        TypeUtils.getMostDerivedClass(SomeType.class, SomeUnrealtedType.class);
    }
}