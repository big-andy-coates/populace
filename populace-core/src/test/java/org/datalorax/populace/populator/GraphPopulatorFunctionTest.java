/*
 * Copyright (c) 2015 Andrew Coates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.datalorax.populace.populator;

import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.populator.instance.InstanceFactory;
import org.datalorax.populace.populator.mutator.Mutators;
import org.datalorax.populace.populator.mutator.PassThroughMutator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * @author Andrew Coates - 25/02/2015.
 */
public class GraphPopulatorFunctionTest {
    private GraphPopulator populator;

    @BeforeMethod
    public void setUp() throws Exception {
        populator = GraphPopulator.newBuilder().build();
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
        assertThat(populated._collection, is(not(nullValue())));
        assertThat(populated._list, is(not(original._list)));
        assertThat(populated._set, is(not(original._set)));
        assertThat(populated._collection, is(not(original._collection)));
        assertThat(populated._nullList, is(not(nullValue())));
        assertThat(populated._nullSet, is(not(nullValue())));
        assertThat(populated._nullCollection, is(not(empty())));
        assertThat(populated._nullList, is(not(empty())));
        assertThat(populated._nullSet, is(not(empty())));
        assertThat(populated._nullCollection, is(not(empty())));
    }

    @Test
    public void shouldHandleMapsByDefault() throws Exception {
        // Given:
        final TypeWithMapField original = new TypeWithMapField();

        // When:
        final TypeWithMapField populated = populator.populate(new TypeWithMapField());

        // Then:
        assertThat(populated._map, is(not(nullValue())));
        assertThat(populated._map, is(not(original._map)));
        assertThat(populated._nullMap, is(not(nullValue())));
    }

    @Test
    public void shouldHandleEnumsByDefault() throws Exception {
        // Given:
        final TypeWithEnumField original = new TypeWithEnumField();

        // When:
        final TypeWithEnumField populated = populator.populate(new TypeWithEnumField());

        // Then:
        assertThat(populated._enum, is(not(nullValue())));
        assertThat(populated._enum, is(not(original._enum)));
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
        final TypeWithNestedObject original = new TypeWithNestedObject();

        // When:
        final TypeWithNestedObject populated = populator.populate(new TypeWithNestedObject());

        // Then:
        assertThat(populated._nestedType._int, is(not(original._nestedType._int)));
    }

    @Test
    public void shouldHandleNullNestedObjects() throws Exception {
        // Given:
        final TypeWithNestedObject currentValue = new TypeWithNestedObject();
        currentValue._nestedType = null;

        // When:
        final TypeWithNestedObject populated = populator.populate(currentValue);

        // Then:
        assertThat(populated._nestedType, is(not(nullValue())));
    }

    @Test
    public void shouldHandleTypeVariables() throws Exception {
        // Given:
        final TypeWrappingTypeWithTypeVariables currentValue = new TypeWrappingTypeWithTypeVariables();

        // When:
        final TypeWrappingTypeWithTypeVariables populated = populator.populate(currentValue);

        // Then:
        assertThat(populated._type._map.values(), not(hasItem(nullValue())));
    }

    @Test
    public void shouldHandleNullObjectFieldsByDefault() throws Exception {
        // Given:
        final TypeWithObjectField currentValue = new TypeWithObjectField();

        // When:
        final TypeWithObjectField populated = populator.populate(currentValue);

        // Then:
        assertThat("default strategy for null Object fields should be to leave them null", populated._null, is(nullValue()));
    }

    @Test
    public void shouldAllowCustomNullObjectHandling() throws Exception {
        // Given:
        final InstanceFactory nullHandler = mock(InstanceFactory.class);
        final TypeWithObjectField currentValue = new TypeWithObjectField();
        final GraphPopulator.Builder builder = GraphPopulator.newBuilder();
        populator = builder.withInstanceFactories(builder
            .instanceFactoriesBuilder()
            .withNullObjectFactory(nullHandler).build())
            .build();

        // When:
        populator.populate(currentValue);

        // Then:
        verify(nullHandler).createInstance(Object.class, currentValue);
    }

    @Test
    public void shouldHandlePopulateCallWithJustTheType() throws Exception {
        // When:
        final TypeWithNestedObject populated = populator.populate(TypeWithNestedObject.class);

        // Then:
        assertThat(populated._nestedType, is(not(nullValue())));
    }

    @Test
    public void shouldWorkWithFinalFields() throws Exception {
        // Given:
        final TypeWithFinalField original = new TypeWithFinalField();

        // When:
        final TypeWithFinalField populated = populator.populate(new TypeWithFinalField());

        // Then:
        final long _finalValue = (Long) TypeWithFinalField.class.getField("_final").get(populated); // Must use reflection to get around compiler optimisation of final fields
        assertThat(_finalValue, is(not(original._final)));
    }

