package org.datalorax.populace.populator;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * @author datalorax - 25/02/2015.
 */
public class GraphPopulatorTest {
    private GraphPopulator populator;
    private PopulatorConfig config;
    private MutatorConfig mutators;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorConfig.class);
        mutators = mock(MutatorConfig.class);

        when(config.getMutatorConfig()).thenReturn(mutators);

        populator = new GraphPopulator(config);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowOnNullConfig() {
        new GraphPopulator(null);
    }

    @Test
    public void shouldPassTypeToMutatorWhenWorkingWithInstance() throws Exception {
        // Given:
        final Mutator mutator = givenMutatorRegistered(HashSet.class);

        // When:
        populator.populate(new HashSet<String>());

        // Then:
        verify(mutator).mutate(eq(HashSet.class), anyObject(), isA(PopulatorConfig.class));
    }

    @Test
    public void shouldPassInstanceToMutatorWhenWorkingWithInstance() throws Exception {
        // Given:
        final Mutator mutator = givenMutatorRegistered(HashSet.class);
        final HashSet<String> instance = new HashSet<String>();

        // When:
        populator.populate(instance);

        // Then:
        verify(mutator).mutate(isA(Type.class), eq(instance), isA(PopulatorConfig.class));
    }

    @Test
    public void shouldPassConfigToMutatorWhenWorkingWithInstance() throws Exception {
        // Given:
        final Mutator mutator = givenMutatorRegistered(HashSet.class);

        // When:
        populator.populate(new HashSet<String>());

        // Then:
        verify(mutator).mutate(isA(Type.class), anyObject(), eq(config));
    }

    @Test
    public void shouldPassTypeToMutatorWhenWorkingWithType() throws Exception {
        // Given:
        final Mutator mutator = givenMutatorRegistered(HashSet.class);

        // When:
        populator.populate(HashSet.class);

        // Then:
        verify(mutator).mutate(eq(HashSet.class), anyObject(), isA(PopulatorConfig.class));
    }

    @Test
    public void shouldPassNullToMutatorWhenWorkingWithType() throws Exception {
        // Given:
        final Mutator mutator = givenMutatorRegistered(HashMap.class);

        // When:
        populator.populate(HashMap.class);

        // Then:
        verify(mutator).mutate(isA(Type.class), eq(null), isA(PopulatorConfig.class));
    }

    @Test
    public void shouldPassConfigToMutatorWhenWorkingWithTyp() throws Exception {
        // Given:
        final Mutator mutator = givenMutatorRegistered(HashSet.class);

        // When:
        populator.populate(HashSet.class);

        // Then:
        verify(mutator).mutate(isA(Type.class), anyObject(), eq(config));
    }

    private Mutator givenMutatorRegistered(Type... types) {
        final Mutator mutator = mock(Mutator.class);
        for (Type type : types) {
            when(mutators.getMutator(type)).thenReturn(mutator);
        }
        return mutator;
    }
}