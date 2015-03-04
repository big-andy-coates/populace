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
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ChangeMapValuesMutatorTest {
    private Mutator mutator;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorContext.class);

        mutator = ChangeMapValuesMutator.INSTANCE;
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnUnsupportedType() throws Exception {
        mutator.mutate(String.class, null, null, config);
    }

    @Test
    public void shouldReturnNullForNullInput() throws Exception {
        // When:
        final Object mutated = mutator.mutate(Map.class, null, null, config);

        // Then:
        assertThat(mutated, is(nullValue()));
    }

    @Test
    public void shouldNotBlowUpOnRawTypes() throws Exception {
        // Given:
        givenMutatorRegistered(Object.class, PassThroughMutator.INSTANCE);
        final Map currentValue = new HashMap<String, String>() {{
            put("key", null);
        }};

        // When:
        mutator.mutate(Map.class, currentValue, null, config);
    }

    @Test
    public void shouldPassEachValueInMapToComponentMutator() throws Exception {
        // Given:
        final Mutator componentMutator = mock(Mutator.class);
        final Type mapType = TypeUtils.parameterize(Map.class, Integer.class, String.class);
        givenMutatorRegistered(String.class, componentMutator);
        final Map currentValue = new HashMap<Integer, String>() {{
            put(1, "value_1");
            put(2, "value_2");
        }};

        // When:
        mutator.mutate(mapType, currentValue, null, config);

        // Then:
        verify(componentMutator).mutate(String.class, "value_1", null, config);
        verify(componentMutator).mutate(String.class, "value_2", null, config);
    }

    @Test
    public void shouldNotPassParentObjectToComponentMutatorAsItsNotTheParentOfTheComponent() throws Exception {
        // Given:
        final Object parent = new Object();
        final Mutator componentMutator = mock(Mutator.class);
        final Type mapType = TypeUtils.parameterize(Map.class, Integer.class, String.class);
        givenMutatorRegistered(String.class, componentMutator);
        final Map<Integer, String> currentValue = new HashMap<Integer, String>() {{
            put(1, "value1");
        }};

        // When:
        mutator.mutate(mapType, currentValue, parent, config);

        // Then:
        verify(componentMutator).mutate(any(Class.class), anyObject(), eq(null), any(PopulatorContext.class));
    }

    @Test
    public void shouldGetComponentMutatorUsingValueTypeWhenElementNoNull() throws Exception {
        // Given:
        givenMutatorRegistered(Long.class, EnsureMutator.INSTANCE);
        final Type baseType = TypeUtils.parameterize(Map.class, String.class, Number.class);
        final Map<String, Long> currentValue = new HashMap<String, Long>() {{
            put("key", 1L);
        }};

        // When:
        mutator.mutate(baseType, currentValue, null, config);

        // Then:
        verify(config).getMutator(Long.class);
    }

    @Test(enabled = false)  // Enable once InstanceFactory can handle Long
    public void shouldGetComponentMutatorUsingFieldTypeForNullValues() throws Exception {
        // Given:
        givenMutatorRegistered(Number.class, EnsureMutator.INSTANCE);
        final Type baseType = TypeUtils.parameterize(Map.class, String.class, Number.class);
        final Map<String, Long> currentValue = new HashMap<String, Long>() {{
            put("key", null);
        }};

        // When:
        mutator.mutate(baseType, currentValue, null, config);

        // Then:
        verify(config).getMutator(Number.class);
    }

    @Test
    public void shouldPassActualValueTypeToMutatorForNonNullValues() throws Exception {
        // Given:
        final Mutator componentMutator = mock(Mutator.class);
        givenMutatorRegistered(Long.class, componentMutator);
        final Type baseType = TypeUtils.parameterize(Map.class, String.class, Number.class);
        final Map<String, Long> currentValue = new HashMap<String, Long>() {{
            put("key", 1L);
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
        final Type baseType = TypeUtils.parameterize(Map.class, String.class, Number.class);
        final Map<String, Long> currentValue = new HashMap<String, Long>() {{
            put("key", null);
        }};

        // When:
        mutator.mutate(baseType, currentValue, null, config);

        // Then:
        verify(componentMutator).mutate(eq(Number.class), anyObject(), anyObject(), any(PopulatorContext.class));
    }

    @Test
    public void shouldPutResultFromComponentMutatorBackIntoMapValue() throws Exception {
        // Given:
        givenMutatorRegistered(String.class, StringMutator.INSTANCE);
        final Map<Integer, String> currentValue = new HashMap<Integer, String>() {{
            put(1, "initial_value");
        }};

        // When:
        mutator.mutate(Map.class, currentValue, null, config);

        // Then:
        assertThat(currentValue.values(), not(hasItem("initial_value")));
    }

    @Test
    public void shouldMutateSetInPlaceAndReturnIt() throws Exception {
        // Given:
        final Map<Integer, String> currentValue = new HashMap<Integer, String>();

        // When:
        final Object mutated = mutator.mutate(Map.class, currentValue, null, config);

        // Then:
        assertThat(mutated, is(currentValue));
    }

    private void givenMutatorRegistered(Class<?> type, Mutator mutator) {
        when(config.getMutator(type)).thenReturn(mutator);
    }
}