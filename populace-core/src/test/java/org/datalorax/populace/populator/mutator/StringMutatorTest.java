package org.datalorax.populace.populator.mutator;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorConfig;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

/**
 * @author datalorax - 27/02/2015.
 */
public class StringMutatorTest {
    private Mutator mutator;
    private PopulatorConfig config;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorConfig.class);
        mutator = new StringMutator();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnUnsupportedType() throws Exception {
        mutator.mutate(Integer.class, null, config);
    }

    @Test
    public void shouldMutateString() throws Exception {
        // Given:
        final String original = "hello";

        // When:
        final String populated = (String) mutator.mutate(String.class, original, config);

        // Then:
        assertThat(populated, is(not(original)));
    }

    @Test
    public void shouldCreateNewStringIfCurrentIsNull() throws Exception {
        // When:
        final String populated = (String) mutator.mutate(String.class, null, config);

        // Then:
        assertThat(populated, is(not(nullValue())));
    }
}