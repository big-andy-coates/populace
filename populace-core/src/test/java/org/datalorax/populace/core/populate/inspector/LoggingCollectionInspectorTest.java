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

package org.datalorax.populace.core.populate.inspector;

import com.google.common.collect.Iterables;
import com.google.common.testing.EqualsTester;
import org.datalorax.populace.core.walk.element.RawElement;
import org.datalorax.populace.core.walk.inspector.Inspector;
import org.datalorax.populace.core.walk.inspector.Inspectors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

public class LoggingCollectionInspectorTest {
    private LoggingCollectionInspector inspector;
    private Inspectors inspectors;

    @BeforeMethod
    public void setUp() throws Exception {
        inspectors = mock(Inspectors.class);

        inspector = LoggingCollectionInspector.INSTANCE;
    }

    @Test
    public void shouldReturnEmptyFields() throws Exception {
        assertThat(Iterables.isEmpty(inspector.getFields(getClass(), inspectors)), is(true));
    }

    @Test
    public void shouldExposeAllCollectionElements() throws Exception {
        // Given:
        final Collection<String> collection = new ArrayList<>();
        collection.add("first");
        collection.add("second");

        // When:
        final List<RawElement> elements = toList(inspector.getElements(collection, inspectors));

        // Then:
        assertThat(elements, hasSize(2));
        assertThat(elements.get(0).getValue(), is("first"));
        assertThat(elements.get(1).getValue(), is("second"));
    }

    @Test
    public void shouldNotBlowUpOnSetValue() throws Exception {
        // Given:
        final Collection<String> collection = new ArrayList<>();
        collection.add("first");
        final RawElement element = inspector.getElements(collection, inspectors).next();

        // When:
        element.setValue("new");

        // Then:
        assertThat("It should actually set the value", element.getValue(), is("first"));
    }

    @Test
    public void shouldNotBlowUpOnSetNullValue() throws Exception {
        // Given:
        final Collection<String> collection = new ArrayList<>();
        collection.add("first");
        final RawElement element = inspector.getElements(collection, inspectors).next();

        // When:
        element.setValue(null);

        // Then:
        assertThat("It should actually set the value", element.getValue(), is("first"));
    }

    @Test
    public void shouldNotBlowUpOnSetNullValueThatIsAlreadyNull() throws Exception {
        // Given:
        final Collection<String> collection = new ArrayList<>();
        collection.add(null);
        final RawElement element = inspector.getElements(collection, inspectors).next();

        // When:
        element.setValue(null);

        // Then:
        assertThat("It should actually set the value", element.getValue(), is(nullValue()));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                LoggingCollectionInspector.INSTANCE,
                new LoggingCollectionInspector())
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