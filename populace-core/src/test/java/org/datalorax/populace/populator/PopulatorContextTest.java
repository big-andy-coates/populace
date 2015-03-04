package org.datalorax.populace.populator;

import org.datalorax.populace.populator.instance.InstanceFactories;
import org.datalorax.populace.typed.ImmutableTypeMap;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PopulatorContextTest {
    @Mock
    private ImmutableTypeMap<Mutator> mutators;
    @Mock
    private InstanceFactories instanceFactories;
    private Field field;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // Need to setup default for mutators and instanceFactories else constructor throws.
        when(mutators.getDefault()).thenReturn(mock(Mutator.class, "default"));
        when(mutators.getArrayDefault()).thenReturn(mock(Mutator.class, "array default"));

        field = getClass().getDeclaredField("field");

        config = new PopulatorContext(mutators, instanceFactories);
    }

    // Todo(ac): test
}