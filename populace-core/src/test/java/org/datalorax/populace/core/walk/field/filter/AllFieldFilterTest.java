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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AllFieldFilterTest {
    private FieldFilter first;
    private FieldFilter second;
    private FieldFilter third;
    private FieldInfo field;
    private FieldFilter filter;

    @BeforeMethod
    public void setUp() throws Exception {
        first = mock(FieldFilter.class, "first");
        second = mock(FieldFilter.class, "second");
        third = mock(FieldFilter.class, "third");
        field = mock(FieldInfo.class);

        filter = new AllFieldFilter(first, second, third);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowIfAnyFilterIsNull() throws Exception {
        new AllFieldFilter(first, null, third);
    }

    @Test
    public void shouldExcludeIfAnyFilterExclude() throws Exception {
        // Given:
        when(first.include(field)).thenReturn(true);
        when(second.include(field)).thenReturn(false);
        when(third.include(field)).thenReturn(true);

        // Then:
        assertThat(filter.include(field), is(false));
    }

    @Test
    public void shouldIncludeOnlyIfAllFiltersInclude() throws Exception {
        // Given:
        when(first.include(field)).thenReturn(true);
        when(second.include(field)).thenReturn(true);
        when(third.include(field)).thenReturn(true);

        // Then:
        assertThat(filter.include(field), is(true));
    }
}