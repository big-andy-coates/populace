package org.datalorax.populace.populator;

import org.datalorax.populace.populator.instance.InstanceFactories;
import org.datalorax.populace.populator.mutator.Mutators;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Field;

public class PopulatorContextTest {
    @Mock
    private Mutators mutators;
    @Mock
    private InstanceFactories instanceFactories;
    private Field field;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        field = getClass().getDeclaredField("field");

        config = new PopulatorContext(mutators, instanceFactories);
    }

    // Todo(ac): test
}