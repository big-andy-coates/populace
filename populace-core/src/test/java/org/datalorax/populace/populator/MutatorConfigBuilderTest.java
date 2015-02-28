package org.datalorax.populace.populator;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;

public class MutatorConfigBuilderTest {
    private static final Map<Type, Mutator> NO_SPECIFIC_MUTATORS = Collections.emptyMap();
    private static final Map<Class<?>, Mutator> NO_BASE_MUTATORS = Collections.emptyMap();

    private MutatorConfigBuilder builder;

    @BeforeMethod
    public void setUp() throws Exception {
        builder = new MutatorConfigBuilder();
    }

    @Test
    public void shouldHavePrimitiveMutatorsByDefault() throws Exception {
        // Given:
        final Mutator defaultMutator = mock(Mutator.class, "default");

        // When:
        final MutatorConfig mutatorConfig = builder.build();

        // Then:
        assertThat(mutatorConfig.getMutator(boolean.class), is(not(defaultMutator)));
        assertThat(mutatorConfig.getMutator(byte.class), is(not(defaultMutator)));
        assertThat(mutatorConfig.getMutator(char.class), is(not(defaultMutator)));
        assertThat(mutatorConfig.getMutator(short.class), is(not(defaultMutator)));
        assertThat(mutatorConfig.getMutator(int.class), is(not(defaultMutator)));
        assertThat(mutatorConfig.getMutator(long.class), is(not(defaultMutator)));
        assertThat(mutatorConfig.getMutator(float.class), is(not(defaultMutator)));
        assertThat(mutatorConfig.getMutator(double.class), is(not(defaultMutator)));
    }

    @Test
    public void shouldHaveBoxPrimitiveMutatorsByDefault() throws Exception {
        // Given:
        final Mutator defaultMutator = mock(Mutator.class, "default");

        // When:
        final MutatorConfig mutatorConfig = builder.build();

        // Then:
        assertThat(mutatorConfig.getMutator(Boolean.class), is(not(defaultMutator)));
        assertThat(mutatorConfig.getMutator(Byte.class), is(not(defaultMutator)));
        assertThat(mutatorConfig.getMutator(Character.class), is(not(defaultMutator)));
        assertThat(mutatorConfig.getMutator(Short.class), is(not(defaultMutator)));
        assertThat(mutatorConfig.getMutator(Integer.class), is(not(defaultMutator)));
        assertThat(mutatorConfig.getMutator(Long.class), is(not(defaultMutator)));
        assertThat(mutatorConfig.getMutator(Float.class), is(not(defaultMutator)));
        assertThat(mutatorConfig.getMutator(Double.class), is(not(defaultMutator)));
    }

    @Test
    public void shouldHaveStringMutatorsByDefault() throws Exception {
        // Given:
        final Mutator defaultMutator = mock(Mutator.class, "default");

        // When:
        final MutatorConfig mutatorConfig = builder.build();

        // Then:
        assertThat(mutatorConfig.getMutator(String.class), is(not(defaultMutator)));
    }

    @Test
    public void shouldHaveDateMutatorsByDefault() throws Exception {
        // Given:
        final Mutator defaultMutator = mock(Mutator.class, "default");

        // When:
        final MutatorConfig mutatorConfig = builder.build();

        // Then:
        assertThat(mutatorConfig.getMutator(Date.class), is(not(defaultMutator)));
    }

    @Test
    public void shouldHaveCollectionMutatorsByDefault() throws Exception {
        // Given:
        final Mutator defaultMutator = mock(Mutator.class, "default");

        // When:
        final MutatorConfig mutatorConfig = builder.build();

        // Then:
        assertThat(mutatorConfig.getMutator(List.class), is(not(defaultMutator)));
        assertThat(mutatorConfig.getMutator(Set.class), is(not(defaultMutator)));
        assertThat(mutatorConfig.getMutator(Map.class), is(not(defaultMutator)));
    }

    @Test
    public void shouldOverrideExistingSpecificMutator() throws Exception {
        // Given:
        final Mutator overrideIntMutator = mock(Mutator.class, "int - override");

        // When:
        builder.withSpecificMutator(int.class, overrideIntMutator);

        // Then:
        final MutatorConfig mutatorConfig = builder.build();
        assertThat(mutatorConfig.getMutator(int.class), is(overrideIntMutator));
    }

    @Test
    public void shouldInstallCustomSpecificMutator() throws Exception {
        // Given:
        final Mutator customMutator = mock(Mutator.class, "custom");

        // When:
        builder.withSpecificMutator(MutatorConfig.class, customMutator);

        // Then:
        final MutatorConfig mutatorConfig = builder.build();
        assertThat(mutatorConfig.getMutator(MutatorConfig.class), is(customMutator));
    }

    @Test
    public void shouldOverrideExistingBaseMutator() throws Exception {
        // Given:
        final Mutator overrideSetMutator = mock(Mutator.class, "set - override");

        // When:
        builder.withBaseMutator(Set.class, overrideSetMutator);

        // Then:
        final MutatorConfig mutatorConfig = builder.build();
        assertThat(mutatorConfig.getMutator(HashSet.class), is(overrideSetMutator));
    }

    @Test
    public void shouldInstallCustomBaseMutator() throws Exception {
        // Given:
        final Mutator customMutator = mock(Mutator.class, "custom");

        // When:
        builder.withBaseMutator(MutatorConfig.Builder.class, customMutator);

        // Then:
        final MutatorConfig mutatorConfig = builder.build();
        assertThat(mutatorConfig.getMutator(MutatorConfigBuilder.class), is(customMutator));
    }

    @Test
    public void shouldInstallDefaultMutator() throws Exception {
        // Given:
        final Type unregisteredType = MutatorConfigBuilder.class;
        final Mutator customMutator = mock(Mutator.class, "custom");

        // When:
        builder.withDefaultMutator(customMutator);

        // Then:
        final MutatorConfig mutatorConfig = builder.build();
        assertThat(mutatorConfig.getMutator(unregisteredType), is(customMutator));
    }

    @Test
    public void shouldInstallDefaultArrayMutator() throws Exception {
        // Given:
        final Type arrayType = TypeUtils.genericArrayType(int.class);
        final Mutator customMutator = mock(Mutator.class, "custom");

        // When:
        builder.withDefaultArrayMutator(customMutator);

        // Then:
        final MutatorConfig mutatorConfig = builder.build();
        assertThat(mutatorConfig.getMutator(arrayType), is(customMutator));
    }

    private void givenOtherMutatorsExist() {
        builder.withSpecificMutator(String.class, mock(Mutator.class, "string"))
                .withBaseMutator(List.class, mock(Mutator.class, "lists"));
    }


}