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

import org.datalorax.populace.core.walk.field.RawField;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WalkerStackTest {
    @Test
    public void shouldStartPathWithRootObjectType() throws Exception {
        // Given:
        final WalkerStack root = WalkerStack.newStack("rootObject");

        // Then:
        assertThat(root.getPath(), is("String"));
    }

    @Test
    public void shouldAppendFieldsToPath() throws Exception {
        // Given:
        final WalkerStack root = WalkerStack.newStack(new Object());
        final RawField field = mock(RawField.class);
        when(field.getGenericType()).thenReturn(Number.class);
        when(field.getName()).thenReturn("fred");

        // When:
        final WalkerStack stack = root.push(field);

        // Then:
        assertThat(stack.getPath(), endsWith(".fred"));
    }
}