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

package org.datalorax.populace.core.walk.visitor;

import org.datalorax.populace.core.walk.field.FieldInfo;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SetAccessibleFieldVisitorTest {
    @SuppressWarnings("UnusedDeclaration")  // Accessed via reflection
    private String inaccessibleField;

    @Test
    public void shouldSetFieldAccessible() throws Exception {
        // Given:
        final FieldInfo fieldInfo = mock(FieldInfo.class);

        // When:
        SetAccessibleFieldVisitor.INSTANCE.visit(fieldInfo);

        // Then:
        verify(fieldInfo).ensureAccessible();
    }
}