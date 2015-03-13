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

import org.datalorax.populace.core.populate.Mutator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ChangeBigDecimalMutatorTest {
    private Mutator mutator;

    @BeforeMethod
    public void setUp() throws Exception {
        mutator = ChangeBigDecimalMutator.INSTANCE;
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnUnsupportedType() throws Exception {
        // When:
        mutator.mutate(Map.class, null, null, null);
    }

    @Test
    public void shouldReturnNullOnNullInput() throws Exception {
        // When:
        final Object mutated = mutator.mutate(BigDecimal.class, null, null, null);

        // Then:
        assertThat(mutated, is(nullValue()));
    }

    @Test
    public void shouldMutateExistingValue() throws Exception {
        // Given:
        final BigDecimal original = new BigDecimal("1.3456");

        // When:
        final Object mutated = mutator.mutate(BigDecimal.class, original, null, null);

        // Then:
        assertThat(mutated, is(instanceOf(BigDecimal.class)));
        assertThat((BigDecimal) mutated, is(not(comparesEqualTo(original))));
    }
}