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

package org.datalorax.populace.core.populate.mutator.change;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.core.populate.Mutator;
import org.datalorax.populace.core.populate.PopulatorContext;
import org.datalorax.populace.core.populate.PopulatorException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

public class ChangeCollectionElementsMutatorTest {
    private Mutator mutator;
    private PopulatorContext config;
    private Mutator componentMutator;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorContext.class);
        componentMutator = mock(Mutator.class);

        mutator = new ChangeCollectionElementsMutator();
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
    public void shouldNotBlowUpOnRawBaseType() throws Exception {
        // Given:
        givenCreateInstanceWillReturn("new Instance");
        givenComponentMutatorWillReturnSomethingDifferent();
        final Set currentValue = new HashSet();

        // When:
        mutator.mutate(Collection.class, currentValue, null, config);
    }

    @Test
    public void shouldNotBlowUpOnRawDerivedTypes() throws Exception {
        // Given:
        givenCreateInstanceWillReturn("new Instance");
        givenComponentMutatorWillReturnSomethingDifferent();
        final Set<String> currentValue = new HashSet<>();

        // When:
        mutator.mutate(Set.class, currentValue, null, config);
    }

    @Test
    public void shouldKeepTryingMutateAndAddElementToTheSetUntilItSucceeds() throws Exception {
        // Given:
        givenCreateInstanceWillReturn("new");
        givenMutatorRegistered();
        final Type setType = TypeUtils.parameterize(Set.class, String.class);
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
        givenCreateInstanceWillReturn("new Instance");
        givenComponentMutatorWillReturnSomethingDifferent();
        final Object parent = new Object();
        final Type setType = TypeUtils.parameterize(Collection.class, String.class);
        final Collection currentValue = new ArrayList<String>() {{
            add("value1");
        }};

        // When:
        mutator.mutate(setType, currentValue, parent, config);

        // Then:
        verify(componentMutator).mutate(any(Class.class), anyObject(), eq(null), any(PopulatorContext.class));
    }

    @Test
    public void shouldGetComponentTypeUsingTypeFromFirstNonNullElement() throws Exception {
        // Given:
        givenCreateInstanceWillReturn(4L);
        givenComponentMutatorWillReturnSomethingDifferent();
        final Type baseSetType = TypeUtils.parameterize(Collection.class, Number.class);
        final Set<Number> currentValue = new HashSet<Number>() {{
            add(null);
            add(1L);
        }};

        // When:
        mutator.mutate(baseSetType, currentValue, null, config);

        // Then:
        verify(config).createInstance(eq(Long.class), anyObject());
        verify(config).getMutator(Long.class);
        verify(componentMutator).mutate(eq(Long.class), anyObject(), anyObject(), any(PopulatorContext.class));
    }

    @Test
    public void shouldGetComponentTypeUsingFieldTypeIfNoNonNullElements() throws Exception {
        // Given:
        givenCreateInstanceWillReturn(4L);
        givenComponentMutatorWillReturnSomethingDifferent();
        final Type baseSetType = TypeUtils.parameterize(Set.class, Number.class);
        final Set<Long> currentValue = new HashSet<Long>() {{
            add(null);
        }};

        // When:
        mutator.mutate(baseSetType, currentValue, null, config);

        // Then:
        verify(config).createInstance(eq(Number.class), anyObject());
        verify(config).getMutator(Number.class);
        verify(componentMutator).mutate(eq(Number.class), anyObject(), anyObject(), any(PopulatorContext.class));
    }

    @Test
    public void shouldNotThrowIfComponentMutatorDoesNotMutateButCollectionUpdated() throws Exception {
        // Given:
        givenCreateInstanceWillReturn(1L);
        givenComponentMutatorDoesNotMutate();
        final Type baseType = TypeUtils.parameterize(Set.class, Long.class);
        final Set<Long> currentValue = new HashSet<>();

        // When:
        mutator.mutate(baseType, currentValue, null, config);
    }

    @Test(expectedExceptions = PopulatorException.class)
    public void shouldThrowIfComponentMutatorDoesNotMutateAndCollectionNotUpdated() throws Exception {
        // Given:
        givenCreateInstanceWillReturn(1L);
        givenComponentMutatorDoesNotMutate();
        final Type baseType = TypeUtils.parameterize(Set.class, Long.class);
        final Set<Long> currentValue = new HashSet<>();
        currentValue.add(1L);

        // When:
        mutator.mutate(baseType, currentValue, null, config);
    }

    @Test
    public void shouldNotThrowIfMutatorReturnsNullAndCollectionUpdated() throws Exception {
        // Given:
        givenCreateInstanceWillReturn(1L);
        givenComponentMutatorWillReturnNull();
        final Type baseType = TypeUtils.parameterize(Set.class, Long.class);
        final Set<Long> currentValue = new HashSet<>();

        // When:
        mutator.mutate(baseType, currentValue, null, config);
    }

    @Test(expectedExceptions = PopulatorException.class)
    public void shouldThrowIfMutatorReturnsNullAndCollectionNotUpdated() throws Exception {
        // Given:
        givenCreateInstanceWillReturn(1L);
        givenComponentMutatorWillReturnNull();
        final Type baseType = TypeUtils.parameterize(Set.class, Long.class);
        final Set<Long> currentValue = new HashSet<>();
        currentValue.add(null);

        // When:
        mutator.mutate(baseType, currentValue, null, config);
    }

    private void givenMutatorRegistered() {
        when(config.getMutator(any(Class.class))).thenReturn(componentMutator);
    }

    private void givenComponentMutatorDoesNotMutate() {
        givenMutatorRegistered();
        when(componentMutator.mutate(any(Type.class), anyObject(), anyObject(), any(PopulatorContext.class)))
            .thenAnswer(invocation -> invocation.getArguments()[1]);
    }

    private void givenComponentMutatorWillReturnSomethingDifferent() {
        givenMutatorRegistered();
        when(componentMutator.mutate(any(Type.class), anyObject(), anyObject(), any(PopulatorContext.class)))
            .thenAnswer(invocation -> {
                final Object current = invocation.getArguments()[1];
                if (current instanceof String) {
                    return current + " changed";
                }
                if (current instanceof Long) {
                    return 1L + (Long) current;
                }
                throw new UnsupportedOperationException("Test needs extending to support: " + current.getClass());
            });
    }

    private void givenComponentMutatorWillReturnNull() {
        givenMutatorRegistered();
        when(componentMutator.mutate(any(Type.class), anyObject(), anyObject(), any(PopulatorContext.class))).thenReturn(null);
    }

    private <T> void givenCreateInstanceWillReturn(final T instance) {
        when(config.createInstance(any(Type.class), anyObject())).thenReturn(instance);
    }
}