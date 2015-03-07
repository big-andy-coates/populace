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

package org.datalorax.populace.graph;

import org.datalorax.populace.field.filter.ExcludeStaticFieldsFilter;
import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.field.filter.FieldFilters;
import org.datalorax.populace.field.visitor.FieldVisitor;
import org.datalorax.populace.field.visitor.FieldVisitors;
import org.datalorax.populace.field.visitor.SetAccessibleFieldVisitor;
import org.datalorax.populace.graph.inspector.Inspectors;
import org.datalorax.populace.graph.inspector.TerminalInspector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.datalorax.populace.field.FieldInfoMatcher.hasField;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class GraphWalkerFunctionalTest {
    private GraphWalker walker;
    private FieldFilter filter;
    private FieldVisitor visitor;

    @BeforeMethod
    public void setUp() throws Exception {
        filter = mock(FieldFilter.class);
        visitor = mock(FieldVisitor.class);

        when(filter.evaluate(any(Field.class))).thenReturn(true);

        walker = GraphWalker.newBuilder().build();
    }

    @Test
    public void shouldVisitGraphInOrder() throws Exception {
        // Given:
        final TypeWithNestedObject instance = new TypeWithNestedObject();

        // When:
        walker.walk(instance, visitor);

        // Then:
        verify(visitor).visit(argThat(hasField(equalTo(TypeWithNestedObject.class.getDeclaredField("_nested")), equalTo(instance))));
        verify(visitor).visit(argThat(hasField(equalTo(NestedType.class.getDeclaredField("_nested")), equalTo(instance._nested))));
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldObeyFieldFilter() throws Exception {
        // Given:
        walker = GraphWalker.newBuilder()
            .withFieldFilter(FieldFilters.and(ExcludeStaticFieldsFilter.INSTANCE, filter))  // Skip statics
            .build();

        // When:
        walker.walk(new TypeWithStaticField(), visitor);

        // Then:
        verify(visitor, never()).visit(argThat(hasField(equalTo(TypeWithStaticField.class.getDeclaredField("_static")))));
    }

    @Test
    public void shouldVisitPrimitiveButNotInternals() throws Exception {
        // When:
        walker.walk(new TypeWithPrimitiveField(), visitor);

        // Then:
        verify(visitor).visit(argThat(hasField(equalTo(TypeWithPrimitiveField.class.getDeclaredField("_primitive")))));
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldVisitBoxedPrimitiveButNotInternals() throws Exception {
        // When:
        walker.walk(new TypeWithBoxedPrimitiveField(), visitor);

        // Then:
        verify(visitor).visit(argThat(hasField(equalTo(TypeWithBoxedPrimitiveField.class.getDeclaredField("_boxed")))));
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldVisitStringButNotInternals() throws Exception {
        // When:
        walker.walk(new TypeWithStringField(), visitor);

        // Then:
        verify(visitor).visit(argThat(hasField(equalTo(TypeWithStringField.class.getDeclaredField("_string")))));
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldVisitEnumsButNotInternals() throws Exception {
        // When:
        walker.walk(new TypeWithEnumField(), visitor);

        // Then:
        verify(visitor).visit(argThat(hasField(equalTo(TypeWithEnumField.class.getDeclaredField("_enum")))));
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldWalkElementsOfArray() throws Exception {
        // When:
        walker.walk(new TypeWithArrayField(), visitor);

        // Then:
        verify(visitor).visit(argThat(hasField(equalTo(TypeWithArrayField.class.getDeclaredField("_array")))));
        verify(visitor).visit(argThat(hasField(equalTo(NestedType.class.getDeclaredField("_nested")))));
        verify(visitor).visit(argThat(hasField(equalTo(AnotherNestedType.class.getDeclaredField("_nested")))));
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldWalkElementsOfCollection() throws Exception {
        // When:
        walker.walk(new TypeWithCollectionField(), visitor);

        // Then:
        verify(visitor).visit(argThat(hasField(equalTo(TypeWithCollectionField.class.getDeclaredField("_collection")))));
        verify(visitor).visit(argThat(hasField(equalTo(NestedType.class.getDeclaredField("_nested")))));
        verify(visitor).visit(argThat(hasField(equalTo(AnotherNestedType.class.getDeclaredField("_nested")))));
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldWalkValuesOfMap() throws Exception {
        // When:
        walker.walk(new TypeWithMapField(), visitor);

        // Then:
        verify(visitor).visit(argThat(hasField(equalTo(TypeWithMapField.class.getDeclaredField("_map")))));
        verify(visitor).visit(argThat(hasField(equalTo(NestedType.class.getDeclaredField("_nested")))));
        verify(visitor).visit(argThat(hasField(equalTo(AnotherNestedType.class.getDeclaredField("_nested")))));
        verifyNoMoreInteractions(visitor);
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
        walker.walk(new TypeWithNestedObject(), visitor);

        // Then:
        verify(visitor, never()).visit(argThat(hasField(equalTo(NestedType.class.getDeclaredField("_nested")))));
    }

    @Test
    public void shouldVisitPrivateFieldsIfSomethingSetsAccessible() throws Exception {
        // Given:
        final FieldVisitor visitors = FieldVisitors.chain(SetAccessibleFieldVisitor.INSTANCE, visitor);

        // When:
        walker.walk(new TypeWithPrivateField(), visitors);

        // Then:
        verify(visitor).visit(argThat(hasField(equalTo(TypeWithPrivateField.class.getDeclaredField("_private")))));
    }

    @Test(expectedExceptions = WalkerException.class)
    public void shouldThrowOnVisitingPrivateFieldIfNothingSetsAccessible() throws Exception {
        // When:
        walker.walk(new TypeWithPrivateField(), visitor);
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
    public static class TypeWithPrivateField {
        private Object _private;
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
        public enum SomeEnum {
            first, second, third
        }

        public SomeEnum _enum = SomeEnum.first;
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

    // Todo(ac): Add field filter types to exclude circular references and to not follow parent reference in inner class.
}