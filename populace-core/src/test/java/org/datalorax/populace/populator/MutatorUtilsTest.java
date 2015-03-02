package org.datalorax.populace.populator;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.mutator.MutatorUtils;
import org.datalorax.populace.typed.TypeMap;
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

public class MutatorUtilsTest {
    private TypeMap.Builder<Mutator> builder;

    @BeforeMethod
    public void setUp() throws Exception {
        builder = MutatorUtils.defaultMutators();
    }

    @Test
    public void shouldHavePrimitiveMutatorsByDefault() throws Exception {
        // Given:
        final Mutator defaultMutator = mock(Mutator.class, "default");

        // When:
        final TypeMap<Mutator> mutators = builder.build();

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
        final TypeMap<Mutator> mutators = builder.build();

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
        final TypeMap<Mutator> mutators = builder.build();

        // Then:
        assertThat(mutators.get(String.class), is(not(defaultMutator)));
    }

    @Test
    public void shouldHaveDateMutatorsByDefault() throws Exception {
        // Given:
        final Mutator defaultMutator = mock(Mutator.class, "default");

        // When:
        final TypeMap<Mutator> mutators = builder.build();

        // Then:
        assertThat(mutators.get(Date.class), is(not(defaultMutator)));
    }

    @Test
    public void shouldHaveCollectionMutatorsByDefault() throws Exception {
        // Given:
        final Mutator defaultMutator = mock(Mutator.class, "default");

        // When:
        final TypeMap<Mutator> mutators = builder.build();

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
        final TypeMap<Mutator> mutators = builder.build();

        // Then:
        assertThat(mutators.get(unregisteredType), is(notNullValue()));
    }

    @Test
    public void shouldHaveDefaultArrayMutator() throws Exception {
        // Given:
        final Type arrayType = TypeUtils.genericArrayType(int.class);

        // When:
        final TypeMap<Mutator> mutators = builder.build();

        // Then:
        assertThat(mutators.get(arrayType), is(notNullValue()));
    }
}