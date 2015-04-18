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

package org.datalorax.populace.core.populate;

import org.datalorax.populace.core.CustomCollection;
import org.datalorax.populace.core.populate.instance.InstanceFactories;
import org.datalorax.populace.core.populate.instance.InstanceFactory;
import org.datalorax.populace.core.populate.instance.NullObjectStrategy;
import org.datalorax.populace.core.populate.mutator.Mutators;
import org.datalorax.populace.core.populate.mutator.NoOpMutator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
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
        class WithPrimitives {
            private boolean _boolean = false;
            private byte _byte = 9;
            private char _char = 'a';
            private short _short = 1;
            private int _int = 2;
            private long _long = 3L;
            private float _float = 1.2f;
            private double _double = 1.2;
        }

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
    public void shouldNullHandleBoxedPrimitivesByDefault() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithBoxedPrimitives {
            private Boolean _boolean;
            private Byte _byte;
            private Character _char;
            private Short _short;
            private Integer _int;
            private Long _long;
            private Float _float;
            private Double _double;
        }

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
    public void shouldHandleNullBoxedPrimitivesByDefault() throws Exception {
        // Given:
        class WithBoxedPrimitives {
            private Boolean _boolean = false;
            private Byte _byte = 9;
            private Character _char = 'a';
            private Short _short = 1;
            private Integer _int = 2;
            private Long _long = 3L;
            private Float _float = 1.2f;
            private Double _double = 1.2;
        }

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
        class WithString {
            public String _string = "someString";
        }

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
        class WithDate {
            public Date _date = new Date();
        }

        final WithDate original = new WithDate();

        // When:
        final WithDate populated = populator.populate(new WithDate());

        // Then:
        assertThat(populated._date, is(not(nullValue())));
        assertThat(populated._date, is(not(original._date)));
    }

    @Test
    public void shouldHandleArraysOfMutableTypes() throws Exception {
        // Given:
        class WithArray {
            public TypeThatCanBeMutated[] _array = new TypeThatCanBeMutated[]{
                new TypeThatCanBeMutated("1"),
                new TypeThatCanBeMutated("2"),
                new TypeThatCanBeMutated("3")
            };
        }

        final WithArray original = new WithArray();

        // When:
        final WithArray populated = populator.populate(new WithArray());

        // Then:
        assertThat(populated._array, is(not(nullValue())));
        assertThat(populated._array, is(not(original._array)));
    }

    @Test
    public void shouldHandleArraysOfImmutableTypes() throws Exception {
        // Given:
        class WithArray {
            public String[] _arrayOfTerminal = new String[]{"1", "2", "3"};
        }

        final WithArray original = new WithArray();

        // When:
        final WithArray populated = populator.populate(new WithArray());

        // Then:
        assertThat(populated._arrayOfTerminal, is(not(nullValue())));
        assertThat(populated._arrayOfTerminal, is(not(original._arrayOfTerminal)));
    }

    @Test
    public void shouldHandleNullArrays() throws Exception {
        // Given:
        class WithArray {
            public String[] _nullArray;
        }

        // When:
        final WithArray populated = populator.populate(new WithArray());

        // Then:
        assertThat(populated._nullArray, is(not(nullValue())));
        assertThat(populated._nullArray.length, is(not(0)));
    }

    @Test
    public void shouldHandleArraysWithNulls() throws Exception {
        // Given:
        class WithArray {
            public TypeThatCanBeMutated[] _arrayWithNull = new TypeThatCanBeMutated[]{null};
        }

        final WithArray original = new WithArray();

        // When:
        final WithArray populated = populator.populate(new WithArray());

        // Then:
        assertThat(populated._arrayWithNull, is(not(nullValue())));
        assertThat(populated._arrayWithNull, is(not(original._arrayWithNull)));
    }

    @Test
    public void shouldHandleArraysWithJustRuntimeTimeInfo() throws Exception {
        // Given:
        class WithArray {
            public Object _arrayWithNull = new TypeThatCanBeMutated[]{new TypeThatCanBeMutated("1")};
        }

        final WithArray original = new WithArray();

        // When:
        final WithArray populated = populator.populate(new WithArray());

        // Then:
        assertThat(populated._arrayWithNull, is(not(nullValue())));
        assertThat(populated._arrayWithNull, is(not(original._arrayWithNull)));
    }

    @Test
    public void shouldHandleCollectionsOfMutableTypes() throws Exception {
        // Given:
        class WithCollections {
            public Collection<TypeThatCanBeMutated> _collection = new ArrayList<TypeThatCanBeMutated>() {{
                add(new TypeThatCanBeMutated());
            }};
        }

        final WithCollections original = new WithCollections();

        // When:
        final WithCollections populated = populator.populate(new WithCollections());

        // Then:
        assertThat(populated._collection, is(not(nullValue())));
        assertThat(populated._collection, is(not(original._collection)));
    }

    @Test
    public void shouldNotBlowUpOnCollectionsOfImmutableTypes() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithCollections {
            public Collection<String> _collectionTerminalType = new CustomCollection<String>() {{
                add("value");
            }};
        }

        // When:
        populator.populate(new WithCollections());

        // Then:
        // Don't blow Up!
    }

    @Test
    public void shouldHandleNullCollections() throws Exception {
        // Given:
        class WithCollections {
            public Collection<TypeThatCanBeMutated> _nullCollection = null;
        }

        // When:
        final WithCollections populated = populator.populate(new WithCollections());

        // Then:
        assertThat(populated._nullCollection, is(not(empty())));
    }

    @Test
    public void shouldHandleEmptyCollectionsOfMutableTypes() throws Exception {
        // Given:
        class WithCollections {
            public Collection<TypeThatCanBeMutated> _collectionWithNull = new CustomCollection<>();
        }

        final WithCollections original = new WithCollections();

        // When:
        final WithCollections populated = populator.populate(new WithCollections());

        // Then:
        assertThat(populated._collectionWithNull, is(not(nullValue())));
        assertThat(populated._collectionWithNull, is(not(original._collectionWithNull)));
    }

    @Test
    public void shouldHandleEmptyCollectionsOfImmutableTypes() throws Exception {
        // Given:
        class WithCollections {
            public Collection<TypeThatCanBeMutated> _collectionWithNull = new CustomCollection<>();
        }

        final WithCollections original = new WithCollections();

        // When:
        final WithCollections populated = populator.populate(new WithCollections());

        // Then:
        assertThat(populated._collectionWithNull, is(not(nullValue())));
        assertThat(populated._collectionWithNull, is(not(original._collectionWithNull)));
    }

    @Test
    public void shouldHandleCollectionWithNulls() throws Exception {
        // Given:
        class WithCollections {
            public Collection<TypeThatCanBeMutated> _collectionWithNull = new CustomCollection<TypeThatCanBeMutated>() {{
                add(null);
            }};
        }

        final WithCollections original = new WithCollections();

        // When:
        final WithCollections populated = populator.populate(new WithCollections());

        // Then:
        assertThat(populated._collectionWithNull, is(not(nullValue())));
        assertThat(populated._collectionWithNull, is(not(original._collectionWithNull)));
    }

    @Test
    public void shouldHandleCollectionWithJustRuntimeTypeInfo() throws Exception {
        // Given:
        class WithCollections {
            public Object _collection = new CustomCollection<TypeThatCanBeMutated>() {{
                add(new TypeThatCanBeMutated());
            }};
        }

        final WithCollections original = new WithCollections();

        // When:
        final WithCollections populated = populator.populate(new WithCollections());

        // Then:
        assertThat(populated._collection, is(not(nullValue())));
        assertThat(populated._collection, is(not(original._collection)));
    }

    @Test
    public void shouldHandleListsOfMutableTypes() throws Exception {
        // Given:
        class WithLists {
            public List<TypeThatCanBeMutated> _list = new ArrayList<TypeThatCanBeMutated>() {{
                add(new TypeThatCanBeMutated());
            }};
        }

        final WithLists original = new WithLists();

        // When:
        final WithLists populated = populator.populate(new WithLists());

        // Then:
        assertThat(populated._list, is(not(nullValue())));
        assertThat(populated._list, is(not(original._list)));
    }

    @Test
    public void shouldHandleListsWithNulls() throws Exception {
        // Given:
        class WithLists {
            public List<TypeThatCanBeMutated> _listWithNull = new ArrayList<TypeThatCanBeMutated>() {{
                add(null);
            }};
        }

        final WithLists original = new WithLists();

        // When:
        final WithLists populated = populator.populate(new WithLists());

        // Then:
        assertThat(populated._listWithNull, is(not(nullValue())));
        assertThat(populated._listWithNull, is(not(original._listWithNull)));
    }

    @Test
    public void shouldHandleNullLists() throws Exception {
        // Given:
        class WithLists {
            public List<TypeThatCanBeMutated> _nullList = null;
        }

        // When:
        final WithLists populated = populator.populate(new WithLists());

        // Then:
        assertThat(populated._nullList, is(not(nullValue())));
        assertThat(populated._nullList, is(not(empty())));
    }

    @Test
    public void shouldHandleListsOfImmutableTypes() throws Exception {
        // Given:
        class WithLists {
            public List<String> _listTerminalType = new ArrayList<String>() {{
                add("1");
            }};
        }

        final WithLists original = new WithLists();

        // When:
        final WithLists populated = populator.populate(new WithLists());

        // Then:
        assertThat(populated._listTerminalType, is(not(nullValue())));
        assertThat(populated._listTerminalType, is(not(original._listTerminalType)));
    }

    @Test
    public void shouldHandleListsWithJustRuntimeTypeInfo() throws Exception {
        // Given:
        class WithLists {
            public Object list = new ArrayList<String>() {{
                add("initial");
            }};
        }

        final WithLists original = new WithLists();

        // When:
        final WithLists populated = populator.populate(new WithLists());

        // Then:
        assertThat(populated.list, is(not(nullValue())));
        assertThat(populated.list, is(not(original.list)));
    }

    @Test
    public void shouldHandleSetsOfMutableTypes() throws Exception {
        // Given:
        class WithSets {
            public Set<TypeThatCanBeMutated> _set = new HashSet<TypeThatCanBeMutated>() {{
                add(new TypeThatCanBeMutated());
            }};
        }

        final WithSets original = new WithSets();

        // When:
        final WithSets populated = populator.populate(new WithSets());

        // Then:
        assertThat(populated._set, is(not(nullValue())));
        assertThat(populated._set, is(not(original._set)));
    }

    @Test
    public void shouldHandleSetsOfImmutableTypes() throws Exception {
        // Given:
        class WithSets {
            public Set<String> _setTerminalType = new HashSet<String>() {{
                add("1");
            }};
        }

        final WithSets original = new WithSets();

        // When:
        final WithSets populated = populator.populate(new WithSets());

        // Then:
        assertThat(populated._setTerminalType, is(not(nullValue())));
        assertThat(populated._setTerminalType, is(not(original._setTerminalType)));
    }

    @Test
    public void shouldHandleNullSets() throws Exception {
        // Given:
        class WithSets {
            public Set<TypeThatCanBeMutated> _nullSet = null;
        }

        // When:
        final WithSets populated = populator.populate(new WithSets());

        // Then:
        assertThat(populated._nullSet, is(not(nullValue())));
        assertThat(populated._nullSet, is(not(empty())));
    }

    @Test
    public void shouldHandleSetsWithNulls() throws Exception {
        // Given:
        class WithSets {
            public Set<TypeThatCanBeMutated> _setWithNull = new HashSet<TypeThatCanBeMutated>() {{
                add(null);
            }};
        }

        final WithSets original = new WithSets();

        // When:
        final WithSets populated = populator.populate(new WithSets());

        // Then:
        assertThat(populated._setWithNull, is(not(nullValue())));
        assertThat(populated._setWithNull, is(not(original._setWithNull)));
    }

    @Test
    public void shouldHandleSetsWithJustRuntimeTypeInfo() throws Exception {
        // Given:
        class WithSets {
            public Collection<TypeThatCanBeMutated> _collection = new HashSet<>();
        }

        final WithSets original = new WithSets();

        // When:
        final WithSets populated = populator.populate(new WithSets());

        // Then:
        assertThat(populated._collection, is(not(nullValue())));
        assertThat(populated._collection, is(not(original._collection)));
    }

    @Test
    public void shouldNotInvalidateSetsWhenContentIsMutated() throws Exception {
        // Given:
        class WithSets {
            public HashSet<TypeThatCanBeMutated> _nullSet = null;
            public HashSet<TypeThatCanBeMutated> _set = new HashSet<TypeThatCanBeMutated>() {{
                add(new TypeThatCanBeMutated());
            }};
            public HashSet<TypeThatCanBeMutated> _setWithNull = new HashSet<TypeThatCanBeMutated>() {{
                add(null);
            }};
        }

        // When:
        final WithSets populated = populator.populate(new WithSets());

        // Then:
        assertSetValid(populated._nullSet);
        assertSetValid(populated._set);
        assertSetValid(populated._setWithNull);
    }

    @Test
    public void shouldHandleMapsOfNonTerminalValues() throws Exception {
        // Given:
        class TypeWithMapField {
            public Map<String, TypeThatCanBeMutated> _map = new HashMap<String, TypeThatCanBeMutated>() {{
                put("this", new TypeThatCanBeMutated());
            }};
        }

        final TypeWithMapField original = new TypeWithMapField();

        // When:
        final TypeWithMapField populated = populator.populate(new TypeWithMapField());

        // Then:
        assertThat(populated._map, is(not(nullValue())));
        assertThat(populated._map, is(not(original._map)));
    }

    @Test
    public void shouldHandleMapsOfTerminalValues() throws Exception {
        // Given:
        class TypeWithMapField {
            public Map<String, String> _mapTerminalType = new HashMap<String, String>() {{
                put("this", "1");
            }};
        }

        final TypeWithMapField original = new TypeWithMapField();

        // When:
        final TypeWithMapField populated = populator.populate(new TypeWithMapField());

        // Then:
        assertThat(populated._mapTerminalType, is(not(nullValue())));
        assertThat(populated._mapTerminalType, is(not(original._mapTerminalType)));
    }

    @Test
    public void shouldHandleNullMaps() throws Exception {
        // Given:
        class TypeWithMapField {
            public Map<String, TypeThatCanBeMutated> _nullMap = null;
        }

        // When:
        final TypeWithMapField populated = populator.populate(new TypeWithMapField());

        // Then:
        assertThat(populated._nullMap, is(not(nullValue())));
    }

    @Test
    public void shouldHandleMapsWithNullValues() throws Exception {
        // Given:
        class TypeWithMapField {
            public Map<String, TypeThatCanBeMutated> _mapWithNull = new HashMap<String, TypeThatCanBeMutated>() {{
                put("this", null);
            }};
        }

        final TypeWithMapField original = new TypeWithMapField();

        // When:
        final TypeWithMapField populated = populator.populate(new TypeWithMapField());

        // Then:
        assertThat(populated._mapWithNull, is(not(nullValue())));
        assertThat(populated._mapWithNull, is(not(original._mapWithNull)));
    }

    @Test
    public void shouldHandleMapsWithJustRuntimeTypeInfo() throws Exception {
        // Given:
        class TypeWithMapField {
            public Object map = new HashMap<String, TypeThatCanBeMutated>() {{
                put("this", new TypeThatCanBeMutated());
            }};
        }

        final TypeWithMapField original = new TypeWithMapField();

        // When:
        final TypeWithMapField populated = populator.populate(new TypeWithMapField());

        // Then:
        assertThat(populated.map, is(not(nullValue())));
        assertThat(populated.map, is(not(original.map)));
    }

    @Test
    public void shouldHandleEnums() throws Exception {
        // Given:
        class TypeWithEnumField {
            public SomeEnum _enum;
        }

        final TypeWithEnumField original = new TypeWithEnumField();

        // When:
        final TypeWithEnumField populated = populator.populate(new TypeWithEnumField());

        // Then:
        assertThat(populated._enum, is(not(nullValue())));
        assertThat(populated._enum, is(not(original._enum)));
    }

    @Test
    public void shouldHandleNestedObjects() throws Exception {
        // Given:
        class TypeWithNestedObject {
            public Nested _nestedType = new Nested();

            class Nested {
                private int _int = 42;
            }
        }
        final TypeWithNestedObject original = new TypeWithNestedObject();

        // When:
        final TypeWithNestedObject populated = populator.populate(new TypeWithNestedObject());

        // Then:
        assertThat(populated._nestedType._int, is(not(original._nestedType._int)));
    }

    @Test
    public void shouldHandleNullNestedObjects() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class TypeWithNestedObject {
            public Nested _nestedType = null;

            class Nested {
                private int field;
            }
        }

        final TypeWithNestedObject currentValue = new TypeWithNestedObject();

        // When:
        final TypeWithNestedObject populated = populator.populate(currentValue);

        // Then:
        assertThat(populated._nestedType, is(not(nullValue())));
    }

    @Test
    public void shouldWorkWithPrivateConstructors() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class SomeType {
            private TypeWithPrivateConstructor field;

            class TypeWithPrivateConstructor {
                private TypeWithPrivateConstructor() {
                }
            }
        }

        // When:
        final SomeType populated = populator.populate(new SomeType());

        // Then:
        assertThat(populated.field, is(not(nullValue())));
    }

    @Test
    public void shouldHandleTypeVariables() throws Exception {
        // Given:
        class TypeWithTypeVariables<TypeElement> {
            // collection knows how to map ET -> TypeElement
            public Collection<TypeElement> collection = new CustomCollection<>();
        }

        class TypeWrappingTypeWithTypeVariables {
            // _type knows how to map TypeElement -> String
            public TypeWithTypeVariables<String> _type = new TypeWithTypeVariables<>();
        }

        final TypeWrappingTypeWithTypeVariables currentValue = new TypeWrappingTypeWithTypeVariables();

        // When:
        final TypeWrappingTypeWithTypeVariables populated = populator.populate(currentValue);

        // Then:
        assertThat(populated._type.collection, is(not(empty())));
        assertThat(populated._type.collection.iterator().next(), is(instanceOf(String.class)));
        assertThat(populated._type.collection.iterator().next(), is(not("")));
    }

    @Test
    public void shouldHandleNullObjectFieldsByDefault() throws Exception {
        // Given:
        class TypeWithObjectField {
            public Object _null;
        }
        final TypeWithObjectField currentValue = new TypeWithObjectField();

        // When:
        final TypeWithObjectField populated = populator.populate(currentValue);

        // Then:
        assertThat("default strategy for null Object fields should be to leave them null", populated._null, is(nullValue()));
    }

    @Test
    public void shouldAllowCustomNullObjectHandling() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class TypeWithObjectField {
            public Object _null;
        }
        final NullObjectStrategy nullHandler = mock(NullObjectStrategy.class);
        final TypeWithObjectField currentValue = new TypeWithObjectField();
        final GraphPopulator.Builder builder = GraphPopulator.newBuilder();
        populator = builder.withInstanceFactories(builder
            .instanceFactoriesBuilder()
            .withNullObjectStrategy(nullHandler).build())
            .build();

        // When:
        populator.populate(currentValue);

        // Then:
        verify(nullHandler).onNullObject(eq(currentValue));
    }

    @Test
    public void shouldHandlePopulateCallWithJustTheType() throws Exception {
        // When:
        final SimpleType populated = populator.populate(SimpleType.class);

        // Then:
        assertThat(populated.field, is(not(nullValue())));
    }

    @Test
    public void shouldWorkWithFinalFields() throws Exception {
        // Given:
        class TypeWithFinalField {
            public final long _final = 9L;
        }

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
        class TypeWithTransientField {
            public transient long _transient = 9L;
        }

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
        @SuppressWarnings("UnusedDeclaration")
        class SomeType {
            private int _int = 2;
        }

        final Mutator mutator = givenMutatorRegistered(int.class);

        // When:
        populator.populate(new SomeType());

        // Then:
        verify(mutator).mutate(eq(int.class), eq(2), anyObject(), isA(PopulatorContext.class));
    }

    @Test
    public void shouldHonourFieldFilterList() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class SomeType {
            private double excludeMe;
        }

        final SomeType original = new SomeType();
        populator = GraphPopulator.newBuilder().withFieldFilter(field -> !field.getName().equals("excludeMe")).build();

        // When:
        final SomeType populated = populator.populate(new SomeType());

        // Then:
        assertThat(populated.excludeMe, is(original.excludeMe));
    }

    @Test
    public void shouldWorkWithRawGenericTypes() throws Exception {
        // Given:
        class WithRawGenericType {
            @SuppressWarnings("unchecked")
            public List _rawList = new ArrayList();
        }

        final WithRawGenericType original = new WithRawGenericType();

        // When:
        final WithRawGenericType populated = populator.populate(new WithRawGenericType());

        // Then:
        assertThat(populated._rawList, is(notNullValue()));
        assertThat(populated._rawList, is(not(original._rawList)));
    }

    @Test
    public void shouldHandleFieldsWithJustRuntimeTypeInfo() throws Exception {
        // Given:
        class WithNoCompileTimeTypeInfo {
            public Object immutableField = "initial";
            public TypeThatCanBeMutated mutableField = new TypeThatCanBeMutated("initial");
        }

        final WithNoCompileTimeTypeInfo original = new WithNoCompileTimeTypeInfo();

        // When:
        final WithNoCompileTimeTypeInfo populated = populator.populate(new WithNoCompileTimeTypeInfo());

        // Then:
        assertThat(populated.immutableField, is(notNullValue()));
        assertThat(populated.mutableField, is(notNullValue()));
        assertThat(populated.immutableField, is(not(original.immutableField)));
        assertThat(populated.mutableField, is(not(original.mutableField)));
    }

    @Test
    public void shouldTreatUnboundedWildcardsAsObjects() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class TypeWithUnboundedWildcard {
            public List<?> wildcardField;
        }

        final NullObjectStrategy nullHandler = mock(NullObjectStrategy.class);
        final TypeWithUnboundedWildcard currentValue = new TypeWithUnboundedWildcard();
        final GraphPopulator.Builder builder = GraphPopulator.newBuilder();
        populator = builder.withInstanceFactories(builder
            .instanceFactoriesBuilder()
            .withNullObjectStrategy(nullHandler).build())
            .build();

        // When:
        populator.populate(currentValue);

        // Then:
        verify(nullHandler, atLeastOnce()).onNullObject(eq(currentValue));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldAllowInstanceFactoryToBeRegisteredAgainstBoundedWildcardType() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class TypeWithBoundedWildcard {
            public List<? extends Number> wildcardField;
        }

        final Type wildcard = TypeWithBoundedWildcard.class.getField("wildcardField").getGenericType();
        final InstanceFactory factory = mock(InstanceFactory.class);
        final TypeWithBoundedWildcard currentValue = new TypeWithBoundedWildcard();
        final GraphPopulator.Builder builder = GraphPopulator.newBuilder();
        populator = builder.withInstanceFactories(builder
            .instanceFactoriesBuilder()
            .withSpecificFactory(wildcard, factory).build())
            .build();

        // When:
        populator.populate(currentValue);

        // Then:
        verify(factory).createInstance(any(Class.class), anyObject(), any(InstanceFactories.class));
    }

    @Test
    public void shouldWorkWithBigDecimals() throws Exception {
        // Given:
        class TypeWithBigDecimalField {
            public BigDecimal _bigDecimal;
        }

        final TypeWithBigDecimalField original = new TypeWithBigDecimalField();

        // When:
        final TypeWithBigDecimalField populated = populator.populate(new TypeWithBigDecimalField());

        // Then:
        assertThat(populated._bigDecimal, is(notNullValue()));
        assertThat(populated._bigDecimal, is(not(original._bigDecimal)));
    }

    // Todo(ac): Add tests to ensure we're not mutating any field more than once - think arrays, collections, etc.
    // Todo(ac): Add test with deep object graph (may have issues with stack overflow)
    // Todo(Ac): Add test for Map<String, List<Integer>>

    private Mutator givenMutatorRegistered(Type... types) {
        final Mutator mutator = spy(NoOpMutator.class);
        final Mutators.Builder builder = Mutators.newBuilder();
        for (Type type : types) {
            builder.withSpecificMutator(type, mutator);
        }
        populator = GraphPopulator.newBuilder().withMutators(builder.build()).build();
        return mutator;
    }

    private static <T> void assertSetValid(final HashSet<T> original) {
        // Check set hasn't been made invalid by values being mutated after they've been inserted into the set:
        final HashSet<T> copy = new HashSet<>(original);
        assertThat(original, is(equalTo(copy)));
        assertThat(copy, is(equalTo(original)));
    }

    @SuppressWarnings("UnusedDeclaration")
    public enum SomeEnum {
        forkHandles, fourCandles
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class SimpleType {
        private int field;
    }

    private static class TypeWithStaticField {
        public static long _static = 9L;
    }

    private static class TypeThatCanBeMutated {
        public String field = "initial";

        public TypeThatCanBeMutated() {
        }

        public TypeThatCanBeMutated(final String initial) {
            field = initial;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final TypeThatCanBeMutated that = (TypeThatCanBeMutated) o;
            return field.equals(that.field);
        }

        @Override
        public int hashCode() {
            return field.hashCode();
        }
    }

}

// Todo(ac): Add 'strict' Populator#strict() that returns a populator built with types that throw on error i.e. throw on null object, throw on immutable colleciton element, etc.