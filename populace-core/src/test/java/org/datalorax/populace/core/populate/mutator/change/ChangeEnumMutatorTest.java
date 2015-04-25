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

import com.google.common.testing.EqualsTester;
import org.datalorax.populace.core.populate.Mutator;
import org.datalorax.populace.core.populate.PopulatorContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

public class ChangeEnumMutatorTest {
    private ChangeEnumMutator mutator;
    private PopulatorContext context;

    @BeforeMethod
    public void setUp() throws Exception {
        context = mock(PopulatorContext.class);

        mutator = ChangeEnumMutator.INSTANCE;
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnNonEnumType() throws Exception {
        mutator.mutate(String.class, null, null, context);
    }

    @Test
    public void shouldReturnNullIfCurrentValueNull() throws Exception {
        // When:
        final Object result = mutator.mutate(SomeEnum.class, null, null, context);

        // Then:
        assertThat(result, is(nullValue()));
    }

    @Test
    public void shouldReturnTheNextEnumValue() throws Exception {
        assertThat(mutator.mutate(SomeEnum.class, SomeEnum.One, null, context), is(SomeEnum.Two));
        assertThat(mutator.mutate(SomeEnum.class, SomeEnum.Two, null, context), is(SomeEnum.Three));
        assertThat(mutator.mutate(SomeEnum.class, SomeEnum.Three, null, context), is(SomeEnum.One));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                ChangeEnumMutator.INSTANCE,
                new ChangeEnumMutator())
            .addEqualityGroup(
                mock(Mutator.class))
            .testEquals();
    }

    private enum SomeEnum {One, Two, Three}
}