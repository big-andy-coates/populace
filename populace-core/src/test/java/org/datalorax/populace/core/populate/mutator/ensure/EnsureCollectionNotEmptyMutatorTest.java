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
    public void shouldWorkWithRawBaseTypes() throws Exception {
        // Given:
        givenMutatorRegistered(List.class.getTypeParameters()[0], mutatorThatReturns("this"));
        final List<String> currentValue = new ArrayList<>();
        final List<String> expected = new ArrayList<>();
        expected.add("this");

        // When:
        final Object mutated = mutator.mutate(List.class, currentValue, null, config);

        // Then:
        assertThat(mutated, is(expected));
    }

    @Test
    public void shouldWorkWithRawDerivedTypes() throws Exception {
        // Given:
        givenMutatorRegistered(ArrayList.class.getTypeParameters()[0], mutatorThatReturns("this"));
        final List<String> currentValue = new ArrayList<>();
        final List<String> expected = new ArrayList<>();
        expected.add("this");

        // When:
        final Object mutated = mutator.mutate(ArrayList.class, currentValue, null, config);

        // Then:
        assertThat(mutated, is(expected));
    }

    @Test
    public void shouldWorkWithParameterizedTypes() throws Exception {
        // Given:
        givenMutatorRegistered(String.class, mutatorThatReturns("something"));
        final Type pt = TypeUtils.parameterise(ArrayList.class, String.class);
        final List<String> currentValue = new ArrayList<>();
        final List<String> expected = new ArrayList<>();
        expected.add("something");

        // When:
        final Object mutated = mutator.mutate(pt, currentValue, null, config);

        // Then:
        assertThat(mutated, is(expected));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                EnsureCollectionNotEmptyMutator.INSTANCE,
                new EnsureCollectionNotEmptyMutator())
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