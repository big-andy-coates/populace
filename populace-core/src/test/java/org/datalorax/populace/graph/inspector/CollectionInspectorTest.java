package org.datalorax.populace.graph.inspector;

import org.datalorax.populace.field.visitor.FieldVisitor;
import org.datalorax.populace.graph.WalkerContext;
import org.testng.annotations.BeforeMethod;

import static org.mockito.Mockito.mock;

public class CollectionInspectorTest {
    private FieldVisitor visitor;
    private WalkerContext config;
    private Inspector inspector;

    @BeforeMethod
    public void setUp() throws Exception {
        visitor = mock(FieldVisitor.class);
        config = mock(WalkerContext.class);

        inspector = CollectionInspector.INSTANCE;
    }

    // Todo(ac): how about some tests?
}