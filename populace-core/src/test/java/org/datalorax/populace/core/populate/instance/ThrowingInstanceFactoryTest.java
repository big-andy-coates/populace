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

package org.datalorax.populace.core.populate.instance;

import com.google.common.testing.EqualsTester;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

public class ThrowingInstanceFactoryTest {
    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void shouldThrowOnNullObject() throws Exception {
        // Given:
        final Object parent = new Object();

        // When:
        new ThrowingInstanceFactory().onNullObject(parent);
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(new ThrowingInstanceFactory(), new ThrowingInstanceFactory())
            .addEqualityGroup(mock(NullObjectStrategy.class))
            .testEquals();
    }
}