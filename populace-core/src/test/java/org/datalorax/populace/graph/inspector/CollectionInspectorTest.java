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