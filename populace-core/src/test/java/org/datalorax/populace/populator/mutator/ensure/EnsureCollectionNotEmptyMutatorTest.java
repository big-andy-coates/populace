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

package org.datalorax.populace.populator.mutator.ensure;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;
import org.datalorax.populace.populator.mutator.PassThroughMutator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class EnsureCollectionNotEmptyMutatorTest {
    private Mutator mutator;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorContext.class);

        mutator = new EnsureCollectionNotEmptyMutator();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIfTypeNotACollection() throws Exception {
        // When:
        mutator.mutate(String.class, null, null, config);
    }

    @Test
    public void shouldDoNothingToNullCollection() throws Exception {
        // When:
        final Object mutated = mutator.mutate(List.class, null, null, config);

        // Then:
        assertThat(mutated, is(nullValue()));
    }

    @Test
    public void shouldDoNothingToNonEmptyCollection() throws Exception {
        // Given:
        final List list = mock(List.class);

        // When:
        final Object mutated = mutator.mutate(List.class, list, null, config);

        // Then:
        assertThat(mutated, is(sameInstance(list)));
        verify(list).isEmpty();
        verifyNoMoreInteractions(list);
    }

    @Test
    public void shouldNotBlowUpOnRawBaseType() throws Exception {
        // Given:
        givenMutatorRegistered(Object.class, PassThroughMutator.INSTANCE);
        final List currentValue = new ArrayList();

        // When:
        mutator.mutate(List.class, currentValue, null, config);
    }

    @Test
    public void shouldNotBlowUpOnRawDerivedTypes() throws Exception {
        // Given:
        givenMutatorRegistered(Object.class, PassThroughMutator.INSTANCE);
        final List currentValue = new ArrayList();

        // When:
        mutator.mutate(ArrayList.class, currentValue, null, config);
    }

    // Todo(ac): how about some tests? including Collection<Collection<>> style

    private void givenMutatorRegistered(Class<?> type, Mutator mutator) {
        when(config.getMutator(type)).thenReturn(mutator);
    }

    private <T> void givenCreateInstanceWillReturn(final Class<T> type, final T instance) {
        when(config.createInstance(type, null)).thenReturn(instance);
    }
}