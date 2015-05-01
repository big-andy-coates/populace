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

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;
import org.datalorax.populace.core.walk.field.FieldInfo;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AndFieldFilterTest {
    private FieldFilter first;
    private FieldFilter second;
    private FieldInfo field;
    private FieldFilter filter;

    @BeforeMethod
    public void setUp() throws Exception {
        first = mock(FieldFilter.class);
        second = mock(FieldFilter.class);
        field = mock(FieldInfo.class);

        filter = new AndFieldFilter(first, second);
    }

    @Test
    public void shouldReturnFalseIfBothReturnFalse() throws Exception {
        // Given:
        when(first.include(field)).thenReturn(false);
        when(second.include(field)).thenReturn(false);

        // Then:
        assertThat(filter.include(field), is(false));
    }

    @Test
    public void shouldReturnFalseIfOnlyFirstReturnsTrue() throws Exception {
        // Given:
        when(first.include(field)).thenReturn(true);
        when(second.include(field)).thenReturn(false);

        // Then:
        assertThat(filter.include(field), is(false));
    }

    @Test
    public void shouldReturnFalseIfOnlySecondReturnsTrue() throws Exception {
        // Given:
        when(first.include(field)).thenReturn(false);
        when(second.include(field)).thenReturn(true);

        // Then:
        assertThat(filter.include(field), is(false));
    }

    @Test
    public void shouldReturnTrueOnlyIfBothReturnTrue() throws Exception {
        // Given:
        when(first.include(field)).thenReturn(true);
        when(second.include(field)).thenReturn(true);

        // Then:
        assertThat(filter.include(field), is(true));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(new AndFieldFilter(first, second), new AndFieldFilter(first, second))
            .addEqualityGroup(new AndFieldFilter(mock(FieldFilter.class), second))
            .addEqualityGroup(new AndFieldFilter(first, mock(FieldFilter.class)))
            .testEquals();
    }

    @Test
    public void shouldThrowNPEsOnConstructorParams() throws Exception {
        new NullPointerTester()
            .setDefault(FieldFilter.class, mock(FieldFilter.class))
            .testAllPublicConstructors(AndFieldFilter.class);
    }
}