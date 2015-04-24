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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class FieldFiltersTest {
    private FieldFilter filter1;
    private FieldFilter filter2;
    private FieldFilter filter3;

    @BeforeMethod
    public void setUp() throws Exception {
        filter1 = mock(FieldFilter.class, "1");
        filter2 = mock(FieldFilter.class, "2");
        filter3 = mock(FieldFilter.class, "3");
    }

    @Test
    public void shouldReturnDefaults() throws Exception {
        assertThat(FieldFilters.defaults(), is(new AndFieldFilter(ExcludeStaticFieldsFilter.INSTANCE, ExcludeTransientFieldsFilter.INSTANCE)));
    }

    @Test
    public void shouldReturnAndFilter() throws Exception {
        assertThat(FieldFilters.and(filter1, filter2), is(new AndFieldFilter(filter1, filter2)));
    }

    @Test
    public void shouldReturnOrFilter() throws Exception {
        assertThat(FieldFilters.or(filter1, filter2), is(new OrFieldFilter(filter1, filter2)));
    }

    @Test
    public void shouldReturnAllFilter() throws Exception {
        assertThat(FieldFilters.all(filter1, filter2, filter3), is(new AllFieldFilter(filter1, filter2, filter3)));
    }

    @Test
    public void shouldReturnAnyFilter() throws Exception {
        assertThat(FieldFilters.any(filter1, filter2, filter3), is(new AnyFieldFilter(filter1, filter2, filter3)));
    }

    @Test
    public void shouldReturnOnlyFilterFromAny() throws Exception {
        assertThat(FieldFilters.any(filter1), is(filter1));
    }

    @Test
    public void shouldReturnOnlyFilterFromAll() throws Exception {
        assertThat(FieldFilters.all(filter1), is(filter1));
    }
}