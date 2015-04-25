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
import org.datalorax.populace.core.walk.field.FieldInfo;
import org.datalorax.populace.core.walk.field.filter.FieldFilter;
import org.datalorax.populace.core.walk.inspector.Inspector;
import org.datalorax.populace.core.walk.inspector.Inspectors;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WalkerContextTest {
    @Mock
    private FieldFilter fieldFilter;
    @Mock
    private Inspectors inspectors;
    private FieldInfo field;
    private WalkerContext context;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        field = mock(FieldInfo.class);

        context = new WalkerContext(fieldFilter, inspectors);
    }

    @Test
    public void shouldExcludeFieldIfFilterReturnsFalse() throws Exception {
        // Given:
        when(fieldFilter.include(field)).thenReturn(false);

        // Then:
        assertThat(context.isExcludedField(field), is(true));
    }

    @Test
    public void shouldNotExcludeFieldIfFilterReturnsTrue() throws Exception {
        // Given:
        when(fieldFilter.include(field)).thenReturn(true);

        // Then:
        assertThat(context.isExcludedField(field), is(false));
    }

    @Test
    public void shouldReturnWalker() throws Exception {
        // Given:
        final Type type = getClass();
        final Inspector expected = mock(Inspector.class);
        when(inspectors.get(type)).thenReturn(expected);

        // Then:
        final Inspector inspector = context.getInspector(type);

        assertThat(inspector, is(expected));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                new WalkerContext(fieldFilter, inspectors),
                new WalkerContext(fieldFilter, inspectors))
            .addEqualityGroup(
                new WalkerContext(mock(FieldFilter.class), inspectors))
            .addEqualityGroup(
                new WalkerContext(fieldFilter, mock(Inspectors.class)))
            .testEquals();
    }

    @Test
    public void shouldThrowNPEsOnConstructorParams() throws Exception {
        new NullPointerTester()
            .setDefault(FieldFilter.class, fieldFilter)
            .setDefault(Inspectors.class, inspectors)
            .testAllPublicConstructors(WalkerContext.class);
    }
}