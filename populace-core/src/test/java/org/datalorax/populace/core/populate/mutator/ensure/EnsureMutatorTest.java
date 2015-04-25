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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

public class EnsureMutatorTest {
    private EnsureMutator mutator;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorContext.class);

        mutator = EnsureMutator.INSTANCE;
    }

    @Test
    public void shouldDoNothingIfValueAlreadyNotNull() throws Exception {
        // When:
        final Object mutated = mutator.mutate(String.class, "current", null, config);

        // Then:
        assertThat(mutated, is("current"));
        verify(config, never()).createInstance(any(Type.class), anyObject());
    }

    @Test
    public void shouldInstantiateIfValueNull() throws Exception {
        // Given:
        final Object parent = new Object();

        // When:
        mutator.mutate(String.class, null, parent, config);

        // Then:
        verify(config).createInstance(String.class, parent);
    }

    @Test
    public void shouldReturnNewValue() throws Exception {
        // Given:
        when(config.createInstance(any(Type.class), anyObject())).thenReturn("new");

        // When:
        final Object mutated = mutator.mutate(String.class, null, null, config);

        // Then:
        assertThat(mutated, is("new"));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                EnsureMutator.INSTANCE,
                new EnsureMutator())
            .addEqualityGroup(
                mock(Mutator.class))
            .testEquals();
    }
}