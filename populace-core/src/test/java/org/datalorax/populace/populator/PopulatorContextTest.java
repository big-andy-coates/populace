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

package org.datalorax.populace.populator;

import org.datalorax.populace.populator.instance.InstanceFactories;
import org.datalorax.populace.populator.instance.InstanceFactory;
import org.datalorax.populace.populator.mutator.Mutators;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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

        context = new PopulatorContext(mutators, instanceFactories);
    }

    @Test
    public void shouldUseInstanceFactoriesToCreateInstance() throws Exception {
        // Given:
        givenInstanceFactoryInstalledFor(String.class, "expected");

        // When:
        final Object instance = context.createInstance(String.class, new Object());

        // Then:
        assertThat(instance, is("expected"));
    }

    @Test
    public void shouldPassParentObjectToInstanceFactoryOnCreateInstance() throws Exception {
        // Given:
        final Object parent = new Object();
        final InstanceFactory factory = givenInstanceFactoryInstalledFor(String.class, "expected");

        // When:
        context.createInstance(String.class, parent);

        // Then:
        verify(factory).createInstance(any(Class.class), eq(parent), any(InstanceFactories.class));
    }

    @Test
    public void shouldPassInstanceFactoriesToInstanceFactoryOnCreateInstance() throws Exception {
        // Given:
        final Object parent = new Object();
        final InstanceFactory factory = givenInstanceFactoryInstalledFor(String.class, "expected");

        // When:
        context.createInstance(String.class, parent);

        // Then:
        verify(factory).createInstance(any(Class.class), anyObject(), isA(InstanceFactories.class));
    }


    private <T> InstanceFactory givenInstanceFactoryInstalledFor(final Class<T> someType, final T instance) {
        final InstanceFactory factory = mock(InstanceFactory.class);
        when(instanceFactories.get(someType)).thenReturn(factory);
        when(factory.createInstance(eq(someType), anyObject(), any(InstanceFactories.class))).thenReturn(instance);
        return factory;
    }

    // Todo(ac): test
}