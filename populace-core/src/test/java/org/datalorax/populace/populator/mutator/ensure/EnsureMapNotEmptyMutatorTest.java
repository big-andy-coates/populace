package org.datalorax.populace.populator.mutator.ensure;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;
import org.datalorax.populace.populator.mutator.PassThroughMutator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

public class EnsureMapNotEmptyMutatorTest {
    private Mutator mutator;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorContext.class);

        mutator = EnsureMapNotEmptyMutator.INSTANCE;
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
           put("value", null);
        }};

        // When:
        mutator.mutate(Map.class, currentValue, null, config);
    }

    @Test
    public void shouldWriteTests() throws Exception {
        // todo(ac):
        fail();
    }

    private void givenMutatorRegistered(Class<?> type, Mutator mutator) {
        when(config.getMutator(type)).thenReturn(mutator);
    }
}