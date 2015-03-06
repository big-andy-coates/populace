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

package org.datalorax.populace.field.visitor;

import org.datalorax.populace.field.FieldInfo;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class FieldVisitorPairTest {
    private FieldVisitor first;
    private FieldVisitor second;
    private FieldInfo field;
    private FieldVisitor visitor;
    private Object instance;

    @BeforeMethod
    public void setUp() throws Exception {
        first = mock(FieldVisitor.class);
        second = mock(FieldVisitor.class);
        field = mock(FieldInfo.class);
        instance = new Object();

        visitor = new FieldVisitorPair(first, second);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowIfFirstVisitorIsNull() throws Exception {
        new FieldVisitorPair(null, second);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowIfSecondVisitorIsNull() throws Exception {
        new FieldVisitorPair(first, null);
    }

    @Test
    public void shouldCalledBothVisitorsInOrder() throws Exception {
        // When:
        visitor.visit(field);

        // Then:
        InOrder inOrder = inOrder(first, second);
        inOrder.verify(first).visit(field);
        inOrder.verify(second).visit(field);
    }

    @Test
    public void shouldPassIsCollectionToBoth() throws Exception {
        // When:
        visitor.visit(field);

        // Then:
        verify(first).visit(field);
        verify(second).visit(field);
    }
}