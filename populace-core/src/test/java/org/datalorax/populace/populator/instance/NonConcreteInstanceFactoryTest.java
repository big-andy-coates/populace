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

package org.datalorax.populace.populator.instance;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class NonConcreteInstanceFactoryTest {
    private final Class<List> baseType = List.class;
    private final Class<ArrayList> defaultType = ArrayList.class;
    private InstanceFactory concreteFactory;
    private NonConcreteInstanceFactory factory;
    private InstanceFactories instanceFactories;
    private Object parent;

    @BeforeMethod
    public void setUp() throws Exception {
        instanceFactories = mock(InstanceFactories.class);
        concreteFactory = mock(InstanceFactory.class);
        parent = mock(Object.class);

        factory = new NonConcreteInstanceFactory(baseType, defaultType, concreteFactory);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowIfNullBaseType() throws Exception {
        new NonConcreteInstanceFactory(null, defaultType, concreteFactory);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowIfNullDefaultType() throws Exception {
        new NonConcreteInstanceFactory(baseType, null, concreteFactory);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowIfNullConcreteFactory() throws Exception {
        new NonConcreteInstanceFactory(baseType, defaultType, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIfRequestedTypeNotSubtypeOfDefaultType() throws Exception {
        // When:
        factory.createInstance(String.class, null, instanceFactories);
    }

    @Test
    public void shouldDelegateToConcreteFactoryForConcreteTypes() throws Exception {
        // When:
        factory.createInstance(Vector.class, parent, instanceFactories);

        // Then:
        verify(concreteFactory).createInstance(Vector.class, parent, instanceFactories);
    }

    @Test
    public void shouldUseConcreteFactoryToCreateDefaultType() throws Exception {
        // When:
        factory.createInstance(List.class, parent, instanceFactories);

        // Then:
        verify(concreteFactory).createInstance(defaultType, parent, instanceFactories);
    }

    @Test
    public void shouldReturnInstanceFromConcreteFactoryForConcreteTypes() throws Exception {
        // Given:
        final Vector expected = mock(Vector.class);
        when(concreteFactory.createInstance(Vector.class, parent, instanceFactories)).thenReturn(expected);

        // When:
        final Vector instance = factory.createInstance(Vector.class, parent, instanceFactories);

        // Then:
        assertThat(instance, is(expected));
    }

    @Test
    public void shouldReturnInstanceFromConcreteFactoryForNonConcreteTypes() throws Exception {
        // Given:
        final ArrayList expected = mock(ArrayList.class);
        when(concreteFactory.createInstance(ArrayList.class, parent, instanceFactories)).thenReturn(expected);

        // When:
        final List instance = factory.createInstance(List.class, parent, instanceFactories);

        // Then:
        assertThat(instance, is(expected));
    }
}