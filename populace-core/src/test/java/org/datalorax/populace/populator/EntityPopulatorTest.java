package org.datalorax.populace.populator;

import org.datalorax.populace.populator.mutator.PassThroughMutator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * @author datalorax - 25/02/2015.
 */
public class EntityPopulatorTest {
    private EntityPopulator populator;

    @BeforeMethod
    public void setUp() throws Exception {
        populator = new EntityPopulator.Builder().build();
    }

    @Test
    public void shouldHandlePrimitivesByDefault() throws Exception {
        // Given:
        final WithPrimitives original = new WithPrimitives();

        // When:
        final WithPrimitives populated = populator.populate(new WithPrimitives());

        // Then:
        assertThat(populated._boolean, is(not(original._boolean)));
        assertThat(populated._byte, is(not(original._byte)));
        assertThat(populated._char, is(not(original._char)));
        assertThat(populated._short, is(not(original._short)));
        assertThat(populated._int, is(not(original._int)));
        assertThat(populated._long, is(not(original._long)));
        assertThat(populated._float, is(not(original._float)));
        assertThat(populated._double, is(not(original._double)));
    }

    @Test
    public void shouldHandleBoxedPrimitivesByDefault() throws Exception {
        // Given:
        final WithBoxedPrimitives original = new WithBoxedPrimitives();

        // When:
        final WithBoxedPrimitives populated = populator.populate(new WithBoxedPrimitives());

        // Then:
        assertThat(populated._boolean, is(not(nullValue())));
        assertThat(populated._boolean, is(not(original._boolean)));
        assertThat(populated._byte, is(not(original._byte)));
        assertThat(populated._char, is(not(original._char)));
        assertThat(populated._short, is(not(original._short)));
        assertThat(populated._int, is(not(original._int)));
        assertThat(populated._long, is(not(original._long)));
        assertThat(populated._float, is(not(original._float)));
        assertThat(populated._double, is(not(original._double)));
    }

    @Test
    public void shouldHandleStringByDefault() throws Exception {
        // Given:
        final WithString original = new WithString();

        // When:
        final WithString populated = populator.populate(new WithString());

        // Then:
        assertThat(populated._string, is(not(nullValue())));
        assertThat(populated._string, is(not(original._string)));
    }

    @Test
    public void shouldHandleDatesByDefault() throws Exception {
        // Given:
        final WithDate original = new WithDate();

        // When:
        final WithDate populated = populator.populate(new WithDate());

        // Then:
        assertThat(populated._date, is(not(nullValue())));
        assertThat(populated._date, is(not(original._date)));
    }

    @Test
    public void shouldHandleArraysByDefault() throws Exception {
        // Given:
        final WithArray original = new WithArray();

        // When:
        final WithArray populated = populator.populate(new WithArray());

        // Then:
        assertThat(populated._array, is(not(nullValue())));
        assertThat(populated._array, is(not(original._array)));
    }

    @Test
    public void shouldHandleCollectionsByDefault() throws Exception {
        // Given:
        final WithCollections original = new WithCollections();

        // When:
        final WithCollections populated = populator.populate(new WithCollections());

        // Then:
        assertThat(populated._list, is(not(nullValue())));
        assertThat(populated._set, is(not(nullValue())));
        assertThat(populated._list, is(not(original._list)));
        assertThat(populated._set, is(not(original._set)));
    }

    @Test
    public void shouldHandleMapsByDefault() throws Exception {
        // Given:
        final WithMap original = new WithMap();

        // When:
        final WithMap populated = populator.populate(new WithMap());

        // Then:
        assertThat(populated._map, is(not(nullValue())));
        assertThat(populated._map, is(not(original._map)));
    }

    @Test
    public void shouldHandleContainersOfCustomTypes() throws Exception {
        // Given:
        final WithMapOfCustomType original = new WithMapOfCustomType();

        // When:
        final WithMapOfCustomType populated = populator.populate(new WithMapOfCustomType());

        // Then:
        assertThat(populated._map, is(not(nullValue())));
        assertThat(populated._map, is(not(original._map)));
    }

    @Test
    public void shouldHandleNestedObjects() throws Exception {
        // Given:
        final WithNestedObject original = new WithNestedObject();

        // When:
        final WithNestedObject populated = populator.populate(new WithNestedObject());

        // Then:
        assertThat(populated._nestedType._int, is(not(original._nestedType._int)));
    }

    @Test
    public void shouldHandleNullNestedObjects() throws Exception {
        // Given:
        final WithNestedObject currentValue = new WithNestedObject();
        currentValue._nestedType = null;

        // When:
        final WithNestedObject populated = populator.populate(currentValue);

        // Then:
        assertThat(populated._nestedType, is(not(nullValue())));
    }

