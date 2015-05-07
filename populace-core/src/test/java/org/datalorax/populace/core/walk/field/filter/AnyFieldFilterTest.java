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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("deprecation")
public class AnyFieldFilterTest {
    @Mock(name = "first")
    private Predicate<FieldInfo> first;
    @Mock(name = "second")
    private Predicate<FieldInfo> second;
    @Mock(name = "third")
    private Predicate<FieldInfo> third;
    @Mock
    private FieldInfo field;
    private Predicate<FieldInfo> filter;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        filter = new AnyFieldFilter(first, second, third);
    }

    @Test
    public void shouldExcludeOnlyIfAllExclude() throws Exception {
        // Given:
        when(first.test(field)).thenReturn(false);
        when(second.test(field)).thenReturn(false);
        when(third.test(field)).thenReturn(false);

        // Then:
        assertThat(filter.test(field), is(false));
    }

    @Test
    public void shouldIncludeIfAnyInclude() throws Exception {
        // Given:
        when(first.test(field)).thenReturn(false);
        when(second.test(field)).thenReturn(true);
        when(third.test(field)).thenReturn(false);

        // Then:
        assertThat(filter.test(field), is(true));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(new AnyFieldFilter(first, second, third), new AnyFieldFilter(first, second, third))
            .addEqualityGroup(new AnyFieldFilter(first, second, third, mock(Predicate.class)))
            .addEqualityGroup(new AnyFieldFilter(first, second))
            .addEqualityGroup(new AnyFieldFilter(mock(Predicate.class), second, third))
            .addEqualityGroup(new AnyFieldFilter(first, mock(Predicate.class), third))
            .testEquals();
    }

    @Test
    public void shouldThrowNPEsOnConstructorParams() throws Exception {
        new NullPointerTester()
            .setDefault(Predicate.class, mock(Predicate.class))
            .testAllPublicConstructors(AnyFieldFilter.class);
    }
}