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

package org.datalorax.populace.core.populate.mutator.commbination;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;
import org.datalorax.populace.core.populate.Mutator;
import org.datalorax.populace.core.populate.PopulatorContext;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;

import static org.datalorax.populace.core.populate.mutator.commbination.ChainedMutator.chain;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class ChainedMutatorTest {
    private Mutator first;
    private Mutator second;
    private Type type;
    private ChainedMutator mutator;
    private Object currentValue;
    private Object parent;
    private PopulatorContext config;
    private Object mutated;
    private Mutator third;

    @BeforeMethod
    public void setUp() throws Exception {
        first = mock(Mutator.class);
        second = mock(Mutator.class);
        type = mock(Type.class);
        currentValue = "current";
        parent = "parent";
        mutated = "mutated";
        config = mock(PopulatorContext.class);

        mutator = new ChainedMutator(first, second);
        third = mock(Mutator.class, "3");
    }

    @Test
    public void shouldCalledBothVisitorsInOrder() throws Exception {
        // When:
        mutator.mutate(type, currentValue, parent, config);

        // Then:
        InOrder inOrder = inOrder(first, second);
        inOrder.verify(first).mutate(eq(type), anyObject(), eq(parent), eq(config));
        inOrder.verify(second).mutate(eq(type), anyObject(), eq(parent), eq(config));
    }

    @Test
    public void shouldPassCurrentValueToFirstMutator() throws Exception {
        // When:
        mutator.mutate(type, currentValue, parent, config);

        // Then:
        verify(first).mutate(any(Type.class), eq(currentValue), anyObject(), any(PopulatorContext.class));
    }

    @Test
    public void shouldPassOutputOfFirstAsCurrentValueToSecond() throws Exception {
        // Given:
        when(first.mutate(any(Type.class), anyObject(), anyObject(), any(PopulatorContext.class))).thenReturn(mutated);

        // When:
        mutator.mutate(type, currentValue, parent, config);

        // Then:
        verify(second).mutate(any(Type.class), eq(mutated), anyObject(), any(PopulatorContext.class));
    }

    @Test
    public void shouldPassOutputOfSecondAsCurrentValueToThird() throws Exception {
        // Given:
        when(second.mutate(any(Type.class), anyObject(), anyObject(), any(PopulatorContext.class))).thenReturn(mutated);

        // When:
        chain(first, second, third).mutate(type, currentValue, parent, config);

        // Then:
        verify(third).mutate(any(Type.class), eq(mutated), anyObject(), any(PopulatorContext.class));
    }

    @Test
    public void shouldReturnTheResultOfLastMutator() throws Exception {
        // Given:
        when(third.mutate(any(Type.class), anyObject(), anyObject(), any(PopulatorContext.class))).thenReturn(mutated);

        // When:
        final Object result = chain(first, second, third).mutate(type, currentValue, parent, config);

        // Then:
        assertThat(result, is(mutated));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                new ChainedMutator(first, second),
                new ChainedMutator(first, second),
                chain(first, second))
            .addEqualityGroup(
                new ChainedMutator(second, first))
            .addEqualityGroup(
                new ChainedMutator(mock(Mutator.class, "2"), second))
            .addEqualityGroup(
                new ChainedMutator(first, mock(Mutator.class, "2")))
            .testEquals();
    }

    @Test
    public void shouldThrowNPEsOnConstructorParams() throws Exception {
        new NullPointerTester()
            .setDefault(Mutator.class, mock(Mutator.class))
            .testAllPublicConstructors(ChainedMutator.class);
    }
}