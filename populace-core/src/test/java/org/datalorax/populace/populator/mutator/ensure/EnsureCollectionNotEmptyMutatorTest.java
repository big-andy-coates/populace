package org.datalorax.populace.populator.mutator.ensure;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;
import org.datalorax.populace.populator.mutator.PassThroughMutator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

public class EnsureCollectionNotEmptyMutatorTest {
    private Mutator mutator;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorContext.class);

        mutator = new EnsureCollectionNotEmptyMutator();
    }

    @Test
    public void shouldWriteTests() throws Exception {
        // todo(ac):
        fail();
    }

    @Test
    public void shouldNotBlowUpOnRawTypes() throws Exception {
        // Given:
        givenMutatorRegistered(Object.class, PassThroughMutator.INSTANCE);
        final List currentValue = new ArrayList() {{
            //noinspection unchecked
            add("value");
        }};

        // When:
        mutator.mutate(List.class, currentValue, null, config);
    }

    private void givenMutatorRegistered(Class<?> type, Mutator mutator) {
        when(config.getMutator(type)).thenReturn(mutator);
    }

    private <T> void givenCreateInstanceWillReturn(final Class<T> type, final T instance) {
        when(config.createInstance(type, null)).thenReturn(instance);
    }
}