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

import org.datalorax.populace.core.CustomCollection;
import org.datalorax.populace.core.walk.field.TypeTable;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.datalorax.populace.core.util.TypeMatchers.typeEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("UnusedDeclaration")
public class TypeResolverTest {
    private TypeResolver resolver;
    private TypeTable typeTable;

    @BeforeMethod
    public void setUp() throws Exception {
        typeTable = mock(TypeTable.class);
        resolver = new TypeResolver(typeTable);

        // By default, just type table can't resolve anything:
        when(typeTable.resolveTypeVariable(any(TypeVariable.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowOnNullArg() throws Exception {
        new TypeResolver(null);
    }

    @Test
    public void shouldReturnRawUnparameterisedClassUntouched() throws Exception {
        assertThat(resolver.resolve(String.class), is(typeEqualTo(String.class)));
    }

    @Test
    public void shouldReturnParameterisedClassWithTypeVariablesIfItCantResolveThem() throws Exception {
        assertThat(resolver.resolve(Map.class), is(typeEqualTo(TypeUtils.parameterise(Map.class, Map.class.getTypeParameters()))));
    }

    @Test
    public void shouldPartiallyResolveParameterisedClass() throws Exception {
        // Given:
        when(typeTable.resolveTypeVariable(Map.class.getTypeParameters()[0])).thenReturn(String.class);

        // Then:
        assertThat(resolver.resolve(Map.class), is(typeEqualTo(TypeUtils.parameterise(Map.class, String.class, Map.class.getTypeParameters()[1]))));
    }

    @Test
    public void shouldFullyResolveParameterisedClass() throws Exception {
        // Given:
        when(typeTable.resolveTypeVariable(Map.class.getTypeParameters()[0])).thenReturn(String.class);
        when(typeTable.resolveTypeVariable(Map.class.getTypeParameters()[1])).thenReturn(Integer.class);

        // Then:
        assertThat(resolver.resolve(Map.class), is(typeEqualTo(TypeUtils.parameterise(Map.class, String.class, Integer.class))));
    }

    @Test
    public void shouldResolveUsingInheritedInterfaceGenericInfo() throws Exception {
        // Given:
        class SomeType<T> implements SomeInterface<T> {
        }

        when(typeTable.resolveTypeVariable(SomeInterface.class.getTypeParameters()[0])).thenReturn(Number.class);

        // Then:
        assertThat(resolver.resolve(SomeType.class), is(typeEqualTo(TypeUtils.parameterise(SomeType.class, Number.class))));
    }

    @Test
    public void shouldResolveUsingSuperTypes() throws Exception {
        // Given:
        class TypeWithTypeVariables<TypeElement> {
            // collection knows how to map ET -> TypeElement
            public Collection<TypeElement> collection = new CustomCollection<>();
        }

        final TypeVariable<Class<TypeWithTypeVariables>> TypeElement = TypeWithTypeVariables.class.getTypeParameters()[0];
        final TypeVariable<Class<Collection>> E = Collection.class.getTypeParameters()[0];
        when(typeTable.resolveTypeVariable(E)).thenReturn(TypeElement);
        when(typeTable.resolveTypeVariable(TypeElement)).thenReturn(String.class);

        // Then:
        assertThat(resolver.resolve(CustomCollection.class), is(typeEqualTo(TypeUtils.parameterise(CustomCollection.class, String.class))));
    }

    @Test
    public void shouldNotUseUnrelatedSuperInterfaceTypeVariables() throws Exception {
        // Given:
        class SomeType<T> implements SomeInterface<String> {
        }

        when(typeTable.resolveTypeVariable(SomeInterface.class.getTypeParameters()[0])).thenReturn(String.class);

        // Then:
        assertThat(resolver.resolve(SomeType.class), is(typeEqualTo(TypeUtils.parameterise(SomeType.class, SomeType.class.getTypeParameters()))));
    }

    @Test
    public void shouldNotUseResolveSuperClassTypeVariables() throws Exception {
        // Given:
        class SuperClass<ST> {
        }
        class SomeType<T> extends SuperClass<String> {
        }

        when(typeTable.resolveTypeVariable(SuperClass.class.getTypeParameters()[0])).thenReturn(String.class);

        // Then:
        assertThat(resolver.resolve(SomeType.class), is(typeEqualTo(TypeUtils.parameterise(SomeType.class, SomeType.class.getTypeParameters()))));
    }

    @Test
    public void shouldWorkWithBoundedTypeVariable() throws Exception {
        // Given:
        class SuperClass<ST extends Number> {
        }
        class SomeType<T extends Number> extends SuperClass<T> {
        }

        final TypeVariable<Class<SuperClass>> ST = SuperClass.class.getTypeParameters()[0];
        when(typeTable.resolveTypeVariable(ST)).thenReturn(Integer.class);

        // Then:
        assertThat(resolver.resolve(SomeType.class), is(typeEqualTo(TypeUtils.parameterise(SomeType.class, Integer.class))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldWorkWithWildCards() throws Exception {
        // Given:
        class SomeType {
            Collection<?> field;
        }
        final Type typeWithWildcard = SomeType.class.getDeclaredField("field").getGenericType();
        final Type expectedComponentType = TypeUtils.wildcardType();

        // When:
        final Type resolved = resolver.resolve(typeWithWildcard);

        // Then:
        assertThat(resolved, is(equalTo(TypeUtils.parameterise(Collection.class, expectedComponentType))));
        // commons.lang bug workaround:
        assertThat(resolved, is(instanceOf(ParameterizedType.class)));
        assertThat(((ParameterizedType) resolved).getActualTypeArguments(), is(array(typeEqualTo(expectedComponentType))));
        // end
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldResolveWildcardLowerBounds() throws Exception {
        // Given:
        class SomeType<LB> {
            Collection<? super Set<LB>> field;
        }
        final Type typeWithWildcard = SomeType.class.getDeclaredField("field").getGenericType();
        final TypeVariable<Class<SomeType>> LB = SomeType.class.getTypeParameters()[0];
        when(typeTable.resolveTypeVariable(LB)).thenReturn(String.class);
        final ParameterizedType expectedSetType = TypeUtils.parameterise(Set.class, String.class);
        final WildcardType expectedComponentType = TypeUtils.wildcardTypeWithLowerBounds(expectedSetType);

        // When:
        final Type resolved = resolver.resolve(typeWithWildcard);

        // Then:
        assertThat(resolved, is(equalTo(TypeUtils.parameterise(Collection.class, expectedComponentType))));
        // commons.lang bug workaround:
        assertThat(resolved, is(instanceOf(ParameterizedType.class)));
        final Type argType = ((ParameterizedType) resolved).getActualTypeArguments()[0];
        assertThat(argType, is(typeEqualTo(expectedComponentType)));
        assertThat(((WildcardType) argType).getLowerBounds(), is(array(typeEqualTo(expectedSetType))));
        // end
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldResolveWildcardsUpperBounds() throws Exception {
        // Given:
        class SomeType<UB> {
            Collection<? extends Set<UB>> field;
        }
        final Type typeWithWildcard = SomeType.class.getDeclaredField("field").getGenericType();
        final TypeVariable<Class<SomeType>> UB = SomeType.class.getTypeParameters()[0];
        when(typeTable.resolveTypeVariable(UB)).thenReturn(Integer.class);
        final ParameterizedType expectedSetType = TypeUtils.parameterise(Set.class, Integer.class);
        final WildcardType expectedComponentType = TypeUtils.wildcardTypeWithUpperBounds(expectedSetType);

        // When:
        final Type resolved = resolver.resolve(typeWithWildcard);

        // Then:
        assertThat(resolver.resolve(typeWithWildcard), is(equalTo(TypeUtils.parameterise(Collection.class, expectedComponentType))));
        // commons.lang bug workaround:
        assertThat(resolved, is(instanceOf(ParameterizedType.class)));
        final Type argType = ((ParameterizedType) resolved).getActualTypeArguments()[0];
        assertThat(argType, is(typeEqualTo(expectedComponentType)));
        assertThat(((WildcardType) argType).getUpperBounds(), is(array(typeEqualTo(expectedSetType))));
        // end
    }

    @Test
    public void shouldNotCreateNewWildcardIfLowerBoundNotResolved() throws Exception {
        // Given:
        class SomeType {
            Collection<? super Number> field;
        }
        final Type typeWithWildcard = SomeType.class.getDeclaredField("field").getGenericType();
        final Type wildcardType = ((ParameterizedType) typeWithWildcard).getActualTypeArguments()[0];

        // When:
        final Type resolved = resolver.resolve(typeWithWildcard);

        // Then:
        assertThat(((ParameterizedType) resolved).getActualTypeArguments()[0], is(sameInstance(wildcardType)));
    }

    @Test
    public void shouldNotCreateNewWildcardIfUpperBoundNotResolved() throws Exception {
        // Given:
        class SomeType {
            Collection<? extends Number> field;
        }
        final Type typeWithWildcard = SomeType.class.getDeclaredField("field").getGenericType();
        final Type wildcardType = ((ParameterizedType) typeWithWildcard).getActualTypeArguments()[0];

        // When:
        final Type resolved = resolver.resolve(typeWithWildcard);

        // Then:
        assertThat(((ParameterizedType) resolved).getActualTypeArguments()[0], is(sameInstance(wildcardType)));
    }

    @Test
    public void shouldResolveGenericArrayComponentTypeThatIsTypeVariable() throws Exception {
        // Given:
        class SomeType<ST> {
            @SafeVarargs
            final void someMethod(ST... sts) {
            }
        }

        final Type genericArrayType = SomeType.class.getDeclaredMethod("someMethod", Object[].class).getGenericParameterTypes()[0];
        final TypeVariable<Class<SomeType>> ST = SomeType.class.getTypeParameters()[0];
        when(typeTable.resolveTypeVariable(ST)).thenReturn(Integer.class);

        // When:
        final Type resolved = resolver.resolve(genericArrayType);

        // Then:
        assertThat(resolver.resolve(genericArrayType), is(equalTo(TypeUtils.genericArrayType(Integer.class))));
    }

    @Test
    public void shouldResolveGenericArrayComponentTypeThatIsParameterisedType() throws Exception {
        // Given:
        class SomeType<ST> {
            @SafeVarargs
            final void someMethod(List<ST>... lists) {
            }
        }

        final Type genericArrayType = SomeType.class.getDeclaredMethod("someMethod", List[].class).getGenericParameterTypes()[0];
        final TypeVariable<Class<SomeType>> ST = SomeType.class.getTypeParameters()[0];
        when(typeTable.resolveTypeVariable(ST)).thenReturn(Integer.class);
        final ParameterizedType expectedListType = TypeUtils.parameterise(List.class, Integer.class);

        // When:
        final Type resolved = resolver.resolve(genericArrayType);

        // Then:
        assertThat(resolver.resolve(genericArrayType), is(equalTo(TypeUtils.genericArrayType(expectedListType))));
    }

    @Test
    public void shouldNotCreateNewGenericArrayTypeIfComponentTypeNotResolved() throws Exception {
        // Given:
        class SomeType<ST> {
            @SafeVarargs
            final void someMethod(ST... sts) {
            }
        }

        final Type genericArrayType = SomeType.class.getDeclaredMethod("someMethod", Object[].class).getGenericParameterTypes()[0];

        // When:
        final Type resolved = resolver.resolve(genericArrayType);

        // Then:
        assertThat(resolved, is(sameInstance(genericArrayType)));
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void shouldThrowOnUnsupportedType() throws Exception {
        resolver.resolve(mock(Type.class));
    }

    @Test
    public void shouldNotNeedToWorkAroundApacheCommonsLangTypeUtilsBug() throws Exception {
        // Given:
        class SomeType {
            Collection<?> field;
        }

        final ParameterizedType wildcardCollectionType = (ParameterizedType) SomeType.class.getDeclaredField("field").getGenericType();
        final WildcardType wildcard = (WildcardType) wildcardCollectionType.getActualTypeArguments()[0];
        final ParameterizedType ptWithWildcard = org.apache.commons.lang3.reflect.TypeUtils.parameterize(Collection.class, wildcard);
        final ParameterizedType otherPt = org.apache.commons.lang3.reflect.TypeUtils.parameterize(Collection.class, String.class);

        // Then:
        assertThat(otherPt, is(not(equalTo(ptWithWildcard))));  // Passes
        assertThat(ptWithWildcard, is(equalTo(otherPt)));       // should fail!

        // NB: If this test starts failing at the line above, then the bug in commons.lang has been fixed.
        // So, remove this test and all the workarounds marked with 'commons.lang bug workaround'.
    }

    interface SomeInterface<E> {
    }
}