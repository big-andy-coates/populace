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

import org.datalorax.populace.core.populate.PopulatorContext;
import org.datalorax.populace.core.util.TypeUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class EnsureArrayElementsNotNullMutatorTest {
    private EnsureArrayElementsNotNullMutator mutator;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorContext.class);

        mutator = EnsureArrayElementsNotNullMutator.INSTANCE;
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnUnsupportedType() throws Exception {
        mutator.mutate(String.class, null, null, config);
    }

    @Test
    public void shouldReturnNullForNullInput() throws Exception {
        // When:
        final Object mutated = mutator.mutate(arrayType(String.class), null, null, config);

        // Then:
        assertThat(mutated, is(nullValue()));
    }

    @Test
    public void shouldJustReturnEmptyArray() throws Exception {
        // Given:
        final double[] currentValue = new double[]{};

        // When:
        final Object mutated = mutator.mutate(arrayType(double.class), currentValue, null, config);

        // Then:
        assertThat(mutated, is(sameInstance(currentValue)));
        assertThat(((double[]) mutated).length, is(0));
        verifyNoMoreInteractions(config);
    }

    @Test
    public void shouldDoNothingToArraysWithNoNullValues() throws Exception {
        // Given:
        final String[] currentValue = new String[]{"hello"};
        final String[] original = currentValue.clone();

        // When:
        final Object mutated = mutator.mutate(arrayType(String.class), currentValue, null, config);

        // Then:
        assertThat(mutated, is(sameInstance(currentValue)));
        assertThat(mutated, is(original));
        verifyNoMoreInteractions(config);
    }

    @Test
    public void shouldUseArrayTypeForCreateInstance() throws Exception {
        // Given:
        when(config.createInstance(any(Type.class), anyObject())).thenReturn("value");
        final String[] currentValue = new String[]{null};

        // When:
        mutator.mutate(arrayType(String.class), currentValue, null, config);

        // Then:
        verify(config).createInstance(eq(String.class), anyObject());
    }

    @Test
    public void shouldLeaveValueAsNullIfCreateInstanceReturnsNull() throws Exception {
        // Given:
        when(config.createInstance(any(Type.class), anyObject())).thenReturn(null);
        final Integer[] currentValue = new Integer[]{null};

        // When:
        final Integer[] mutated = (Integer[]) mutator.mutate(arrayType(Integer.class), currentValue, null, config);

        // Then:
        assertThat(mutated[0], is(nullValue()));
    }

    @Test
    public void shouldNotPassParentObjectToCreateInstanceAsItsNotTheParentOfTheComponent() throws Exception {
        // Given:
        final Object parent = new Object();
        final Integer[] currentValue = new Integer[]{null};

        // When:
        mutator.mutate(arrayType(Integer.class), currentValue, parent, config);

        // Then:
        verify(config).createInstance(any(Type.class), eq(null));
    }

    private static Type arrayType(final Class<?> componentType) {
        return TypeUtils.genericArrayType(componentType);
    }
}