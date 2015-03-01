package org.datalorax.populace.populator.graph;

import org.datalorax.populace.populator.field.filter.ExcludeStaticFieldsFilter;
import org.datalorax.populace.populator.field.filter.FieldFilter;
import org.datalorax.populace.populator.field.filter.FieldFilterUtils;
import org.datalorax.populace.populator.field.visitor.FieldVisitor;
import org.datalorax.populace.populator.field.visitor.FieldVisitorUtils;
import org.datalorax.populace.populator.field.visitor.SetAccessibleFieldVisitor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class GraphWalkerTest {
    private GraphWalker walker;
    private FieldFilter filter;
    private FieldVisitor visitor;

    @BeforeMethod
    public void setUp() throws Exception {
        filter = mock(FieldFilter.class);
        visitor = mock(FieldVisitor.class);

        when(filter.evaluate(any(Field.class))).thenReturn(true);

        walker = new GraphWalker(
                FieldFilterUtils.and(ExcludeStaticFieldsFilter.INSTANCE, filter),       // Skip statics else we get horrid circular reference
                FieldVisitorUtils.combine(SetAccessibleFieldVisitor.INSTANCE, visitor)  // Set accessible so we can access private fields
        );
    }

    @Test
    public void shouldVisitGraph() throws Exception {
        // Given:
        final TypeWithNestedObject instance = new TypeWithNestedObject();

        // When:
        walker.walk(instance);

        // Then:
        verify(visitor).visit(TypeWithNestedObject.class.getDeclaredField("_nested"), instance);
        verify(visitor).visit(NestedType.class.getDeclaredField("_int"), instance._nested);
        verifyNoMoreInteractions(visitor);
    }

    private static class TypeWithNestedObject {
        public NestedType _nested = new NestedType();
    }

    private static class NestedType {
        public int _int = 9;
    }

    private static class TypeWithStaticField {
        public static String _static = "value";
    }

    private static class TypeWithPrivateField {
        private Object _private;
    }


}