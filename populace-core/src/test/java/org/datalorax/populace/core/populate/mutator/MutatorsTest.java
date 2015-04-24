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
import org.datalorax.populace.core.util.ImmutableTypeMap;
import org.datalorax.populace.core.util.TypeUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

public class MutatorsTest {
    private Mutators.Builder builder;
    private Mutators mutators;

    @BeforeMethod
    public void setUp() throws Exception {
        builder = Mutators.newBuilder();
        mutators = builder.build();
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
        // When:
        final Mutator mutator = mutators.getDefault();

        // Then:
        assertThat(mutator, is(notNullValue()));
    }

    @Test
    public void shouldReturnDefaultMutatorForUnregisteredNonArrayType() throws Exception {
        // Given:
        final Type unregisteredType = getClass();

        // When:
        final Mutator mutator = mutators.get(unregisteredType);

        // Then:
        assertThat(mutator, is(mutators.getDefault()));
    }

    @Test
    public void shouldBeAbleToOverrideTheDefault() throws Exception {
        // Given:
        final Mutator newDefaultMutator = mock(Mutator.class, "default");

        // When:
        final Mutators mutators = builder
            .withDefaultMutator(newDefaultMutator)
            .build();

        // Then:
        assertThat(mutators.getDefault(), is(newDefaultMutator));
    }

    @Test
    public void shouldHaveDefaultArrayMutator() throws Exception {
        // Given:
        final Type arrayType = TypeUtils.genericArrayType(int.class);

        // When:
        final Mutator mutator = mutators.get(arrayType);

        // Then:
        assertThat(mutator, is(notNullValue()));
    }

    @Test
    public void shouldReturnDefaultArrayMutatorIfArrayTypeNotRegistered() throws Exception {
        // Given:
        final Type unregistered = TypeUtils.genericArrayType(int.class);

        // When:
        final Mutator mutator = mutators.get(unregistered);

        // Then:
        assertThat(mutator, is(mutators.getArrayDefault()));
    }

    @Test
    public void shouldBeAbleToOverrideDefaultArrayMutator() throws Exception {
        // Given:
        final Mutator newDefaultMutator = mock(Mutator.class, "array default");

        // When:
        final Mutators mutators = builder
            .withArrayDefaultMutator(newDefaultMutator)
            .build();

        // Then:
        assertThat(mutators.getArrayDefault(), is(newDefaultMutator));
    }

    @Test
    public void shouldReturnNotPresentFromGetSpecificIfNoSpecificRegistered() throws Exception {
        // Given:
        final Type unregistered = TypeUtils.parameterise(Collection.class, Integer.class);

        // When:
        final Optional<Mutator> mutator = mutators.getSpecific(unregistered);

        // Then:
        assertThat(mutator, is(Optional.<Mutator>empty()));
    }

    @Test
    public void shouldReturnSpecificMutatorFromGetSpecificIfRegistered() throws Exception {
        // Given:
        final Mutator expected = mock(Mutator.class);
        final Type registered = TypeUtils.parameterise(Collection.class, Integer.class);
        mutators = builder.withSpecificMutator(registered, expected).build();

        // When:
        final Optional<Mutator> mutator = mutators.getSpecific(registered);

        // Then:
        assertThat(mutator, is(Optional.of(expected)));
    }

    @Test
    public void shouldReturnNotPresentFromGetSuperIfNoSuperRegistered() throws Exception {
        // When:
        final Optional<Mutator> mutator = mutators.getSuper(String.class);

        // Then:
        assertThat(mutator, is(Optional.<Mutator>empty()));
    }

    @Test
    public void shouldReturnSuperMutatorFromGetSuperIfRegistered() throws Exception {
        // Given:
        final Mutator expected = mock(Mutator.class);
        mutators = builder.withSuperMutator(String.class, expected).build();

        // When:
        final Optional<Mutator> mutator = mutators.getSuper(String.class);

        // Then:
        assertThat(mutator, is(Optional.of(expected)));
    }

    @Test
    public void shouldSupportSpecificMutatorForBigDecimal() throws Exception {
        // When:
        final Mutator mutator = mutators.get(BigDecimal.class);

        // Then:
        assertThat(mutator, is(not(mutators.getDefault())));
    }

    @Test
    public void shouldGetMutatorForNonClassOrParameterizedTypeIfSpecificRegistered() throws Exception {
        // Given:
        final Type wildcardType = TypeUtils.wildcardType();
        final Mutator mutator = mock(Mutator.class, "specific");

        // When:
        final Mutators mutators = builder
            .withSpecificMutator(wildcardType, mutator)
            .build();

        // Then:
        assertThat(mutators.get(wildcardType), is(mutator));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        final ImmutableTypeMap<Mutator> rawMutators = mock(ImmutableTypeMap.class, "1");

        new EqualsTester()
            .addEqualityGroup(
                new Mutators(rawMutators),
                new Mutators(rawMutators))
            .addEqualityGroup(
                new Mutators(mock(ImmutableTypeMap.class, "2")))
            .testEquals();
    }
}