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

package org.datalorax.populace.core.populate;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;
import org.datalorax.populace.core.walk.GraphWalker;
import org.datalorax.populace.core.walk.visitor.ElementVisitor;
import org.datalorax.populace.core.walk.visitor.FieldVisitor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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

    @Test
    public void shouldPassInstanceToWalkerWhenWorkingWithInstance() throws Exception {
        // Given:
        final Object instance = new Object();

        // When:
        populator.populate(instance);

        // Then:
        verify(walker).walk(eq(instance), any(FieldVisitor.class), any(ElementVisitor.class));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnInnerClassType() throws Exception {
        // Given:
        class InnerClass {
        }

        // When:
        populator.populate(InnerClass.class);
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                new GraphPopulator(walker, config),
                new GraphPopulator(walker, config))
            .addEqualityGroup(
                new GraphPopulator(mock(GraphWalker.class, "other"), config))
            .addEqualityGroup(
                new GraphPopulator(walker, mock(PopulatorContext.class, "other")))
            .testEquals();
    }

    @Test
    public void shouldThrowNPEsOnConstructorParams() throws Exception {
        new NullPointerTester()
            .setDefault(GraphWalker.class, walker)
            .setDefault(PopulatorContext.class, config)
            .testAllPublicConstructors(GraphPopulator.class);
    }
}