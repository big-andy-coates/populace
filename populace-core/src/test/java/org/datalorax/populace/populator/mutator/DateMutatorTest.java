package org.datalorax.populace.populator.mutator;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorConfig;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

/**
 * @author datalorax 26/02/2015
 */
public class DateMutatorTest {
    private Mutator mutator;
    private PopulatorConfig config;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorConfig.class);
        mutator = new DateMutator();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnUnsupportedType() throws Exception {
        mutator.mutate(String.class, null, config);
    }

    @Test
    public void shouldMutateDate() throws Exception {
        // Given:
        final Date original = new Date();

        // When:
        final Date populated = (Date) mutator.mutate(Date.class, new Date(original.getTime()), config);

        // Then:
        assertThat(populated, is(not(original)));
    }

    @Test
    public void shouldCreateNewDateIfCurrentIsNull() throws Exception {
        // When:
        final Date populated = (Date) mutator.mutate(Date.class, null, config);

        // Then:
        assertThat(populated, is(not(nullValue())));
    }
}