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

package org.datalorax.populace.core.walk;

import org.datalorax.populace.core.CustomCollection;
import org.datalorax.populace.core.walk.field.FieldInfo;
import org.datalorax.populace.core.walk.field.FieldInfoMatcher;
import org.datalorax.populace.core.walk.field.filter.ExcludeStaticFieldsFilter;
import org.datalorax.populace.core.walk.field.filter.FieldFilter;
import org.datalorax.populace.core.walk.field.filter.FieldFilters;
import org.datalorax.populace.core.walk.inspector.Inspectors;
import org.datalorax.populace.core.walk.inspector.TerminalInspector;
import org.datalorax.populace.core.walk.visitor.ElementVisitor;
import org.datalorax.populace.core.walk.visitor.FieldVisitor;
import org.datalorax.populace.core.walk.visitor.FieldVisitors;
import org.datalorax.populace.core.walk.visitor.SetAccessibleFieldVisitor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.datalorax.populace.core.walk.element.ElementInfoMatcher.elementOfType;
import static org.datalorax.populace.core.walk.element.ElementInfoMatcher.elementWithValue;
import static org.datalorax.populace.core.walk.field.FieldInfoMatcher.fieldInfo;
import static org.datalorax.populace.core.walk.field.FieldInfoMatcher.fieldWithValue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.fail;

public class GraphWalkerFunctionalTest {
    private GraphWalker walker;
    private FieldFilter filter;
    private FieldVisitor fieldVisitor;
    private FieldVisitor accessibleFieldVisitor;
    private ElementVisitor elementVisitor;

    @BeforeMethod
    public void setUp() throws Exception {
        filter = mock(FieldFilter.class);
        fieldVisitor = mock(FieldVisitor.class);
        elementVisitor = mock(ElementVisitor.class);

        when(filter.include(any(FieldInfo.class))).thenReturn(true);

        accessibleFieldVisitor = FieldVisitors.chain(SetAccessibleFieldVisitor.INSTANCE, fieldVisitor);
        walker = GraphWalker.newBuilder().build();
    }

