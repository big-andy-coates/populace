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

package org.datalorax.populace.populator.mutator;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

/**
 * @author Andrew Coates - 27/02/2015.
 */
public class StringMutatorTest {
    private Mutator mutator;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorContext.class);
        mutator = new StringMutator();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnUnsupportedType() throws Exception {
        mutator.mutate(Integer.class, null, null, config);
    }

    @Test
    public void shouldMutateString() throws Exception {
        // Given:
        final String original = "hello";

        // When:
        final String populated = (String) mutator.mutate(String.class, original, null, config);

        // Then:
        assertThat(populated, is(not(original)));
    }

    @Test
    public void shouldCreateNewStringIfCurrentIsNull() throws Exception {
        // When:
        final String populated = (String) mutator.mutate(String.class, null, null, config);

        // Then:
        assertThat(populated, is(not(nullValue())));
    }
}