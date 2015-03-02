package org.datalorax.populace.populator.mutator;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author datalorax 27/02/2015
 */
public class ListMutatorTest {
    private Mutator mutator;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorContext.class);

        mutator = new ListMutator(ArrayList.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnUnsupportedType() throws Exception {
        mutator.mutate(Date.class, null, null, config);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowOnNullDefault() throws Exception {
        new ListMutator(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnDefaultThatHasNoDefaultConstructor() throws Exception {
        new ListMutator(ListTypeWithNoDefaultConstructor.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIfDefaultListTypeCantBeInstantiated() throws Exception {
        new ListMutator(ListTypeThatCantBeInstantiated.class);
    }

    @Test
    public void shouldPopulateNullListWithDefaultListType() throws Exception {
        // Given:
        givenStringMutatorRegistered();
        final Type listOfStringsType = TypeUtils.parameterize(List.class, String.class);

        // When:
        final Object mutated = mutator.mutate(listOfStringsType, null, null, config);

        // Then:
        assertThat(mutated, is(instanceOf(ArrayList.class)));
        assertThat((List<?>) mutated, is(not(empty())));
        assertThat(((List<?>) mutated).get(0), is(not(nullValue())));
        //noinspection unchecked
        assertThat(((List<String>) mutated).get(0), is(not("")));
    }

    @Test
    public void shouldPopulateNullListWithProvidedListType() throws Exception {
        // Given:
        givenStringMutatorRegistered();
        final Type stackOfStringsType = TypeUtils.parameterize(Stack.class, String.class);

        // When:
        final Object mutated = mutator.mutate(stackOfStringsType, null, null, config);

        // Then:
        assertThat(mutated, is(instanceOf(Stack.class)));
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldThrowOnNullListWithInterfaceTypeNotCompatibleWithDefaultType() throws Exception {
        // When:
        mutator.mutate(ListSubType.class, null, null, config);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldThrowOnNullListWithAbstractTypeNotSuperOfDefaultType() throws Exception {
        // When:
        mutator.mutate(ListTypeThatCantBeInstantiated.class, null, null, config);
    }

    @Test
    public void shouldPopulateEmptyList() throws Exception {
        // Given:
        givenStringMutatorRegistered();
        final Type listOfStringsType = TypeUtils.parameterize(List.class, String.class);

        // When:
        final Object mutated = mutator.mutate(listOfStringsType, new ArrayList<String>(), null, config);

        // Then:
        assertThat(mutated, is(instanceOf(ArrayList.class)));
        assertThat((List<?>) mutated, is(not(empty())));
        assertThat(((List<?>) mutated).get(0), is(not(nullValue())));
        //noinspection unchecked
        assertThat(((List<String>) mutated).get(0), is(not("")));
    }

    @Test
    public void shouldMutateExistingListElements() throws Exception {
        // Given:
        givenStringMutatorRegistered();
        final Type listOfStringsType = TypeUtils.parameterize(List.class, String.class);
        final ArrayList<String> currentValue = new ArrayList<String>() {{
            add("value");
        }};

        // When:
        final Object mutated = mutator.mutate(listOfStringsType, currentValue, null, config);

        // Then:
        assertThat(mutated, is(instanceOf(ArrayList.class)));
        assertThat((List<?>) mutated, is(not(empty())));
        assertThat(((List<?>) mutated).get(0), is(not(nullValue())));
        //noinspection unchecked
        assertThat(((List<String>) mutated).get(0), is(not("")));
        //noinspection unchecked
        assertThat(((List<String>) mutated).get(0), is(not("value")));
    }

    private void givenStringMutatorRegistered() {
        when(config.getMutator(String.class)).thenReturn(new StringMutator());
    }

    private abstract static class ListTypeWithNoDefaultConstructor implements List {
        @SuppressWarnings("UnusedParameters")
        public ListTypeWithNoDefaultConstructor(int i) {
        }
    }

    private abstract static class ListTypeThatCantBeInstantiated implements List {

    }

    private interface ListSubType extends List<String> {

    }
}