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

import org.datalorax.populace.core.populate.Mutator;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class EnsureNullMutatorTest {
    @Test
    public void shouldMostCertainlyReturnNull() throws Exception {
        // Given:
        final Mutator mutator = EnsureNullMutator.INSTANCE;

        // When:
        final Object mutated = mutator.mutate(String.class, "hello", null, null);

        // Then:
        assertThat(mutated, is(nullValue()));
    }
}