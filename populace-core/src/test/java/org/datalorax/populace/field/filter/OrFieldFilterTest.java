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

package org.datalorax.populace.field.filter;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrFieldFilterTest {
    private FieldFilter first;
    private FieldFilter second;
    private Field field;
    private FieldFilter filter;

    @BeforeMethod
    public void setUp() throws Exception {
        first = mock(FieldFilter.class);
        second = mock(FieldFilter.class);
        field = getClass().getDeclaredField("field");

        filter = new OrFieldFilter(first, second);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowIfFirstFilterIsNull() throws Exception {
        new OrFieldFilter(null, second);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowIfSecondFilterIsNull() throws Exception {
        new OrFieldFilter(first, null);
    }

    @Test
    public void shouldReturnFalseIfBothReturnFalse() throws Exception {
        // Given:
        when(first.evaluate(field)).thenReturn(false);
        when(second.evaluate(field)).thenReturn(false);

        // Then:
        assertThat(filter.evaluate(field), is(false));
    }

    @Test
    public void shouldReturnTrueIfOnlyFirstReturnsTrue() throws Exception {
        // Given:
        when(first.evaluate(field)).thenReturn(true);
        when(second.evaluate(field)).thenReturn(false);

        // Then:
        assertThat(filter.evaluate(field), is(true));
    }

    @Test
    public void shouldReturnTrueIfOnlySecondReturnsTrue() throws Exception {
        // Given:
        when(first.evaluate(field)).thenReturn(false);
        when(second.evaluate(field)).thenReturn(true);

        // Then:
        assertThat(filter.evaluate(field), is(true));
    }

    @Test
    public void shouldReturnTrueIfBothReturnTrue() throws Exception {
        // Given:
        when(first.evaluate(field)).thenReturn(true);
        when(second.evaluate(field)).thenReturn(true);

        // Then:
        assertThat(filter.evaluate(field), is(true));
    }
}