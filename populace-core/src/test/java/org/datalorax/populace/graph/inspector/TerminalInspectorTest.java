package org.datalorax.populace.graph.inspector;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TerminalInspectorTest {
    @Test
    public void shouldReturnNoFields() throws Exception {
        assertThat(TerminalInspector.INSTANCE.getFields(null).iterator().hasNext(), is(false));
    }

    @Test
    public void shouldReturnNoChildren() throws Exception {
        assertThat(TerminalInspector.INSTANCE.getChildren(null).iterator().hasNext(), is(false));
    }
}