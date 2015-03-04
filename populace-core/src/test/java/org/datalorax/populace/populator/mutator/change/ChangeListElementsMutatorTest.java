package org.datalorax.populace.populator.mutator.change;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;
import org.datalorax.populace.populator.mutator.PassThroughMutator;
import org.datalorax.populace.populator.mutator.StringMutator;
import org.datalorax.populace.populator.mutator.ensure.EnsureMutator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author datalorax 27/02/2015
 */
public class ChangeListElementsMutatorTest {
    private Mutator mutator;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorContext.class);

        mutator = new ChangeListElementsMutator();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnUnsupportedType() throws Exception {
        mutator.mutate(Date.class, null, null, config);
    }

    @Test
    public void shouldReturnNullForNullInput() throws Exception {
        // When:
        final Object mutated = mutator.mutate(List.class, null, null, config);

        // Then:
        assertThat(mutated, is(nullValue()));
    }

    @Test
    public void shouldNotBlowUpOnRawTypes() throws Exception {
        // Given:
        givenMutatorRegistered(Object.class, PassThroughMutator.INSTANCE);
        final List<String> currentValue = new ArrayList<String>() {{
            //noinspection unchecked
            add(null);
        }};

        // When:
        mutator.mutate(List.class, currentValue, null, config);
    }

    @Test
    public void shouldPassEachElementInListToComponentMutator() throws Exception {
        // Given:
        final Mutator componentMutator = mock(Mutator.class);
        final Type listType = TypeUtils.parameterize(List.class, String.class);
        givenMutatorRegistered(String.class, componentMutator);
        final List currentValue = new ArrayList<String>() {{
            add("value_1");
            add("value_2");
        }};

        // When:
        mutator.mutate(listType, currentValue, null, config);

        // Then:
        verify(componentMutator).mutate(String.class, "value_1", null, config);
        verify(componentMutator).mutate(String.class, "value_2", null, config);
    }

    @Test
    public void shouldNotPassParentObjectToComponentMutatorAsItsNotTheParentOfTheComponent() throws Exception {
        // Given:
        final Object parent = new Object();
        final Mutator componentMutator = mock(Mutator.class);
        final Type listType = TypeUtils.parameterize(List.class, String.class);
        givenMutatorRegistered(String.class, componentMutator);
        final List<String> currentValue = new ArrayList<String>() {{
            add("value1");
        }};

        // When:
        mutator.mutate(listType, currentValue, parent, config);

        // Then:
        verify(componentMutator).mutate(any(Class.class), anyObject(), eq(null), any(PopulatorContext.class));
    }

    @Test(enabled = false)  // Enable once InstanceFactory can handle Long
    public void shouldGetComponentMutatorUsingElementTypeWhenElementNoNull() throws Exception {
        // Given:
        givenMutatorRegistered(Long.class, EnsureMutator.INSTANCE);
        final Type baseListType = TypeUtils.parameterize(List.class, Number.class);
        final List<Long> currentValue = new ArrayList<Long>() {{
            add(1L);
        }};

        // When:
        mutator.mutate(baseListType, currentValue, null, config);

        // Then:
        verify(config).getMutator(Long.class);
    }

    @Test(enabled = false)  // Enable once InstanceFactory can handle Long
    public void shouldGetComponentMutatorUsingFieldTypeForNullElements() throws Exception {
        // Given:
        givenMutatorRegistered(Number.class, EnsureMutator.INSTANCE);
        final Type baseListType = TypeUtils.parameterize(List.class, Number.class);
        final List<Long> currentValue = new ArrayList<Long>() {{
            add(null);
        }};

        // When:
        mutator.mutate(baseListType, currentValue, null, config);

        // Then:
        verify(config).getMutator(Number.class);
    }

    @Test
    public void shouldPutResultFromComponentMutatorBackIntoList() throws Exception {
        // Given:
        givenMutatorRegistered(String.class, StringMutator.INSTANCE);
        final List<String> currentValue = new ArrayList<String>() {{
            add("initial_value");
        }};

        // When:
        mutator.mutate(List.class, currentValue, null, config);

        // Then:
        assertThat(currentValue, not(hasItem("initial_value")));
    }

    @Test
    public void shouldPassActualValueTypeToMutatorForNonNullValues() throws Exception {
        // Given:
        final Mutator componentMutator = mock(Mutator.class);
        givenMutatorRegistered(Long.class, componentMutator);
        final Type baseType = TypeUtils.parameterize(List.class, Number.class);
        final List<Long> currentValue = new ArrayList<Long>() {{
            add(1L);
        }};

        // When:
        mutator.mutate(baseType, currentValue, null, config);

        // Then:
        verify(componentMutator).mutate(eq(Long.class), anyObject(), anyObject(), any(PopulatorContext.class));
    }

    @Test
    public void shouldPassFieldValueTypeToMutatorForNullValues() throws Exception {
        // Given:
        final Mutator componentMutator = mock(Mutator.class);
        givenMutatorRegistered(Number.class, componentMutator);
        final Type baseType = TypeUtils.parameterize(List.class, Number.class);
        final List<Long> currentValue = new ArrayList<Long>() {{
            add(null);
        }};

        // When:
        mutator.mutate(baseType, currentValue, null, config);

        // Then:
        verify(componentMutator).mutate(eq(Number.class), anyObject(), anyObject(), any(PopulatorContext.class));
    }

    @Test
    public void shouldMutateInPlaceAndReturnIt() throws Exception {
        // Given:
        final List<String> currentValue = new ArrayList<String>();

        // When:
        final Object mutated = mutator.mutate(List.class, currentValue, null, config);

        // Then:
        assertThat(mutated, is(currentValue));
    }

    private void givenMutatorRegistered(Class<?> type, Mutator mutator) {
        when(config.getMutator(type)).thenReturn(mutator);
    }
}