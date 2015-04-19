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

package org.datalorax.populace.core.populate.mutator.ensure;

import org.datalorax.populace.core.populate.PopulatorContext;
import org.datalorax.populace.core.util.TypeUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Andrew Coates 27/02/2015
 */
public class EnsureListElementsNotNullMutatorTest {
    private EnsureListElementsNotNullMutator mutator;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorContext.class);

        mutator = EnsureListElementsNotNullMutator.INSTANCE;
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnUnsupportedType() throws Exception {
        mutator.mutate(String.class, null, null, config);
    }

    @Test
    public void shouldReturnNullForNullInput() throws Exception {
        // When:
        final Object mutated = mutator.mutate(List.class, null, null, config);

        // Then:
        assertThat(mutated, is(nullValue()));
    }

    @Test
    public void shouldJustReturnEmptyList() throws Exception {
        // Given:
        final List<Integer> currentValue = new ArrayList<>();

        // When:
        final Object mutated = mutator.mutate(List.class, currentValue, null, config);

        // Then:
        assertThat(mutated, is(sameInstance(currentValue)));
        verifyNoMoreInteractions(config);
    }

    @Test
    public void shouldDoNothingToListsWithNoNullValues() throws Exception {
        // Given:
        final List<String> currentValue = new ArrayList<>();
        currentValue.add("hello");
        final List<String> original = new ArrayList<>(currentValue);

        // When:
        final Object mutated = mutator.mutate(List.class, currentValue, null, config);

        // Then:
        assertThat(mutated, is(sameInstance(currentValue)));
        assertThat(mutated, is(original));
        verifyNoMoreInteractions(config);
    }

    @Test
    public void shouldUseObjectValueTypeForRawTypes() throws Exception {
        // Given:
        when(config.createInstance(any(Type.class), anyObject())).thenReturn("value");
        final List currentValue = new ArrayList<String>() {{
            add(null);
        }};

        // When:
        mutator.mutate(List.class, currentValue, null, config);

        // Then:
        verify(config).createInstance(eq(Object.class), anyObject());
    }

    @Test
    public void shouldLeaveValueAsNullIfCreateInstanceReturnsNull() throws Exception {
        // Given:
        when(config.createInstance(any(Type.class), anyObject())).thenReturn(null);
        final Type type = TypeUtils.parameterise(List.class, String.class);
        final List currentValue = new ArrayList<String>() {{
            add(null);
        }};

        // When:
        final List mutated = (List) mutator.mutate(type, currentValue, null, config);

        // Then:
        assertThat(mutated.get(0), is(nullValue()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldWorkWithDerivedTypes() throws Exception {
        // Given:
        when(config.createInstance(any(Type.class), anyObject())).thenReturn("value");
        final Type type = TypeUtils.parameterise(ArrayList.class, String.class);
        final List currentValue = new ArrayList<String>() {{
            add(null);
        }};

        // When:
        final List<String> mutated = (List<String>) mutator.mutate(type, currentValue, null, config);

        // Then:
        assertThat(mutated, hasItem("value"));
    }

    @Test
    public void shouldNotPassParentObjectToCreateInstanceAsItsNotTheParentOfTheComponent() throws Exception {
        // Given:
        final Object parent = new Object();
        final Type type = TypeUtils.parameterise(List.class, String.class);
        final List<String> currentValue = new ArrayList<String>() {{
            add(null);
        }};

        // When:
        mutator.mutate(type, currentValue, parent, config);

        // Then:
        verify(config).createInstance(any(Type.class), eq(null));
    }

    @Test
    public void shouldGetValueTypeFromGenerics() throws Exception {
        // Given:
        final Type baseType = TypeUtils.parameterise(List.class, Number.class);
        final List currentValue = new ArrayList<Long>() {{
            add(null);
        }};

        // When:
        mutator.mutate(baseType, currentValue, null, config);

        // Then:
        verify(config).createInstance(eq(Number.class), anyObject());
    }
}

// Todo(ac): don't expose Mutators to client API if 'Ensure' mutators just don't make sense.
// Todo(ac): Walker needs its own Inspector for Maps, exposing the entries. Populate should override this to just be values.