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

package org.datalorax.populace.populator.mutator.change;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;
import org.datalorax.populace.populator.mutator.PassThroughMutator;
import org.datalorax.populace.populator.mutator.ensure.EnsureMutator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

public class ChangeSetElementsMutatorTest {
    private Mutator mutator;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorContext.class);

        mutator = new ChangeSetElementsMutator();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnUnsupportedType() throws Exception {
        mutator.mutate(Date.class, null, null, config);
    }

    @Test
    public void shouldReturnNullForNullInput() throws Exception {
        // When:
        final Object mutated = mutator.mutate(Set.class, null, null, config);

        // Then:
        assertThat(mutated, is(nullValue()));
    }

    @Test
    public void shouldNotBlowUpOnRawTypes() throws Exception {
        // Given:
        givenMutatorRegistered(Object.class, PassThroughMutator.INSTANCE);
        final Set currentValue = new HashSet();

        // When:
        mutator.mutate(Set.class, currentValue, null, config);
    }

    @Test
    public void shouldKeepTryingMutateAndAddElementToTheSetUntilItSucceeds() throws Exception {
        // Given:
        final Mutator componentMutator = mock(Mutator.class);
        final Type setType = TypeUtils.parameterize(Set.class, String.class);
        givenCreateInstanceWillReturn(String.class, "new");
        givenMutatorRegistered(String.class, componentMutator);
        final Set<String> currentValue = new HashSet<String>() {{
            add("value_1");
            add("value_2");
        }};
        when(componentMutator.mutate(String.class, "new", null, config)).thenReturn("value_1");
        when(componentMutator.mutate(String.class, "value_1", null, config)).thenReturn("value_2");
        when(componentMutator.mutate(String.class, "value_2", null, config)).thenReturn("value_3");

        // When:
        mutator.mutate(setType, currentValue, null, config);

        // Then:
        verify(componentMutator, times(3)).mutate(any(Class.class), anyObject(), eq(null), any(PopulatorContext.class));
        assertThat(currentValue, containsInAnyOrder("value_1", "value_2", "value_3"));
    }

    @Test
    public void shouldNotPassParentObjectToComponentMutatorAsItsNotTheParentOfTheComponent() throws Exception {
        // Given:
        final Object parent = new Object();
        final Mutator componentMutator = mock(Mutator.class);
        final Type setType = TypeUtils.parameterize(Set.class, String.class);
        givenMutatorRegistered(String.class, componentMutator);
        final Set<String> currentValue = new HashSet<String>() {{
            add("value1");
        }};

        // When:
        mutator.mutate(setType, currentValue, parent, config);

        // Then:
        verify(componentMutator).mutate(any(Class.class), anyObject(), eq(null), any(PopulatorContext.class));
    }

    @Test
    public void shouldGetComponentMutatorUsingTypeFromFirstNonNullElement() throws Exception {
        // Given:
        givenCreateInstanceWillReturn(Long.class, 4L);
        givenMutatorRegistered(Long.class, EnsureMutator.INSTANCE);
        final Type baseSetType = TypeUtils.parameterize(Set.class, Number.class);
        final Set<Long> currentValue = new HashSet<Long>() {{
            add(null);
            add(1L);
        }};

        // When:
        mutator.mutate(baseSetType, currentValue, null, config);

        // Then:
        verify(config).getMutator(Long.class);
    }

    @Test(enabled = false)  // Enable once InstanceFactory can handle Long
    public void shouldGetComponentMutatorUsingFieldTypeIfNoNonNullElements() throws Exception {
        // Given:
        givenMutatorRegistered(Number.class, EnsureMutator.INSTANCE);
        final Type baseSetType = TypeUtils.parameterize(Set.class, Number.class);
        final Set<Long> currentValue = new HashSet<Long>() {{
            add(null);
        }};

        // When:
        mutator.mutate(baseSetType, currentValue, null, config);

        // Then:
        verify(config).getMutator(Number.class);
    }

    @Test
    public void shouldPassActualValueTypeToMutatorWhenSetContainsNonNullElement() throws Exception {
        // Given:
        final Mutator componentMutator = mock(Mutator.class);
        givenMutatorRegistered(Long.class, componentMutator);
        final Type baseType = TypeUtils.parameterize(Set.class, Number.class);
        final Set<Long> currentValue = new HashSet<Long>() {{
            add(1L);
        }};

        // When:
        mutator.mutate(baseType, currentValue, null, config);

        // Then:
        verify(componentMutator).mutate(eq(Long.class), anyObject(), anyObject(), any(PopulatorContext.class));
    }

    @Test
    public void shouldPassFieldValueTypeToMutatorIfMapHasNoNonNullElements() throws Exception {
        // Given:
        final Mutator componentMutator = mock(Mutator.class);
        givenMutatorRegistered(Number.class, componentMutator);
        final Type baseType = TypeUtils.parameterize(Set.class, Number.class);
        final Set<Long> currentValue = new HashSet<Long>();

        // When:
        mutator.mutate(baseType, currentValue, null, config);

        // Then:
        verify(componentMutator).mutate(eq(Number.class), anyObject(), anyObject(), any(PopulatorContext.class));
    }

    private void givenMutatorRegistered(Class<?> type, Mutator mutator) {
        when(config.getMutator(type)).thenReturn(mutator);
    }

    private <T> void givenCreateInstanceWillReturn(final Class<T> type, final T instance) {
        when(config.createInstance(type, null)).thenReturn(instance);
    }
}