package org.datalorax.populace.populator;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;

public class GraphPopulatorBuilderTest {
    private GraphPopulatorBuilder builder;

    @BeforeMethod
    public void setUp() throws Exception {
        builder = new GraphPopulatorBuilder();
    }

    @Test
    public void shouldInstallSpecificMutator() throws Exception {
        // Todo(ac):
        fail();
    }

}