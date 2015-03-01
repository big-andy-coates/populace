package org.datalorax.populace.populator.mutator;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.datalorax.populace.populator.mutator.PrimitiveMutatorTest.TypeTrait.typeTrait;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

/**
 * @author datalorax - 26/02/2015.
 */
public class PrimitiveMutatorTest {
    private Mutator mutator;
    private PopulatorContext config;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorContext.class);
        mutator = new PrimitiveMutator();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnUnsupportedType() throws Exception {
        mutator.mutate(Date.class, null, config);
    }

    @Test(dataProvider = "primitives")
    public void shouldMutatePrimitives(TypeTrait<?> trait) throws Exception {
        // Given:
        final Object currentValue = trait.exampleValue;

        // When:
        final Object mutated = mutator.mutate(trait.type, currentValue, config);

        // Then:
        assertThat(mutated, is(instanceOf(trait.type)));
        assertThat(mutated, is(not(currentValue)));
    }

    @Test(dataProvider = "primitives")
    public void shouldMutatePrimitivesAtMinValue(TypeTrait<?> trait) throws Exception {
        // Given:
        final Object currentValue = trait.minValue;

        // When:
        final Object mutated = mutator.mutate(trait.type, currentValue, config);

        // Then:
        assertThat(mutated, is(instanceOf(trait.type)));
        assertThat(mutated, is(not(currentValue)));
    }

    @Test(dataProvider = "primitives")
    public void shouldMutatePrimitivesAtMaxValue(TypeTrait<?> trait) throws Exception {
        // Given:
        final Object currentValue = trait.maxValue;

        // When:
        final Object mutated = mutator.mutate(trait.type, currentValue, config);

        // Then:
        assertThat(mutated, is(instanceOf(trait.type)));
        assertThat(mutated, is(not(currentValue)));
    }

    @Test(dataProvider = "boxedPrimitives")
    public void shouldMutateBoxedPrimitives(TypeTrait<?> trait) throws Exception {
        // Given:
        final Object currentValue = trait.exampleValue;

        // When:
        final Object mutated = mutator.mutate(trait.type, currentValue, config);

        // Then:
        assertThat(mutated, is(instanceOf(trait.type)));
        assertThat(mutated, is(not(currentValue)));
    }

    @Test(dataProvider = "boxedPrimitives")
    public void shouldMutateNullBoxedPrimitives(TypeTrait<?> trait) throws Exception {
        // Given:
        final Object currentValue = null;

        // When:
        final Object mutated = mutator.mutate(trait.type, currentValue, config);

        // Then:
        assertThat(mutated, is(instanceOf(trait.type)));
        assertThat(mutated, is(not(nullValue())));
    }

    @Test(dataProvider = "boxedPrimitives")
    public void shouldMutateBoxedPrimitivesAtMinValue(TypeTrait<?> trait) throws Exception {
        // Given:
        final Object currentValue = trait.minValue;

        // When:
        final Object mutated = mutator.mutate(trait.type, currentValue, config);

        // Then:
        assertThat(mutated, is(instanceOf(trait.type)));
        assertThat(mutated, is(not(currentValue)));
    }

    @Test(dataProvider = "boxedPrimitives")
    public void shouldMutateBoxedPrimitivesAtMaxValue(TypeTrait<?> trait) throws Exception {
        // Given:
        final Object currentValue = trait.maxValue;

        // When:
        final Object mutated = mutator.mutate(trait.type, currentValue, config);

        // Then:
        assertThat(mutated, is(instanceOf(trait.type)));
        assertThat(mutated, is(not(currentValue)));
    }

    @DataProvider
    private Object[][] primitives() {
        Object[][] objects = new Object[PRIMITIVES.size()][];
        int index = 0;
        for (TypeTrait type : PRIMITIVES) {
            objects[index++] = new Object[]{type};
        }
        return objects;
    }

    @DataProvider
    private Object[][] boxedPrimitives() {
        Object[][] objects = new Object[BOXED_PRIMITIVES.size()][];
        int index = 0;
        for (TypeTrait type : BOXED_PRIMITIVES) {
            objects[index++] = new Object[]{type};
        }
        return objects;
    }

    public static class TypeTrait<T> {
        private final Class<T> type;
        private final T exampleValue;
        private final T minValue;
        private final T maxValue;

        private TypeTrait(Class<T> type, T exampleValue, T minValue, T maxValue) {
            this.type = type;
            this.exampleValue = exampleValue;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public static <TT> TypeTrait<TT> typeTrait(Class<TT> type, TT exampleValue, TT minValue, TT maxValue) {
            return new TypeTrait<TT>(type, exampleValue, minValue, maxValue);
        }
    }

    private static final List<TypeTrait<?>> PRIMITIVES = new ArrayList<TypeTrait<?>>() {{
        add(typeTrait(boolean.class, true, false, true));
        add(typeTrait(byte.class, (byte)9, Byte.MIN_VALUE, Byte.MAX_VALUE));
        add(typeTrait(char.class, 'a', Character.MIN_VALUE, Character.MAX_VALUE));
        add(typeTrait(int.class, 29, Integer.MIN_VALUE, Integer.MAX_VALUE));
        add(typeTrait(long.class, 29L, Long.MIN_VALUE, Long.MAX_VALUE));
        add(typeTrait(float.class, 2.9f, Float.MIN_VALUE, Float.MAX_VALUE));
        add(typeTrait(double.class, 2.9, Double.MIN_VALUE, Double.MAX_VALUE));
    }};

    private static final List<TypeTrait<?>> BOXED_PRIMITIVES = new ArrayList<TypeTrait<?>>() {{
        add(typeTrait(Boolean.class, true, false, true));
        add(typeTrait(Byte.class, (byte)9, Byte.MIN_VALUE, Byte.MAX_VALUE));
        add(typeTrait(Character.class, 'a', Character.MIN_VALUE, Character.MAX_VALUE));
        add(typeTrait(Integer.class, 29, Integer.MIN_VALUE, Integer.MAX_VALUE));
        add(typeTrait(Long.class, 29L, Long.MIN_VALUE, Long.MAX_VALUE));
        add(typeTrait(Float.class, 2.9f, Float.MIN_VALUE, Float.MAX_VALUE));
        add(typeTrait(Double.class, 2.9, Double.MIN_VALUE, Double.MAX_VALUE));
    }};
}