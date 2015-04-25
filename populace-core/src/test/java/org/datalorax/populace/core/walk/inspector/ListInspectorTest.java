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

package org.datalorax.populace.core.walk.inspector;

import com.google.common.collect.Iterables;
import com.google.common.testing.EqualsTester;
import org.datalorax.populace.core.walk.element.RawElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class ListInspectorTest {
    private ListInspector inspector;
    private Inspectors inspectors;

    @BeforeMethod
    public void setUp() throws Exception {
        inspectors = mock(Inspectors.class);

        inspector = ListInspector.INSTANCE;
    }

    @Test
    public void shouldReturnEmptyFields() throws Exception {
        assertThat(Iterables.isEmpty(inspector.getFields(getClass(), inspectors)), is(true));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIfUnsupportedType() throws Exception {
        inspector.getElements(new HashSet<String>(), inspectors);
    }

    @Test
    public void shouldExposeAllCollectionElements() throws Exception {
        // Given:
        final List<String> list = new ArrayList<>();
        list.add("first");
        list.add("second");

        // When:
        final List<RawElement> elements = toList(inspector.getElements(list, inspectors));

        // Then:
        assertThat(elements, hasSize(2));
        assertThat(elements.get(0).getValue(), is("first"));
        assertThat(elements.get(1).getValue(), is("second"));
    }

    @Test(expectedExceptions = NoSuchElementException.class)
    public void shouldThrowFromNextWhenNoMoreElements() throws Exception {
        // Given:
        final Iterator<RawElement> elements = inspector.getElements(new ArrayList<>(), inspectors);

        // When:
        elements.next();
    }

    @Test
    public void shouldSetValue() throws Exception {
        // Given:
        final List<String> list = new ArrayList<>();
        list.add("a");
        final List<RawElement> elements = toList(inspector.getElements(list, inspectors));

        // When:
        elements.get(0).setValue("new");

        // Then:
        assertThat(list.get(0), is("new"));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                ListInspector.INSTANCE,
                new ListInspector())
            .addEqualityGroup(
                mock(Inspector.class))
            .testEquals();
    }

    private static <T> List<T> toList(final Iterator<T> elements) {
        final List<T> list = new ArrayList<>();
        while (elements.hasNext()) {
            list.add(elements.next());
        }
        return list;
    }
}