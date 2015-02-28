package org.datalorax.populace.populator;


import org.apache.commons.lang3.reflect.TypeUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

/**
 * @author datalorax - 27/02/2015.
 */
public class MutatorConfigTest {
    private static final Map<Type, Mutator> NO_SPECIFIC_MUTATORS = Collections.emptyMap();
    private static final Map<Class<?>, Mutator> NO_BASE_MUTATORS = Collections.emptyMap();

    private Mutator defaultMutator;
    private Mutator defaultArrayMutator;

    @BeforeMethod
    public void setUp() throws Exception {
        defaultMutator = mock(Mutator.class, "default");
        defaultArrayMutator = mock(Mutator.class, "default array");
    }

    @Test
    public void shouldGetSpecificOverDetailsArrayMutator() throws Exception {
        // Given:
        final Type setStringsType = TypeUtils.parameterize(HashSet.class, String.class);
        final Mutator specificMutator = mock(Mutator.class, "HashSet<String>");
        final Map<Type, Mutator> specificMutators = Collections.singletonMap(setStringsType, specificMutator);
        final MutatorConfig config = new MutatorConfig(specificMutators, NO_BASE_MUTATORS, defaultMutator, defaultArrayMutator);

        // When:
        final Mutator mutator = config.getMutator(setStringsType);

        // Then:
        assertThat(mutator, is(specificMutator));
    }

    @Test
    public void shouldGetDefaultArrayIfArrayTypeAndNoSpecificMutatorInstalled() throws Exception {
        // Given:
        final Type arrayOfNumbersType = TypeUtils.genericArrayType(Number.class);
        final Mutator specificMutator = mock(Mutator.class, "Number[]");
        final Map<Type, Mutator> specificMutators = Collections.singletonMap(arrayOfNumbersType, specificMutator);
        final MutatorConfig config = new MutatorConfig(specificMutators, NO_BASE_MUTATORS, defaultMutator, defaultArrayMutator);

        // When:
        final Mutator mutator = config.getMutator(TypeUtils.genericArrayType(Long.class));

        // Then:
        assertThat(mutator, is(defaultArrayMutator));
    }

    @Test
    public void shouldGetSpecificOverBaseMutator() throws Exception {
        // Given:
        final Type setStringsType = TypeUtils.parameterize(HashSet.class, String.class);
        final Mutator baseMutator = mock(Mutator.class, "Set");
        final Mutator specificMutator = mock(Mutator.class, "HashSet<String>");
        final Map<Class<?>, Mutator> baseMutators = Collections.<Class<?>, Mutator>singletonMap(Set.class, baseMutator);
        final Map<Type, Mutator> specificMutators = Collections.singletonMap(setStringsType, specificMutator);

        final MutatorConfig config = new MutatorConfig(specificMutators, baseMutators, defaultMutator, defaultArrayMutator);

        // When:
        final Mutator mutator = config.getMutator(setStringsType);

        // Then:
        assertThat(mutator, is(specificMutator));
    }

    @Test
    public void shouldPickMostSpecificBaseMutator() throws Exception {
        // Given:
        final Mutator setMutator = mock(Mutator.class, "Set");
        final Mutator collectionMutator = mock(Mutator.class, "Collection");
        final Map<Class<?>, Mutator> baseMutators = new TreeMap<Class<?>, Mutator>(new ClassComparator()) {{
            put(Collection.class, collectionMutator);
            put(Set.class, setMutator);
        }};
        final MutatorConfig config = new MutatorConfig(NO_SPECIFIC_MUTATORS, baseMutators, defaultMutator, defaultArrayMutator);
        final Type setStringsType = TypeUtils.parameterize(HashSet.class, String.class);

        // When:
        final Mutator mutator = config.getMutator(setStringsType);

        // Then:
        assertThat(mutator, is(setMutator));
    }

    @Test
    public void shouldPickMostSpecificBaseMutatorRegardlessOrOrder() throws Exception {
        // Given:
        final Mutator setMutator = mock(Mutator.class, "Set");
        final Mutator collectionMutator = mock(Mutator.class, "Collection");
        final Map<Class<?>, Mutator> baseMutators = new TreeMap<Class<?>, Mutator>(new ClassComparator().reversed()) {{
            put(Collection.class, collectionMutator);
            put(Set.class, setMutator);
        }};
        final MutatorConfig config = new MutatorConfig(NO_SPECIFIC_MUTATORS, baseMutators, defaultMutator, defaultArrayMutator);
        final Type setStringsType = TypeUtils.parameterize(HashSet.class, String.class);

        // When:
        final Mutator mutator = config.getMutator(setStringsType);

        // Then:
        assertThat(mutator, is(setMutator));
    }

    @Test
    public void shouldGetDefaultMutatorIfNotArrayTypeAndNothingElseMatches() throws Exception {
        // Given:
        final MutatorConfig config = new MutatorConfig(NO_SPECIFIC_MUTATORS, NO_BASE_MUTATORS, defaultMutator, defaultArrayMutator);

        // When:
        final Mutator mutator = config.getMutator(CustomType.class);

        // Then:
        assertThat(mutator, is(defaultMutator));
    }

    private static class ClassComparator implements Comparator<Class<?>> {
        @Override
        public int compare(Class<?> c1, Class<?> c2) {
            return c1.getCanonicalName().compareTo(c2.getCanonicalName());
        }
    }

    private static class CustomType {
    }
}