    @Test
    public void shouldVisitGraphInOrder() throws Exception {
        // Given:
        final TypeWithNestedObject instance = new TypeWithNestedObject();

        // When:
        walker.walk(instance, accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(FieldInfoMatcher.fieldInfo("_nested", TypeWithNestedObject.class, instance)));
        verify(fieldVisitor).visit(argThat(FieldInfoMatcher.fieldInfo("_nested", NestedType.class, instance._nested)));
        verifyNoMoreInteractions(fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldObeyFieldFilter() throws Exception {
        // Given:
        walker = GraphWalker.newBuilder()
            .withFieldFilter(FieldFilters.and(ExcludeStaticFieldsFilter.INSTANCE, filter))  // Skip statics
            .build();

        // When:
        walker.walk(new TypeWithStaticField(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor, never()).visit(argThat(fieldInfo("_static", TypeWithStaticField.class)));
    }

    @Test
    public void shouldVisitPrimitiveButNotInternals() throws Exception {
        // When:
        walker.walk(new TypeWithPrimitiveField(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_primitive", TypeWithPrimitiveField.class)));
        verifyNoMoreInteractions(fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldVisitBoxedPrimitiveButNotInternals() throws Exception {
        // When:
        walker.walk(new TypeWithBoxedPrimitiveField(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_boxed", TypeWithBoxedPrimitiveField.class)));
        verifyNoMoreInteractions(fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldVisitStringButNotInternals() throws Exception {
        // When:
        walker.walk(new TypeWithStringField(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_string", TypeWithStringField.class)));
        verifyNoMoreInteractions(fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldVisitEnumsButNotInternals() throws Exception {
        // When:
        walker.walk(new TypeWithEnumField(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_enum", TypeWithEnumField.class)));
        verifyNoMoreInteractions(fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldWalkArraysOfNonTerminalTypes() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithArray {
            public SomeType[] _array = new SomeType[]{
                new SomeType("1"),
                new SomeType("2")
            };
        }

        // When:
        walker.walk(new WithArray(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_array", WithArray.class)));
        verify(elementVisitor, times(2)).visit(argThat(elementOfType(SomeType.class)));
        verify(fieldVisitor).visit(argThat(fieldWithValue("field", "1", SomeType.class)));
        verify(fieldVisitor).visit(argThat(fieldWithValue("field", "2", SomeType.class)));
    }

    @Test
    public void shouldWalkArraysOfTerminalTypes() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithArray {
            public String[] _arrayOfTerminal = new String[]{"1", "2", "3"};
        }

        // When:
        walker.walk(new WithArray(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_arrayOfTerminal", WithArray.class)));
        verify(elementVisitor, times(3)).visit(argThat(elementOfType(String.class)));
        verifyNoMoreInteractions(fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldWalkNullArray() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithArray {
            public String[] _nullArray;
        }

        // When:
        walker.walk(new WithArray(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_nullArray", WithArray.class)));
        verifyNoMoreInteractions(fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldWalkNullArrayElements() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithArray {
            public SomeType[] _arrayWithNull = new SomeType[]{null};
        }

        // When:
        walker.walk(new WithArray(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(elementVisitor).visit(argThat(elementWithValue(null)));
    }

    @Test
    public void shouldWalkCollectionsOfNonTerminalTypes() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithCollections {
            public Collection<SomeType> _collection = new ArrayList<SomeType>() {{
                add(new SomeType("1"));
                add(new SomeType("2"));
            }};
        }

        // When:
        walker.walk(new WithCollections(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_collection", WithCollections.class)));
        verify(elementVisitor, times(2)).visit(argThat(elementOfType(SomeType.class)));
        verify(fieldVisitor).visit(argThat(fieldWithValue("field", "1", SomeType.class)));
        verify(fieldVisitor).visit(argThat(fieldWithValue("field", "2", SomeType.class)));
        verifyNoMoreInteractions(fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldWalkCollectionsOfTerminalTypes() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithCollections {
            public Collection<String> _collectionTerminalType = new ArrayDeque<String>() {{
                add("1");
                add("2");
            }};
        }

        // When:
        walker.walk(new WithCollections(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_collectionTerminalType", WithCollections.class)));
        verify(elementVisitor, times(2)).visit(argThat(elementOfType(String.class)));
        verifyNoMoreInteractions(fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldWalkNullCollections() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithCollections {
            public Collection<SomeType> _nullCollection = null;
        }

        // When:
        walker.walk(new WithCollections(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_nullCollection", WithCollections.class)));
        verifyNoMoreInteractions(fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldWalkNullCollectionElements() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithCollections {
            public Collection<SomeType> _collectionWithNull = new CustomCollection<SomeType>() {{
                add(null);
            }};
        }

        // When:
        walker.walk(new WithCollections(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(elementVisitor).visit(argThat(elementWithValue(nullValue())));
    }

    @Test
    public void shouldWalkListsOfNonTerminalTypes() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithLists {
            public List<SomeType> _list = new ArrayList<SomeType>() {{
                add(new SomeType("1"));
                add(new SomeType("2"));
            }};
        }

        // When:
        walker.walk(new WithLists(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_list", WithLists.class)));
        verify(elementVisitor, times(2)).visit(argThat(elementOfType(SomeType.class)));
        verify(fieldVisitor).visit(argThat(fieldWithValue("field", "1", SomeType.class)));
        verify(fieldVisitor).visit(argThat(fieldWithValue("field", "2", SomeType.class)));
        verifyNoMoreInteractions(fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldWalkListsOfTerminalTypes() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithLists {
            public List<String> _listTerminalType = new ArrayList<String>() {{
                add("1");
                add("2");
            }};
        }

        // When:
        walker.walk(new WithLists(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_listTerminalType", WithLists.class)));
        verify(elementVisitor, times(2)).visit(argThat(elementOfType(String.class)));
        verifyNoMoreInteractions(fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldWalkListsWithNullElements() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithLists {
            public List<SomeType> _listWithNull = new ArrayList<SomeType>() {{
                add(null);
            }};
        }

        // When:
        walker.walk(new WithLists(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(elementVisitor).visit(argThat(elementWithValue(nullValue())));
    }

    @Test
    public void shouldWalkNullLists() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithLists {
            public List<SomeType> _nullList = null;
        }

        // When:
        walker.walk(new WithLists(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_nullList", WithLists.class)));
        verifyNoMoreInteractions(fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldWalkListsInCollectionFields() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithLists {
            public Collection<SomeType> _listWithNull = new ArrayList<>();
        }

        // When:
        walker.walk(new WithLists(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_listWithNull", WithLists.class)));
    }

    @Test
    public void shouldWalkSetsOfNonTerminalTypes() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithSets {
            public Set<SomeType> _set = new HashSet<SomeType>() {{
                add(new SomeType("1"));
                add(new SomeType("2"));
            }};
        }

        // When:
        walker.walk(new WithSets(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_set", WithSets.class)));
        verify(elementVisitor, times(2)).visit(argThat(elementOfType(SomeType.class)));
        verify(fieldVisitor).visit(argThat(fieldWithValue("field", "1", SomeType.class)));
        verify(fieldVisitor).visit(argThat(fieldWithValue("field", "2", SomeType.class)));
        verifyNoMoreInteractions(fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldWalkSetsOfTerminalTypes() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithSets {
            public Set<String> _setTerminalType = new HashSet<String>() {{
                add("1");
                add("2");
            }};
        }

        // When:
        walker.walk(new WithSets(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_setTerminalType", WithSets.class)));
        verify(elementVisitor, times(2)).visit(argThat(elementOfType(String.class)));
        verifyNoMoreInteractions(fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldWalkNullSetFields() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithSets {
            public Set<SomeType> _nullSet = null;
        }

        // When:
        walker.walk(new WithSets(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldWithValue(equalTo("_nullSet"), nullValue(), equalTo(WithSets.class))));
    }

    @Test
    public void shouldWalkNullSetElements() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithSets {
            public Set<SomeType> _setWithNull = new HashSet<SomeType>() {{
                add(null);
            }};
        }

        // When:
        walker.walk(new WithSets(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(elementVisitor).visit(argThat(elementWithValue(nullValue())));
    }

    @Test
    public void shouldWalkSetsInCollectionField() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithSets {
            public Collection<SomeType> _collection = new HashSet<SomeType>() {{
                add(new SomeType("1"));
            }};
        }

        // When:
        walker.walk(new WithSets(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldWithValue("field", "1", SomeType.class)));
    }

    @Test
    public void shouldWalkMapsOfNonTerminalValues() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithMaps {
            public Map<String, SomeType> _map = new HashMap<String, SomeType>() {{
                put("this", new SomeType("1"));
                put("that", new SomeType("2"));
            }};
        }

        // When:
        walker.walk(new WithMaps(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_map", WithMaps.class)));
        verify(elementVisitor, times(2)).visit(argThat(elementOfType(SomeType.class)));
        verify(fieldVisitor).visit(argThat(fieldWithValue("field", "1", SomeType.class)));
        verify(fieldVisitor).visit(argThat(fieldWithValue("field", "2", SomeType.class)));
        verifyNoMoreInteractions(fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldHandleMapsOfTerminalValues() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithMaps {
            public Map<String, String> _mapTerminalType = new HashMap<String, String>() {{
                put("this", "1");
                put("that", "2");
            }};
        }

        // When:
        walker.walk(new WithMaps(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_mapTerminalType", WithMaps.class)));
        verify(elementVisitor, times(2)).visit(argThat(elementOfType(String.class)));
        verifyNoMoreInteractions(fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldWalkNullMapFields() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class WithMaps {
            public Map<String, SomeType> _nullMap = null;
        }

        // When:
        walker.walk(new WithMaps(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_nullMap", WithMaps.class)));
        verifyNoMoreInteractions(fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldWalkNullMapValues() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class TypeWithMapField {
            public Map<String, SomeType> _mapWithNull = new HashMap<String, SomeType>() {{
                put("this", null);
            }};
        }

        // When:
        walker.walk(new TypeWithMapField(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(elementVisitor).visit(argThat(elementWithValue(nullValue())));
    }

    @Test
    public void shouldHonourCustomInspectors() throws Exception {
        // Given:
        walker = GraphWalker.newBuilder()
            .withInspectors(Inspectors.newBuilder()
                .withSpecificInspector(NestedType.class, TerminalInspector.INSTANCE)
                .build())
            .build();

        // When:
        walker.walk(new TypeWithNestedObject(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor, never()).visit(argThat(fieldInfo("_nested", NestedType.class)));
    }

    @Test
    public void shouldVisitPrivateFieldsIfSomethingSetsAccessible() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class TypeWithPrivateField {
            private Object _private;
        }

        final FieldVisitor visitors = FieldVisitors.chain(SetAccessibleFieldVisitor.INSTANCE, fieldVisitor);

        // When:
        walker.walk(new TypeWithPrivateField(), visitors, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_private", TypeWithPrivateField.class)));
    }

    @Test(expectedExceptions = WalkerException.class)
    public void shouldThrowOnVisitingPrivateFieldIfNothingSetsAccessible() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class TypeWithPrivateField {
            private Object _private;
        }

        // When:
        walker.walk(new TypeWithPrivateField(), fieldVisitor, elementVisitor);
    }

    @Test
    public void shouldIncludePathInExceptions() throws Exception {
        // Given:
        doThrow(new RuntimeException()).when(fieldVisitor).visit(any(FieldInfo.class));

        // When:
        try {
            walker.walk(new TypeWithNestedObject(), accessibleFieldVisitor, elementVisitor);
            fail("should of thrown exception");
        } catch (WalkerException e) {
            // Then:
            assertThat(e.getPath(), containsString("TypeWithNestedObject._nested"));
            assertThat(e.toString(), containsString("TypeWithNestedObject._nested"));
        }
    }

    @Test
    public void shouldVisitFieldsOfSuperTypes() throws Exception {
        // When:
        walker.walk(new TypeWithSuper(), accessibleFieldVisitor, elementVisitor);

        // Then:
        verify(fieldVisitor).visit(argThat(fieldInfo("_superField", SuperType.class)));
    }

    @Test
    public void shouldNotStackOverflowWithCircularReference() throws Exception {
        // Given:
        class TypeWithCircularReference {
            Object child;
        }
        class AnotherType {
            TypeWithCircularReference parent;
        }
        final TypeWithCircularReference instance = new TypeWithCircularReference();
        final AnotherType anotherType = new AnotherType();
        instance.child = anotherType;
        anotherType.parent = instance;

        // When:
        walker.walk(instance, accessibleFieldVisitor, elementVisitor);

        // Then:
        // It didn't stack overflow.
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class TypeWithNestedObject {
        public NestedType _nested = new NestedType();
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class NestedType {
        public NestedType _nested = null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class AnotherNestedType {
        public AnotherNestedType _nested = null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class TypeWithStaticField {
        public static String _static = "value";
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class TypeWithPrimitiveField {
        public int _primitive = 10;
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class TypeWithBoxedPrimitiveField {
        public Long _boxed = 10L;
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class TypeWithStringField {
        public String _string = "value";
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class TypeWithEnumField {
        public SomeEnum _enum = SomeEnum.first;

        public enum SomeEnum {
            first, second, third
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class TypeWithArrayField {
        public Object[] _array = {new NestedType(), new AnotherNestedType()};
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class TypeWithCollectionField {
        public List<Object> _collection = Arrays.asList(new NestedType(), new AnotherNestedType());
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class TypeWithMapField {
        public Map<String, Object> _map = new HashMap<String, Object>() {{
            put("nt", new NestedType());
            put("ant", new AnotherNestedType());
        }};
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class SuperType {
        public String _superField;
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class TypeWithSuper extends SuperType {

    }

    private static class SomeType {
        public String field = "value";

        public SomeType(final String s) {
            field = s;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final SomeType someType = (SomeType) o;
            return field.equals(someType.field);
        }

        @Override
        public int hashCode() {
            return field.hashCode();
        }

        @Override
        public String toString() {
            return "SomeType{" +
                "field='" + field + '\'' +
                '}';
        }
    }

    // Todo(ac): Add field filter types to include circular references and to not follow parent reference in inner class.


}