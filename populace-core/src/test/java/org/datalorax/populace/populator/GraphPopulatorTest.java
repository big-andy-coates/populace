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

package org.datalorax.populace.populator;

import org.datalorax.populace.field.visitor.FieldVisitor;
import org.datalorax.populace.graph.GraphWalker;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Andrew Coates - 25/02/2015.
 */
public class GraphPopulatorTest {
    private GraphPopulator populator;
    private PopulatorContext config;
    private GraphWalker walker;

    @BeforeMethod
    public void setUp() throws Exception {
        config = mock(PopulatorContext.class);
        walker = mock(GraphWalker.class);

        populator = new GraphPopulator(walker, config);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowOnNullConfig() {
        new GraphPopulator(null, null);
    }

    @Test
    public void shouldPassInstanceToWalkerWhenWorkingWithInstance() throws Exception {
        // Given:
        final Object instance = new Object();

        // When:
        populator.populate(instance);

        // Then:
        verify(walker).walk(eq(instance), any(FieldVisitor.class));
    }

    // Todo(ac): how about some tests?

    private Mutator givenMutatorRegistered(Type... types) {
        final Mutator mutator = mock(Mutator.class);
        for (Type type : types) {
            when(config.getMutator(type)).thenReturn(mutator);
        }
        return mutator;
    }
}