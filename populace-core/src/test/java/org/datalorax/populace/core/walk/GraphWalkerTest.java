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

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

public class GraphWalkerTest {
    private WalkerContext context;

    @BeforeMethod
    public void setUp() throws Exception {
        context = mock(WalkerContext.class);
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                new GraphWalker(context),
                new GraphWalker(context))
            .addEqualityGroup(
                new GraphWalker(mock(WalkerContext.class, "other")))
            .testEquals();
    }

    @Test
    public void shouldThrowNPEsOnConstructorParams() throws Exception {
        new NullPointerTester()
            .setDefault(WalkerContext.class, context)
            .testAllPublicConstructors(GraphWalker.class);
    }
}