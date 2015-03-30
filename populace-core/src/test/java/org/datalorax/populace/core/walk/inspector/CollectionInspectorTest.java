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

import org.datalorax.populace.core.walk.WalkerContext;
import org.datalorax.populace.core.walk.element.RawElement;
import org.datalorax.populace.core.walk.visitor.FieldVisitor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Stream;

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

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void shouldThrowOnSetElement() throws Exception {
        // Given:
        final Collection<String> collection = new HashSet<>();
        collection.add("bob");
        final Stream<RawElement> elements = inspector.getElements(collection);

        // When:
        elements.forEach(e -> e.setValue("hello"));
    }

    // Todo(ac): how about some tests?
}