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

package org.datalorax.populace.core.populate;

import org.datalorax.populace.core.populate.instance.InstanceFactories;
import org.datalorax.populace.core.populate.instance.InstanceFactory;
import org.datalorax.populace.core.populate.mutator.Mutators;
import org.datalorax.populace.core.util.TypeUtils;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class PopulatorContextTest {
    @Mock
    private Mutators mutators;
    @Mock
    private InstanceFactories instanceFactories;
    private PopulatorContext context;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(instanceFactories.getSpecific(any(Type.class))).thenReturn(Optional.empty());

        context = new PopulatorContext(mutators, instanceFactories);
    }

    @Test
    public void shouldUseInstanceFactoriesToCreateInstance() throws Exception {
        // Given:
        givenInstanceFactoryInstalled("expected");

        // When:
        final Object instance = context.createInstance(String.class, new Object());

        // Then:
        assertThat(instance, is("expected"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetInstanceFactoryUsingRequestedType() throws Exception {
        // Given:
        final InstanceFactory factory = givenInstanceFactoryInstalledFor(String.class);

        // When:
        context.createInstance(String.class, new Object());

        // Then:
        verify(factory).createInstance(any(Class.class), anyObject(), any(InstanceFactories.class));
    }

    @Test
    public void shouldPassRawTypeToInstanceFactoryOnCreateInstance() throws Exception {
        // Given:
        final Type type = TypeUtils.parameterise(Map.class, String.class, Integer.class);
        final InstanceFactory factory = givenInstanceFactoryInstalled();

        // When:
        context.createInstance(type, null);

        // Then:
        verify(factory).createInstance(eq(Map.class), anyObject(), any(InstanceFactories.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPassParentObjectToInstanceFactoryOnCreateInstance() throws Exception {
        // Given:
        final Object parent = new Object();
        final InstanceFactory factory = givenInstanceFactoryInstalled();

        // When:
        context.createInstance(String.class, parent);

        // Then:
        verify(factory).createInstance(any(Class.class), eq(parent), any(InstanceFactories.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPassInstanceFactoriesToInstanceFactoryOnCreateInstance() throws Exception {
        // Given:
        final Object parent = new Object();
        final InstanceFactory factory = givenInstanceFactoryInstalled();

        // When:
        context.createInstance(String.class, parent);

        // Then:
        verify(factory).createInstance(any(Class.class), anyObject(), isA(InstanceFactories.class));
    }

    @Test
    public void shouldGetInstanceFactoryUsingClass() throws Exception {
        // Given:
        givenInstanceFactoryInstalled();

        // When:
        context.createInstance(String.class, null);

        // Then:
        verify(instanceFactories).get(String.class);
    }

    @Test
    public void shouldGetInstanceFactoryUsingParameterizedType() throws Exception {
        // Given:
        final Type pt = TypeUtils.parameterise(Collection.class, Number.class);
        givenInstanceFactoryInstalled();

        // When:
        context.createInstance(pt, null);

        // Then:
        verify(instanceFactories).get(pt);
    }

    @Test
    public void shouldGetObjectInstanceFactoryForTypeVariables() throws Exception {
        // Given:
        final Type typeVar = mock(TypeVariable.class);
        givenInstanceFactoryInstalled();

        // When:
        context.createInstance(typeVar, null);

        // Then:
        verify(instanceFactories).get(Object.class);
    }

    @Test
    public void shouldGetSpecificInstanceFactoryForWildcardsWithSingleUpperBounds() throws Exception {
        // Given:
        final Type wildcard = TypeUtils.wildcardTypeWithUpperBounds(Integer.class);
        givenInstanceFactoryInstalled();

        // When:
        context.createInstance(wildcard, null);

        // Then:
        verify(instanceFactories).getSpecific(wildcard);
    }

    @Test
    public void shouldNotGetSpecificInstanceFactoryForWildcardsWithMultipleUpperBounds() throws Exception {
        // Given:
        final Type wildcard = TypeUtils.wildcardTypeWithUpperBounds(Integer.class, Number.class);
        givenInstanceFactoryInstalled();

        // When:
        context.createInstance(wildcard, null);

        // Then:
        verify(instanceFactories, never()).getSpecific(any(Type.class));
    }

    @Test
    public void shouldNotGetSpecificInstanceFactoryForWildcardsWithNoUpperBounds() throws Exception {
        // Given:
        final WildcardType wildcard = mock(WildcardType.class);
        when(wildcard.getUpperBounds()).thenReturn(new Type[]{});
        givenInstanceFactoryInstalled();

        // When:
        context.createInstance(wildcard, null);

        // Then:
        verify(instanceFactories, never()).getSpecific(any(Type.class));
    }

    @Test
    public void shouldGetInstanceFactoryUsingWildcardUpperBoundIfNoSpecificFactoryRegistered() throws Exception {
        // Given:
        final Type wildcard = TypeUtils.wildcardTypeWithUpperBounds(Integer.class);
        givenInstanceFactoryInstalled();

        // When:
        context.createInstance(wildcard, null);

        // Then:
        verify(instanceFactories).get(Integer.class);
    }

    @Test
    public void shouldNotGetInstanceFactoryForWildcardUpperBoundIfSpecificFactoryRegistered() throws Exception {
        // Given:
        final Type wildcard = TypeUtils.wildcardTypeWithUpperBounds(Integer.class);
        givenSpecificInstanceFactoryInstalledFor(wildcard);

        // When:
        context.createInstance(wildcard, null);

        // Then:
        verify(instanceFactories, never()).get(wildcard);
    }

    @Test
    public void shouldGetObjectInstanceFactoryForWildcardsWithNoUpperBound() throws Exception {
        // Given:
        final WildcardType wildcard = mock(WildcardType.class);
        when(wildcard.getUpperBounds()).thenReturn(new Type[]{});
        givenInstanceFactoryInstalled();

        // When:
        context.createInstance(wildcard, null);

        // Then:
        verify(instanceFactories).get(Object.class);
    }

    @Test
    public void shouldResolveWildcardTypeWithNoUpperBoundsToObjectOnCreateInstance() throws Exception {
        // Given:
        final Type wildcard = TypeUtils.wildcardTypeWithLowerBounds(Integer.class);
        final InstanceFactory factory = givenInstanceFactoryInstalled();

        // When:
        context.createInstance(wildcard, null);

        // Then:
        verify(factory).createInstance(eq(Object.class), anyObject(), any(InstanceFactories.class));
    }

    @Test
    public void shouldResolveWildcardTypesToUpperBoundOnCreateInstance() throws Exception {
        // Given:
        final Type wildcard = TypeUtils.wildcardTypeWithUpperBounds(Integer.class);
        final InstanceFactory factory = givenSpecificInstanceFactoryInstalledFor(wildcard);

        // When:
        context.createInstance(wildcard, null);

        // Then:
        verify(factory).createInstance(eq(Integer.class), anyObject(), any(InstanceFactories.class));
    }

    @Test
    public void shouldResolveTypeVariablesToObjectOnCreateInstance() throws Exception {
        // Given:
        final Type typeVariable = Collection.class.getTypeParameters()[0];
        final InstanceFactory factory = givenInstanceFactoryInstalled();

        // When:
        context.createInstance(typeVariable, null);

        // Then:
        verify(factory).createInstance(eq(Object.class), anyObject(), any(InstanceFactories.class));
    }

    private InstanceFactory givenInstanceFactoryInstalled() {
        final InstanceFactory factory = mock(InstanceFactory.class);
        when(instanceFactories.get(any(Type.class))).thenReturn(factory);
        return factory;
    }

    @SuppressWarnings("unchecked")
    private InstanceFactory givenInstanceFactoryInstalled(final Object newInstance) {
        final InstanceFactory factory = mock(InstanceFactory.class);
        when(instanceFactories.get(any(Type.class))).thenReturn(factory);
        when(factory.createInstance(any(Class.class), anyObject(), any(InstanceFactories.class))).thenReturn(newInstance);
        return factory;
    }

    private InstanceFactory givenInstanceFactoryInstalledFor(final Type someType) {
        final InstanceFactory factory = mock(InstanceFactory.class);
        when(instanceFactories.get(someType)).thenReturn(factory);
        return factory;
    }

    private InstanceFactory givenSpecificInstanceFactoryInstalledFor(final Type type) {
        final InstanceFactory factory = mock(InstanceFactory.class);
        when(instanceFactories.getSpecific(type)).thenReturn(Optional.of(factory));
        return factory;
    }
}