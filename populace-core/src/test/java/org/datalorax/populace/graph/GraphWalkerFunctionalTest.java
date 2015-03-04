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
        verify(visitor).visit(TypeWithNestedObject.class.getDeclaredField("_nested"), instance);
        verify(visitor).visit(NestedType.class.getDeclaredField("_nested"), instance._nested);
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
        verify(visitor, never()).visit(eq(TypeWithStaticField.class.getDeclaredField("_static")), anyObject());
    }

    @Test
    public void shouldVisitPrimitiveButNotInternals() throws Exception {
        // When:
        walker.walk(new TypeWithPrimitiveField(), visitor);

        // Then:
        verify(visitor).visit(eq(TypeWithPrimitiveField.class.getDeclaredField("_primitive")), anyObject());
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldVisitBoxedPrimitiveButNotInternals() throws Exception {
        // When:
        walker.walk(new TypeWithBoxedPrimitiveField(), visitor);

        // Then:
        verify(visitor).visit(eq(TypeWithBoxedPrimitiveField.class.getDeclaredField("_boxed")), anyObject());
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldVisitStringButNotInternals() throws Exception {
        // When:
        walker.walk(new TypeWithStringField(), visitor);

        // Then:
        verify(visitor).visit(eq(TypeWithStringField.class.getDeclaredField("_string")), anyObject());
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldVisitEnumsButNotInternals() throws Exception {
        // When:
        walker.walk(new TypeWithEnumField(), visitor);

        // Then:
        verify(visitor).visit(eq(TypeWithEnumField.class.getDeclaredField("_enum")), anyObject());
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldWalkElementsOfArray() throws Exception {
        // When:
        walker.walk(new TypeWithArrayField(), visitor);

        // Then:
        verify(visitor).visit(eq(TypeWithArrayField.class.getDeclaredField("_array")), anyObject());
        verify(visitor).visit(eq(NestedType.class.getDeclaredField("_nested")), anyObject());
        verify(visitor).visit(eq(AnotherNestedType.class.getDeclaredField("_nested")), anyObject());
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldWalkElementsOfCollection() throws Exception {
        // When:
        walker.walk(new TypeWithCollectionField(), visitor);

        // Then:
        verify(visitor).visit(eq(TypeWithCollectionField.class.getDeclaredField("_collection")), anyObject());
        verify(visitor).visit(eq(NestedType.class.getDeclaredField("_nested")), anyObject());
        verify(visitor).visit(eq(AnotherNestedType.class.getDeclaredField("_nested")), anyObject());
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldWalkValuesOfMap() throws Exception {
        // When:
        walker.walk(new TypeWithMapField(), visitor);

        // Then:
        verify(visitor).visit(eq(TypeWithMapField.class.getDeclaredField("_map")), anyObject());
        verify(visitor).visit(eq(NestedType.class.getDeclaredField("_nested")), anyObject());
        verify(visitor).visit(eq(AnotherNestedType.class.getDeclaredField("_nested")), anyObject());
        verifyNoMoreInteractions(visitor);
    }

    @Test
    public void shouldHonourCustomInspectors() throws Exception {
        // Given:
        walker = GraphWalker.newBuilder()
                .withInspectors(Inspectors.defaultInspectors()
                        .withSpecificType(NestedType.class, TerminalInspector.INSTANCE)
                        .build())
                .build();

        // When:
        walker.walk(new TypeWithNestedObject(), visitor);

        // Then:
        verify(visitor, never()).visit(eq(NestedType.class.getDeclaredField("_nested")), anyObject());
    }

    @Test
    public void shouldVisitPrivateFieldsIfSomethingSetsAccessible() throws Exception {
        // Given:
        final FieldVisitor visitors = FieldVisitors.chain(SetAccessibleFieldVisitor.INSTANCE, visitor);

        // When:
        walker.walk(new TypeWithPrivateField(), visitors);

        // Then:
        verify(visitor).visit(eq(TypeWithPrivateField.class.getDeclaredField("_private")), anyObject());
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
            first, second, third;
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