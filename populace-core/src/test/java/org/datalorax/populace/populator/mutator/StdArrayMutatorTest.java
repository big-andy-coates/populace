package org.datalorax.populace.populator.mutator;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.MutatorConfig;
import org.datalorax.populace.populator.PopulatorConfig;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author datalorax - 27/02/2015.
 */
public class StdArrayMutatorTest {
    private Mutator mutator;
    private PopulatorConfig config;
    private MutatorConfig mutators;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorConfig.class);
        mutators = mock(MutatorConfig.class);

        when(config.getMutatorConfig()).thenReturn(mutators);

        mutator = new StdArrayMutator();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnUnsupportedType() throws Exception {
        mutator.mutate(Date.class, null, config);
    }

    @Test
    public void shouldMutateArray() throws Exception {
        // Given:
        givenStringMutatorRegistered();
        final GenericArrayType arrayType = TypeUtils.genericArrayType(String.class);
        final String[] original = new String[]{"dead", "parrot"};

        // When:
        final Object mutated = mutator.mutate(arrayType, Arrays.copyOf(original, original.length), config);

        // Then:
        assertThatArraySameSizeButDifferent(original, mutated);
    }

    @Test
    public void shouldCreateNewArrayIfCurrentIsNull() throws Exception {
        // Given:
        givenStringMutatorRegistered();
        final GenericArrayType arrayType = TypeUtils.genericArrayType(String.class);

        // When:
        final Object mutated = mutator.mutate(arrayType, null, config);

        // Then:
        assertThat("should be array", mutated.getClass().isArray(), is(true));
        assertThat("should be array of strings", mutated.getClass().getComponentType(), is((Type)String.class));
        assertThat("should of not of changed length", ((String[])mutated).length, is(not(0)));
        assertThat("should of changed element [0]", ((String[])mutated)[0], is(not(nullValue())));
    }

    @Test
    public void shouldWorkWithMultiDimensionalArrays() throws Exception {
        // Given:
        givenStringMutatorRegistered();
        final String[][] original = new String[][]{new String[]{"dead", "parrot"}, new String[]{"a", "want", "a", "shrubbery"}};

        // When:
        final Object mutated = mutator.mutate(original.getClass(), deepCopy(original), config);

        // Then:
        assertThatArraySameSizeButDifferent(original, mutated);
    }

    private void givenStringMutatorRegistered() {
        when(mutators.getMutator(String.class)).thenReturn(new StringMutator());
    }

    private void assertThatArraySameSizeButDifferent(Object original, Object mutated) {
        _assertThatArraySameSizeButDifferent(original, mutated, "");
    }

    private void _assertThatArraySameSizeButDifferent(Object original, Object mutated, String prefix) {
        assertThat(prefix + " should be of the same type", mutated.getClass(), is((Type)original.getClass()));
        assertThat(prefix + " should be array", mutated.getClass().isArray(), is(true));
        assertThat(prefix + " should be array of " + original.getClass().getComponentType(), mutated.getClass().getComponentType(), is((Type)original.getClass().getComponentType()));
        assertThat(prefix + " should of not of changed length", Array.getLength(mutated), is(Array.getLength(original)));

        for (int i = 0; i != Array.getLength(mutated); ++i) {
            Object o = Array.get(original, i);
            Object m = Array.get(mutated, i);

            if (o.getClass().isArray()) {
                _assertThatArraySameSizeButDifferent(o, m, prefix + '[' + i + ']');
            } else {
                assertThat(prefix + " should of changed", o, is(not(m)));
            }
        }
    }

    private static Object deepCopy(Object original) {
        if (original == null || !original.getClass().isArray()) {
            return original;
        }

        final int length = Array.getLength(original);
        final Object copy = Array.newInstance(original.getClass().getComponentType(), length);
        for (int i = 0; i != length; i++) {
            Array.set(copy, i, deepCopy(Array.get(original, i)));
        }
        return copy;
    }
}