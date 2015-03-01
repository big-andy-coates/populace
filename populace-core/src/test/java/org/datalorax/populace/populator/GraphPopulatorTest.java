package org.datalorax.populace.populator;

import org.datalorax.populace.field.visitor.FieldVisitor;
import org.datalorax.populace.graph.GraphWalker;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.fail;

/**
 * @author datalorax - 25/02/2015.
 */
public class GraphPopulatorTest {
    private GraphPopulator populator;
    private PopulatorContext config;
    private GraphWalker walker;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorContext.class);
        walker = mock(GraphWalker.class);

        populator = new GraphPopulator(walker, config);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowOnNullConfig() {
        new GraphPopulator(null, null);
    }

    @Test
    public void shouldPassInstanceToWalkerWhenWorkingWithInstance() throws Exception {
        // Given:
        final Object instance = new Object();

        // When:
        populator.populate(instance);

        // Then:
        verify(walker).walk(eq(instance), any(FieldVisitor.class));
    }

    @Test
    public void shouldTest() throws Exception {
        fail();
    }

    private Mutator givenMutatorRegistered(Type... types) {
        final Mutator mutator = mock(Mutator.class);
        for (Type type : types) {
            when(config.getMutator(type)).thenReturn(mutator);
        }
        return mutator;
    }
}