    @Test
    public void shouldHandleNonCustomTypesAsMainArgument() throws Exception {
        // When:
        final String populated = populator.populate("bob");

        // Then:
        assertThat(populated, is(not("bob")));
    }

    @Test
    public void shouldHandlePopulateCallWithJustTheType() throws Exception {
        // When:
        final WithNestedObject populated = populator.populate(WithNestedObject.class);

        // Then:
        assertThat(populated._nestedType, is(not(nullValue())));
    }

    // Todo(ac): Add hook for Object / generic types without parameters

    @Test
    public void shouldHonourFieldExclusionList() throws Exception {
        // Given:
        final WithBoxedPrimitives original = new WithBoxedPrimitives();
        populator = new EntityPopulator.Builder().withFieldExclusions(new HashSet<String>() {{
            add("_char");
            add("_int");
        }}).build();

        // When:
        final WithPrimitives populated = populator.populate(new WithPrimitives());

        // Then:
        assertThat(populated._char, is(original._char));
        assertThat(populated._int, is(original._int));
    }

    @Test
    public void shouldWorkWithFinalFields() throws Exception {
        // Given:
        final WithFinalField original = new WithFinalField();

        // When:
        final WithFinalField populated = populator.populate(new WithFinalField());

        // Then:
        final long _finalValue = (Long) WithFinalField.class.getField("_final").get(populated); // Must use reflection to get around compiler optimisation of final fields
        assertThat(_finalValue, is(not(original._final)));
    }

    @Test
    public void shouldIgnoreTransientFields() throws Exception {
        // Given:
        final WithTransientField original = new WithTransientField();

        // When:
        final WithTransientField populated = populator.populate(new WithTransientField());

        // Then:
        assertThat(populated._transient, is(original._transient));
    }

    @Test
    public void shouldIgnoreStaticFields() throws Exception {
        // Given:
        final long original = WithStaticField._static;

        // When:
        populator.populate(new WithStaticField());

        // Then:
        assertThat(WithStaticField._static, is(original));
    }

    @Test
    public void shouldUseCustomMutators() throws Exception {
        // Given:
        final Mutator mutator = givenMutatorRegistered(int.class);

        // When:
        populator.populate(new WithPrimitives());

        // Then:
        verify(mutator).mutate(eq(int.class), eq(2), isA(PopulatorConfig.class));
    }

    private Mutator givenMutatorRegistered(Type... types) {
        final Mutator mutator = spy(PassThroughMutator.class);
        final EntityPopulator.Builder builder = EntityPopulator.newBuilder();
        for (Type type : types) {
            builder.withSpecificMutator(type, mutator);
        }
        populator = builder.build();
        return mutator;
    }

    private static class WithPrimitives {
        private boolean _boolean = false;
        private byte _byte = 9;
        private char _char = 'a';
        private short _short = 1;
        private int _int = 2;
        private long _long = 3L;
        private float _float = 1.2f;
        private double _double = 1.2;
    }

    private static class WithBoxedPrimitives {
        private Boolean _boolean = false;
        private Byte _byte = 9;
        private Character _char = 'a';
        private Short _short = 1;
        private Integer _int = 2;
        private Long _long = 3L;
        private Float _float = 1.2f;
        private Double _double = 1.2;
    }

    private static class WithString {
        public String _string = "someString";
    }

    private static class WithDate {
        public Date _date = new Date();
    }

    private static class WithArray {
        public int[] _array = new int[]{1, 2, 3};
    }

    private static class WithPrivateConstructor {
        private int _int = 42;
        private WithPrivateConstructor() {
        }
    }

    private static class WithNestedObject {
        private WithPrivateConstructor _nestedType = new WithPrivateConstructor();
    }

    private static class WithFinalField {
        public final long _final = 9L;
    }

    private static class WithTransientField {
        public transient long _transient = 9L;
    }

    private static class WithStaticField {
        public static long _static = 9L;
    }

    private static class WithCollections {
        public List<String> _list = new ArrayList<String>() {{
            add("this");
        }};
        public Set<Long> _set = new HashSet<Long>() {{
            add(42L);
        }};
    }

    private static class WithMap {
        public Map<String, Integer> _map = new HashMap<String, Integer>() {{
            put("this", 42);
        }};
    }

    private static class WithMapOfCustomType {
        public Map<String, WithString> _map = new HashMap<String, WithString>() {{
            put("this", new WithString());
        }};
    }
}