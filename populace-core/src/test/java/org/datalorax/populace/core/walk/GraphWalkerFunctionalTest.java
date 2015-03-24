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

import org.datalorax.populace.core.walk.field.FieldInfo;
import org.datalorax.populace.core.walk.field.filter.ExcludeStaticFieldsFilter;
import org.datalorax.populace.core.walk.field.filter.FieldFilter;
import org.datalorax.populace.core.walk.field.filter.FieldFilters;
import org.datalorax.populace.core.walk.inspector.Inspectors;
import org.datalorax.populace.core.walk.inspector.TerminalInspector;
import org.datalorax.populace.core.walk.visitor.FieldVisitor;
import org.datalorax.populace.core.walk.visitor.FieldVisitors;
import org.datalorax.populace.core.walk.visitor.SetAccessibleFieldVisitor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.datalorax.populace.core.walk.field.FieldInfoMatcher.hasField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.fail;

public class GraphWalkerFunctionalTest {
    private GraphWalker walker;
    private FieldFilter filter;
    private FieldVisitor visitor;

    @BeforeMethod
    public void setUp() throws Exception {
        filter = mock(FieldFilter.class);
        visitor = mock(FieldVisitor.class);

        when(filter.include(any(FieldInfo.class))).thenReturn(true);

        walker = GraphWalker.newBuilder().build();
    }

    @Test
    public void shouldVisitGraphInOrder() throws Exception {
        // Given:
        final TypeWithNestedObject instance = new TypeWithNestedObject();

        // When:
        walker.walk(instance, visitor);

        // Then:
        verify(visitor).visit(argThat(hasField("_nested", TypeWithNestedObject.class, instance)));
        verify(visitor).visit(argThat(hasField("_nested", NestedType.class, instance._nested)));
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
        verify(visitor, never()).visit(argThat(hasField("_static", TypeWithStaticField.class)));
    }

    @Test
    public void shouldVisitPrimitiveButNotInternals() throws Exception {
        // When:
        walker.walk(new TypeWithPrimitiveField(), visitor);

        // Then:
        verify(visitor).visit(argThat(hasField("_primitive", TypeWithPrimitiveField.class)));
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldVisitBoxedPrimitiveButNotInternals() throws Exception {
        // When:
        walker.walk(new TypeWithBoxedPrimitiveField(), visitor);

        // Then:
        verify(visitor).visit(argThat(hasField("_boxed", TypeWithBoxedPrimitiveField.class)));
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldVisitStringButNotInternals() throws Exception {
        // When:
        walker.walk(new TypeWithStringField(), visitor);

        // Then:
        verify(visitor).visit(argThat(hasField("_string", TypeWithStringField.class)));
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldVisitEnumsButNotInternals() throws Exception {
        // When:
        walker.walk(new TypeWithEnumField(), visitor);

        // Then:
        verify(visitor).visit(argThat(hasField("_enum", TypeWithEnumField.class)));
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldWalkElementsOfArray() throws Exception {
        // When:
        walker.walk(new TypeWithArrayField(), visitor);

        // Then:
        verify(visitor).visit(argThat(hasField("_array", TypeWithArrayField.class)));
        verify(visitor).visit(argThat(hasField("_nested", NestedType.class)));
        verify(visitor).visit(argThat(hasField("_nested", AnotherNestedType.class)));
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldWalkElementsOfCollection() throws Exception {
        // When:
        walker.walk(new TypeWithCollectionField(), visitor);

        // Then:
        verify(visitor).visit(argThat(hasField("_collection", TypeWithCollectionField.class)));
        verify(visitor).visit(argThat(hasField("_nested", NestedType.class)));
        verify(visitor).visit(argThat(hasField("_nested", AnotherNestedType.class)));
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldWalkValuesOfMap() throws Exception {
        // When:
        walker.walk(new TypeWithMapField(), visitor);

        // Then:
        verify(visitor).visit(argThat(hasField("_map", TypeWithMapField.class)));
        verify(visitor).visit(argThat(hasField("_nested", NestedType.class)));
        verify(visitor).visit(argThat(hasField("_nested", AnotherNestedType.class)));
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
        verify(visitor, never()).visit(argThat(hasField("_nested", NestedType.class)));
    }

    @Test
    public void shouldVisitPrivateFieldsIfSomethingSetsAccessible() throws Exception {
        // Given:
        final FieldVisitor visitors = FieldVisitors.chain(SetAccessibleFieldVisitor.INSTANCE, visitor);

        // When:
        walker.walk(new TypeWithPrivateField(), visitors);

        // Then:
        verify(visitor).visit(argThat(hasField("_private", TypeWithPrivateField.class)));
    }

    @Test(expectedExceptions = WalkerException.class)
    public void shouldThrowOnVisitingPrivateFieldIfNothingSetsAccessible() throws Exception {
        // When:
        walker.walk(new TypeWithPrivateField(), visitor);
    }

    @Test
    public void shouldIncludePathInExceptions() throws Exception {
        // Given:
        doThrow(new RuntimeException()).when(visitor).visit(any(FieldInfo.class));

        // When:
        try {
            walker.walk(new TypeWithNestedObject(), visitor);
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
        walker.walk(new TypeWithSuper(), visitor);

        // Then:
        verify(visitor).visit(argThat(hasField("_superField", SuperType.class)));
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

    // Todo(ac): Add field filter types to include circular references and to not follow parent reference in inner class.
}