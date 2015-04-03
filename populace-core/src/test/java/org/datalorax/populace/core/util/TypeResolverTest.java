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

import org.datalorax.populace.core.walk.field.TypeTable;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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
        assertThat(resolver.resolve(String.class), is(equalTo((Type) String.class)));
    }

    @Test
    public void shouldReturnParameterisedClassWithTypeVariablesIfItCantResolveThem() throws Exception {
        assertThat(resolver.resolve(Map.class), is((Type) TypeUtils.parameterise(Map.class, Map.class.getTypeParameters())));
    }

    @Test
    public void shouldPartiallyResolveParameterisedClass() throws Exception {
        // Given:
        when(typeTable.resolveTypeVariable(Map.class.getTypeParameters()[0])).thenReturn(String.class);

        // Then:
        assertThat(resolver.resolve(Map.class), is((Type) TypeUtils.parameterise(Map.class, String.class, Map.class.getTypeParameters()[1])));
    }

    @Test
    public void shouldFullyResolveParameterisedClass() throws Exception {
        // Given:
        when(typeTable.resolveTypeVariable(Map.class.getTypeParameters()[0])).thenReturn(String.class);
        when(typeTable.resolveTypeVariable(Map.class.getTypeParameters()[1])).thenReturn(Integer.class);

        // Then:
        assertThat(resolver.resolve(Map.class), is((Type) TypeUtils.parameterise(Map.class, String.class, Integer.class)));
    }

    @Test
    public void shouldResolveUsingInheritedInterfaceGenericInfo() throws Exception {
        // Given:
        class SomeType<T> implements SomeInterface<T> {
        }

        when(typeTable.resolveTypeVariable(SomeInterface.class.getTypeParameters()[0])).thenReturn(Number.class);

        // Then:
        assertThat(resolver.resolve(SomeType.class), is(TypeUtils.parameterise(SomeType.class, Number.class)));
    }

    @Test
    public void shouldResolveUsingSuperClassGenericInfo() throws Exception {
        // Given:
        class SuperClass<ST> {
        }
        class SomeType<T> extends SuperClass<T> {
        }

        when(typeTable.resolveTypeVariable(SuperClass.class.getTypeParameters()[0])).thenReturn(Number.class);

        // Then:
        assertThat(resolver.resolve(SomeType.class), is(TypeUtils.parameterise(SomeType.class, Number.class)));
    }

    @Test
    public void shouldNotUseUnrelatedSuperInterfaceTypeVariables() throws Exception {
        // Given:
        class SomeType<T> implements SomeInterface<String> {
        }

        when(typeTable.resolveTypeVariable(SomeInterface.class.getTypeParameters()[0])).thenReturn(String.class);

        // Then:
        assertThat(resolver.resolve(SomeType.class), is(TypeUtils.parameterise(SomeType.class, SomeType.class.getTypeParameters())));
    }

    @Test
    public void shouldNotUseUnresolveSuperClassTypeVariables() throws Exception {
        // Given:
        class SuperClass<ST> {
        }
        class SomeType<T> extends SuperClass<String> {
        }

        when(typeTable.resolveTypeVariable(SuperClass.class.getTypeParameters()[0])).thenReturn(String.class);

        // Then:
        assertThat(resolver.resolve(SomeType.class), is(TypeUtils.parameterise(SomeType.class, SomeType.class.getTypeParameters())));
    }

    @Test
    public void shouldWorkWithBoundedTypeVariable() throws Exception {
        // Given:
        class SuperClass<ST extends Number> {
        }
        class SomeType<T extends Number> extends SuperClass<T> {
        }

        when(typeTable.resolveTypeVariable(SuperClass.class.getTypeParameters()[0])).thenReturn(Integer.class);

        // Then:
        assertThat(resolver.resolve(SomeType.class), is(TypeUtils.parameterise(SomeType.class, Integer.class)));
    }

    @Test(enabled = false)
    public void shouldWorkWithWildCards() throws Exception {
        // Given:
        class SomeType {
            Collection<?> field;
        }
        final Type typeWithWildcard = SomeType.class.getDeclaredField("field").getGenericType();

        // Then:
        assertThat(resolver.resolve(typeWithWildcard), is(TypeUtils.parameterise(SomeType.class, Integer.class)));
    }

    // Todo(ac): wildcards, generic arrays, etc.

    interface SomeInterface<E> {
    }
}