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

import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;
import org.datalorax.populace.populator.mutator.change.ChangeStringMutator;
import org.datalorax.populace.populator.mutator.ensure.EnsureMutator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Andrew Coates - 27/02/2015.
 */
public class ArrayMutatorTest {
    private Mutator mutator;
    private PopulatorContext config;

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

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorContext.class);

        mutator = new ArrayMutator();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnUnsupportedType() throws Exception {
        mutator.mutate(Date.class, null, null, config);
    }

    @Test
    public void shouldMutateArray() throws Exception {
        // Given:
        givenStringMutatorRegistered();
        final GenericArrayType arrayType = TypeUtils.genericArrayType(String.class);
        final String[] original = new String[]{"dead", "parrot"};

        // When:
        final Object mutated = mutator.mutate(arrayType, Arrays.copyOf(original, original.length), null, config);

        // Then:
        assertThatArraySameSizeButDifferent(original, mutated);
    }

    @Test
    public void shouldCreateNewArrayIfCurrentIsNull() throws Exception {
        // Given:
        givenStringMutatorRegistered();
        final GenericArrayType arrayType = TypeUtils.genericArrayType(String.class);

        // When:
        final Object mutated = mutator.mutate(arrayType, null, null, config);

        // Then:
        assertThat("should be array", mutated.getClass().isArray(), is(true));
        assertThat("should be array of strings", mutated.getClass().getComponentType(), is((Type) String.class));
        assertThat("should not be empty array", ((String[]) mutated).length, is(not(0)));
        assertThat("should of changed element [0]", ((String[]) mutated)[0], is(not(nullValue())));
    }

    @Test
    public void shouldWorkWithMultiDimensionalArrays() throws Exception {
        // Given:
        givenStringMutatorRegistered();
        final String[][] original = new String[][]{new String[]{"dead", "parrot"}, new String[]{"a", "want", "a", "shrubbery"}};

        // When:
        final Object mutated = mutator.mutate(original.getClass(), deepCopy(original), null, config);

        // Then:
        assertThatArraySameSizeButDifferent(original, mutated);
    }

    private void givenStringMutatorRegistered() {
        when(config.createInstance(eq(String.class), anyObject())).thenReturn("shrubbery");
        when(config.getMutator(String.class)).thenReturn(Mutators.chain(EnsureMutator.INSTANCE, ChangeStringMutator.INSTANCE));
    }

    private void assertThatArraySameSizeButDifferent(Object original, Object mutated) {
        _assertThatArraySameSizeButDifferent(original, mutated, "");
    }

    private void _assertThatArraySameSizeButDifferent(Object original, Object mutated, String prefix) {
        assertThat(prefix + " should be of the same type", mutated.getClass(), is((Type) original.getClass()));
        assertThat(prefix + " should be array", mutated.getClass().isArray(), is(true));
        assertThat(prefix + " should be array of " + original.getClass().getComponentType(), mutated.getClass().getComponentType(), is((Type) original.getClass().getComponentType()));
        assertThat(prefix + " should not of changed length", Array.getLength(mutated), is(Array.getLength(original)));

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
}