    @Test
    public void shouldIgnoreTransientFieldsByDefault() throws Exception {
        // Given:
        final TypeWithTransientField original = new TypeWithTransientField();

        // When:
        final TypeWithTransientField populated = populator.populate(new TypeWithTransientField());

        // Then:
        assertThat(populated._transient, is(original._transient));
    }

    @Test
    public void shouldIgnoreStaticFieldsByDefault() throws Exception {
        // Given:
        final long original = TypeWithStaticField._static;

        // When:
        populator.populate(new TypeWithStaticField());

        // Then:
        assertThat(TypeWithStaticField._static, is(original));
    }

    @Test
    public void shouldUseCustomMutators() throws Exception {
        // Given:
        final Mutator mutator = givenMutatorRegistered(int.class);

        // When:
        populator.populate(new WithPrimitives());

        // Then:
        verify(mutator).mutate(eq(int.class), eq(2), anyObject(), isA(PopulatorContext.class));
    }

    @Test
    public void shouldHonourFieldFilterList() throws Exception {
        // Given:
        final WithBoxedPrimitives original = new WithBoxedPrimitives();
        populator = GraphPopulator.newBuilder().withFieldFilter(new FieldFilter() {
            @Override
            public boolean evaluate(final Field field) {
                final String name = field.getName();
                return !(name.equals("_char") || name.equals("_int"));
            }
        }).build();

        // When:
        final WithPrimitives populated = populator.populate(new WithPrimitives());

        // Then:
        assertThat(populated._char, is(original._char));
        assertThat(populated._int, is(original._int));
    }

    @Test
    public void shouldWorkWithRawGenericTypes() throws Exception {
        // Given:
        final WithRawGenericType original = new WithRawGenericType();

        // When:
        final WithRawGenericType populated = populator.populate(new WithRawGenericType());

        // Then:
        assertThat(populated._rawList, is(notNullValue()));
        assertThat(populated._rawList, is(not(original._rawList)));
    }

    // Todo(ac): Add tests to ensure we're not mutating any field more than once - think arrays, collections, etc.
    // Todo(ac): Add test with deep object graph (may have issues with stack overflow)

    private Mutator givenMutatorRegistered(Type... types) {
        final Mutator mutator = spy(PassThroughMutator.class);
        final Mutators.Builder builder = Mutators.newBuilder();
        for (Type type : types) {
            builder.withSpecificMutator(type, mutator);
        }
        populator = GraphPopulator.newBuilder().withMutators(builder.build()).build();
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

    private static class TypeWithNestedObject {
        public WithPrivateConstructor _nestedType = new WithPrivateConstructor();
    }

    private static class TypeWithFinalField {
        public final long _final = 9L;
    }

    private static class TypeWithTransientField {
        public transient long _transient = 9L;
    }

    private static class TypeWithStaticField {
        public static long _static = 9L;
    }

    private static class WithCollections {
        public List<String> _nullList = null;
        public List<String> _list = new ArrayList<String>() {{
            add("this");
        }};
        public Set<Long> _nullSet = null;
        public Set<Long> _set = new HashSet<Long>() {{
            add(42L);
        }};
        public Collection<Long> _nullCollection = null;
        public Collection<Long> _collection = new ArrayList<Long>() {{
            add(42L);
        }};
    }

    private static class TypeWithMapField {
        public Map<String, Integer> _nullMap = null;
        public Map<String, Integer> _map = new HashMap<String, Integer>() {{
            put("this", 42);
        }};
    }

    private static class TypeWithEnumField {
        @SuppressWarnings("UnusedDeclaration")
        public enum SomeEnum {
            forkHandles, fourCandles
        }

        public SomeEnum _enum;
    }

    private static class WithMapOfCustomType {
        public Map<String, WithString> _map = new HashMap<String, WithString>() {{
            put("this", new WithString());
        }};
    }

    private static class WithRawGenericType {
        public List _rawList = new ArrayList() {{
            //noinspection unchecked
            add("something");
        }};
    }

    public static class TypeWrappingTypeWithTypeVariables {
        public TypeWithTypeVariables<String, Integer> _type = new TypeWithTypeVariables<String, Integer>();

        public TypeWrappingTypeWithTypeVariables() {
            _type._map.put("key", null);
        }
    }

    public static class TypeWithTypeVariables<K, V> {
        public Map<K, V> _map = new HashMap<K, V>();
    }

    public static class TypeWithObjectField {
        public Object _null;
    }
}

// Todo(Ac): Add test for Map<String, List<Integer>>