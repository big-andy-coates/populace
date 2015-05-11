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

package org.datalorax.populace.core.util;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ObjectUtilsTest {
    @Test
    public void shouldReturnToString() throws Exception {
        assertThat(ObjectUtils.safeToString("value"), is("value"));
    }

    @Test
    public void shouldSwallowException() throws Exception {
        // Given:
        final Object mock = mock(Object.class);
        when(mock.toString()).thenThrow(new RuntimeException("BANG"));

        // When:
        final String result = ObjectUtils.safeToString(mock);

        // Then:
        // Didn't go pop!
        assertThat(result, containsString("RuntimeException"));
    }
}