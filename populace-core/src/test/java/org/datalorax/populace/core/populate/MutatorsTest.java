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

package org.datalorax.populace.core.populate;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.core.populate.mutator.Mutators;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

public class MutatorsTest {
    private Mutators.Builder builder;

    @BeforeMethod
    public void setUp() throws Exception {
        builder = Mutators.newBuilder();
    }

    @Test
    public void shouldHavePrimitiveMutatorsByDefault() throws Exception {
        // Given:
        final Mutator defaultMutator = mock(Mutator.class, "default");

        // When:
        final Mutators mutators = builder
            .withDefaultMutator(defaultMutator)
            .build();

        // Then:
        assertThat(mutators.get(boolean.class), is(not(defaultMutator)));
        assertThat(mutators.get(byte.class), is(not(defaultMutator)));
        assertThat(mutators.get(char.class), is(not(defaultMutator)));
        assertThat(mutators.get(short.class), is(not(defaultMutator)));
        assertThat(mutators.get(int.class), is(not(defaultMutator)));
        assertThat(mutators.get(long.class), is(not(defaultMutator)));
        assertThat(mutators.get(float.class), is(not(defaultMutator)));
        assertThat(mutators.get(double.class), is(not(defaultMutator)));
    }

    @Test
    public void shouldHaveBoxPrimitiveMutatorsByDefault() throws Exception {
        // Given:
        final Mutator defaultMutator = mock(Mutator.class, "default");

        // When:
        final Mutators mutators = builder
            .withDefaultMutator(defaultMutator)
            .build();

        // Then:
        assertThat(mutators.get(Boolean.class), is(not(defaultMutator)));
        assertThat(mutators.get(Byte.class), is(not(defaultMutator)));
        assertThat(mutators.get(Character.class), is(not(defaultMutator)));
        assertThat(mutators.get(Short.class), is(not(defaultMutator)));
        assertThat(mutators.get(Integer.class), is(not(defaultMutator)));
        assertThat(mutators.get(Long.class), is(not(defaultMutator)));
        assertThat(mutators.get(Float.class), is(not(defaultMutator)));
        assertThat(mutators.get(Double.class), is(not(defaultMutator)));
    }

    @Test
    public void shouldHaveStringMutatorsByDefault() throws Exception {
        // Given:
        final Mutator defaultMutator = mock(Mutator.class, "default");

        // When:
        final Mutators mutators = builder
            .withDefaultMutator(defaultMutator)
            .build();

        // Then:
        assertThat(mutators.get(String.class), is(not(defaultMutator)));
    }

    @Test
    public void shouldHaveDateMutatorsByDefault() throws Exception {
        // Given:
        final Mutator defaultMutator = mock(Mutator.class, "default");

        // When:
        final Mutators mutators = builder
            .withDefaultMutator(defaultMutator)
            .build();

        // Then:
        assertThat(mutators.get(Date.class), is(not(defaultMutator)));
    }

    @Test
    public void shouldHaveCollectionMutatorsByDefault() throws Exception {
        // Given:
        final Mutator defaultMutator = mock(Mutator.class, "default");

        // When:
        final Mutators mutators = builder
            .withDefaultMutator(defaultMutator)
            .build();

        // Then:
        assertThat(mutators.get(List.class), is(not(defaultMutator)));
        assertThat(mutators.get(Set.class), is(not(defaultMutator)));
        assertThat(mutators.get(Map.class), is(not(defaultMutator)));
    }

    @Test
    public void shouldHaveDefaultMutator() throws Exception {
        // Given:
        final Type unregisteredType = getClass();

        // When:
        final Mutators mutators = builder.build();

        // Then:
        assertThat(mutators.get(unregisteredType), is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToOverrideTheDefault() throws Exception {
        // Given:
        final Type unregisteredType = getClass();
        final Mutator newDefaultMutator = mock(Mutator.class, "default");

        // When:
        final Mutators mutators = builder
            .withDefaultMutator(newDefaultMutator)
            .build();

        // Then:
        assertThat(mutators.get(unregisteredType), is(newDefaultMutator));
    }

    @Test
    public void shouldHaveDefaultArrayMutator() throws Exception {
        // Given:
        final Type arrayType = TypeUtils.genericArrayType(int.class);

        // When:
        final Mutators mutators = builder.build();

        // Then:
        assertThat(mutators.get(arrayType), is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToOverrideDefaultArrayMutator() throws Exception {
        // Given:
        final Type arrayType = TypeUtils.genericArrayType(int.class);
        final Mutator newDefaultMutator = mock(Mutator.class, "array default");

        // When:
        final Mutators mutators = builder
            .withArrayDefaultMutator(newDefaultMutator)
            .build();

        // Then:
        assertThat(mutators.get(arrayType), is(newDefaultMutator));
    }
}