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
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class EnsureMapValuesNotNullMutatorTest {
    private EnsureMapValuesNotNullMutator mutator;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorContext.class);

        mutator = EnsureMapValuesNotNullMutator.INSTANCE;
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnUnsupportedType() throws Exception {
        mutator.mutate(String.class, null, null, config);
    }

    @Test
    public void shouldReturnNullForNullInput() throws Exception {
        // When:
        final Object mutated = mutator.mutate(Map.class, null, null, config);

        // Then:
        assertThat(mutated, is(nullValue()));
    }

    @Test
    public void shouldJustReturnEmptyMap() throws Exception {
        // Given:
        final Map<String, Integer> currentValue = new HashMap<>();

        // When:
        final Object mutated = mutator.mutate(Map.class, currentValue, null, config);

        // Then:
        assertThat(mutated, is(sameInstance(currentValue)));
        verifyNoMoreInteractions(config);
    }

    @Test
    public void shouldDoNothingToMapsWithNoNullValues() throws Exception {
        // Given:
        final HashMap<String, Integer> currentValue = new HashMap<>();
        currentValue.put("key", 1);
        final Map<String, Integer> original = new HashMap<>(currentValue);

        // When:
        final Object mutated = mutator.mutate(Map.class, currentValue, null, config);

        // Then:
        assertThat(mutated, is(sameInstance(currentValue)));
        assertThat(mutated, is(original));
        verifyNoMoreInteractions(config);
    }

    @Test
    public void shouldUseValueTypeTypeVariableForRawTypes() throws Exception {
        // Given:
        when(config.createInstance(any(Type.class), anyObject())).thenReturn("value");
        final Map currentValue = new HashMap<String, String>() {{
            put("key", null);
        }};

        // When:
        mutator.mutate(Map.class, currentValue, null, config);

        // Then:
        verify(config).createInstance(eq(Map.class.getTypeParameters()[1]), anyObject());
    }

    @Test
    public void shouldLeaveValueAsNullIfCreateInstanceReturnsNull() throws Exception {
        // Given:
        when(config.createInstance(any(Type.class), anyObject())).thenReturn(null);
        final Type type = TypeUtils.parameterise(Map.class, String.class, String.class);
        final Map currentValue = new HashMap<String, String>() {{
            put("key", null);
        }};

        // When:
        final Map mutated = (Map) mutator.mutate(type, currentValue, null, config);

        // Then:
        assertThat(mutated.values().iterator().next(), is(nullValue()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldWorkWithDerivedTypes() throws Exception {
        // Given:
        when(config.createInstance(any(Type.class), anyObject())).thenReturn("value");
        final Type type = TypeUtils.parameterise(HashMap.class, String.class, String.class);
        final Map currentValue = new HashMap<String, String>() {{
            put("key", null);
        }};

        // When:
        final Map<String, String> mutated = (Map<String, String>) mutator.mutate(type, currentValue, null, config);

        // Then:
        assertThat(mutated.get("key"), is("value"));
    }

    @Test
    public void shouldNotPassParentObjectToCreateInstanceAsItsNotTheParentOfTheComponent() throws Exception {
        // Given:
        final Object parent = new Object();
        final Type mapType = TypeUtils.parameterise(Map.class, Integer.class, String.class);
        final Map<Integer, String> currentValue = new HashMap<Integer, String>() {{
            put(1, null);
        }};

        // When:
        mutator.mutate(mapType, currentValue, parent, config);

        // Then:
        verify(config).createInstance(any(Type.class), eq(null));
    }

    @Test
    public void shouldGetValueTypeFromGenerics() throws Exception {
        // Given:
        final Type baseType = TypeUtils.parameterise(Map.class, String.class, Number.class);
        final Map<String, Long> currentValue = new HashMap<String, Long>() {{
            put("key", null);
        }};

        // When:
        mutator.mutate(baseType, currentValue, null, config);

        // Then:
        verify(config).createInstance(eq(Number.class), anyObject());
    }
}