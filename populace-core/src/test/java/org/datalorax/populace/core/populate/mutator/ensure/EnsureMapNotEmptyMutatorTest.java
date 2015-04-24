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

import com.google.common.testing.EqualsTester;
import org.datalorax.populace.core.populate.Mutator;
import org.datalorax.populace.core.populate.PopulatorContext;
import org.datalorax.populace.core.util.TypeUtils;
import org.datalorax.populace.core.walk.inspector.Inspector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class EnsureMapNotEmptyMutatorTest {
    private Mutator mutator;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorContext.class);

        mutator = EnsureMapNotEmptyMutator.INSTANCE;
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
    public void shouldDoNothingToNonEmptyMap() throws Exception {
        // Given:
        final Map map = mock(Map.class);

        // When:
        final Object mutated = mutator.mutate(Map.class, map, null, config);

        // Then:
        assertThat(mutated, is(sameInstance(map)));
        verify(map).isEmpty();
        verifyNoMoreInteractions(map);
    }

    @Test
    public void shouldWorkWithRawBaseTypes() throws Exception {
        // Given:
        givenMutatorRegistered(Map.class.getTypeParameters()[0], mutatorThatReturns("key"));
        givenMutatorRegistered(Map.class.getTypeParameters()[1], mutatorThatReturns("value"));
        final Map<String, String> currentValue = new HashMap<>();
        final Map<String, String> expected = new HashMap<>();
        expected.put("key", "value");

        // When:
        final Object mutated = mutator.mutate(Map.class, currentValue, null, config);

        // Then:
        assertThat(mutated, is(expected));
    }

    @Test
    public void shouldWorkWithRawDerivedTypes() throws Exception {
        // Given:
        givenMutatorRegistered(HashMap.class.getTypeParameters()[0], mutatorThatReturns("key"));
        givenMutatorRegistered(HashMap.class.getTypeParameters()[1], mutatorThatReturns("value"));
        final Map<String, String> currentValue = new HashMap<>();
        final Map<String, String> expected = new HashMap<>();
        expected.put("key", "value");

        // When:
        final Object mutated = mutator.mutate(HashMap.class, currentValue, null, config);

        // Then:
        assertThat(mutated, is(expected));
    }

    @Test
    public void shouldWorkWithParameterizedTypes() throws Exception {
        // Given:
        givenMutatorRegistered(String.class, mutatorThatReturns("key"));
        givenMutatorRegistered(Integer.class, mutatorThatReturns(42));
        final Type pt = TypeUtils.parameterise(HashMap.class, String.class, Integer.class);
        final Map<String, Integer> currentValue = new HashMap<>();
        final Map<String, Integer> expected = new HashMap<>();
        expected.put("key", 42);

        // When:
        final Object mutated = mutator.mutate(pt, currentValue, null, config);

        // Then:
        assertThat(mutated, is(expected));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                EnsureMapNotEmptyMutator.INSTANCE,
                new EnsureMapNotEmptyMutator())
            .addEqualityGroup(
                mock(Inspector.class))
            .testEquals();
    }

    private void givenMutatorRegistered(final Type type, final Mutator mutator) {
        when(config.getMutator(type)).thenReturn(mutator);
    }

    private Mutator mutatorThatReturns(final Object value) {
        return (type, currentValue, parent, config1) -> value;
    }
}