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

package org.datalorax.populace.core.populate.mutator;

import com.google.common.testing.EqualsTester;
import org.datalorax.populace.core.populate.Mutator;
import org.datalorax.populace.core.populate.instance.InstanceFactory;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

public class NoOpMutatorTest {
    @Test
    public void shouldNotChangeAThing() throws Exception {
        // Given:
        final Mutator mutator = NoOpMutator.INSTANCE;
        final String original = "hello";

        // When:
        final Object mutated = mutator.mutate(String.class, original, null, null);

        // Then:
        assertThat(mutated, is(sameInstance(original)));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                NoOpMutator.INSTANCE,
                new NoOpMutator())
            .addEqualityGroup(
                mock(InstanceFactory.class))
            .testEquals();
    }
}