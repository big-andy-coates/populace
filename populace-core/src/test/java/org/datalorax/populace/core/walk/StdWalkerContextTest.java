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
import org.datalorax.populace.core.walk.element.ElementInfo;
import org.datalorax.populace.core.walk.field.FieldInfo;
import org.datalorax.populace.core.walk.inspector.Inspector;
import org.datalorax.populace.core.walk.inspector.Inspectors;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StdWalkerContextTest {
    @Mock(name = "main")
    private Predicate<FieldInfo> fieldFilter;
    @Mock(name = "other")
    private Predicate<FieldInfo> fieldFilter2;
    @Mock(name = "main")
    private Predicate<ElementInfo> elementFilter;
    @Mock(name = "other")
    private Predicate<ElementInfo> elementFilter2;
    @Mock
    private Inspectors inspectors;
    @Mock
    private FieldInfo field;
    @Mock
    private ElementInfo element;
    private WalkerContext context;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        context = new StdWalkerContext(fieldFilter, elementFilter, inspectors);
    }

    @Test
    public void shouldExcludeFieldIfFilterReturnsFalse() throws Exception {
        // Given:
        when(fieldFilter.test(field)).thenReturn(false);

        // Then:
        assertThat(context.isExcludedField(field), is(true));
    }

    @Test
    public void shouldIncludeFieldIfFilterReturnsTrue() throws Exception {
        // Given:
        when(elementFilter.test(element)).thenReturn(true);

        // Then:
        assertThat(context.isExcludedElement(element), is(false));
    }

    @Test
    public void shouldExcludeElementIfFilterReturnsFalse() throws Exception {
        // Given:
        when(elementFilter.test(element)).thenReturn(false);

        // Then:
        assertThat(context.isExcludedElement(element), is(true));
    }

    @Test
    public void shouldIncludeElementIfFilterReturnsTrue() throws Exception {
        // Given:
        when(fieldFilter.test(field)).thenReturn(true);

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
                new StdWalkerContext(fieldFilter, elementFilter, inspectors),
                new StdWalkerContext(fieldFilter, elementFilter, inspectors))
            .addEqualityGroup(
                new StdWalkerContext(fieldFilter2, elementFilter, inspectors))
            .addEqualityGroup(
                new StdWalkerContext(fieldFilter, elementFilter2, inspectors))
            .addEqualityGroup(
                new StdWalkerContext(fieldFilter, elementFilter, mock(Inspectors.class)))
            .testEquals();
    }

    @Test
    public void shouldThrowNPEsOnConstructorParams() throws Exception {
        new NullPointerTester()
            .setDefault(Inspectors.class, inspectors)
            .testAllPublicConstructors(StdWalkerContext.class);
    }
}