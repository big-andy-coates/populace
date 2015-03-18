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

package org.datalorax.populace.core.walk.field.filter;

import org.datalorax.populace.core.walk.field.FieldInfo;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExcludeFinalFieldsFilterTest {
    @Test
    public void shouldExcludeFinalField() throws Exception {
        // Given:
        final FieldInfo fieldInfo = mock(FieldInfo.class);
        when(fieldInfo.isFinal()).thenReturn(true);

        // Then:
        assertThat(ExcludeFinalFieldsFilter.INSTANCE.include(fieldInfo), is(false));
    }

    @Test
    public void shouldIncludeNonFinalField() throws Exception {
        // Given:
        final FieldInfo fieldInfo = mock(FieldInfo.class);
        when(fieldInfo.isFinal()).thenReturn(false);

        // Then:
        assertThat(ExcludeFinalFieldsFilter.INSTANCE.include(fieldInfo), is(true));
    }
}