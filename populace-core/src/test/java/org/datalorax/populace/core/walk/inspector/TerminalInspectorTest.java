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

package org.datalorax.populace.core.walk.inspector;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TerminalInspectorTest {
    @Test
    public void shouldReturnNoFields() throws Exception {
        assertThat(TerminalInspector.INSTANCE.getFields(SomeType.class, null).iterator().hasNext(), is(false));
    }

    @Test
    public void shouldReturnNoChildren() throws Exception {
        assertThat(TerminalInspector.INSTANCE.getElements(new SomeType()).iterator().hasNext(), is(false));
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class SomeType {
        public String field;
    }
}