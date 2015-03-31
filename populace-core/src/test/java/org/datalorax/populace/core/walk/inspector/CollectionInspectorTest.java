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

import org.datalorax.populace.core.CustomCollection;
import org.datalorax.populace.core.walk.element.RawElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CollectionInspectorTest {
    private CollectionInspector inspector;

    private static List<RawElement> collectAll(Iterator<RawElement> it) {
        final List<RawElement> elements = new ArrayList<>();
        while (it.hasNext()) {
            elements.add(it.next());
        }
        return elements;
    }

    @BeforeMethod
    public void setUp() throws Exception {
        inspector = CollectionInspector.INSTANCE;
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnUnsupportedType() throws Exception {
        // When:
        inspector.getElements("not a collection", null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnSetType() throws Exception {
        // When:
        inspector.getElements(new HashSet<>(), null);
    }

    @Test
    public void shouldReturnElements() throws Exception {
        // Given:
        final Collection<String> collection = new CustomCollection<>();
        collection.add("one");
        collection.add("two");

        // When:
        final Iterator<RawElement> elements = inspector.getElements(collection, null);

        // Then:
        assertThat(collectAll(elements), hasSize(2));
    }

    @Test
    public void shouldBeAbleToGetElementValue() throws Exception {
        // Given:
        final Collection<String> collection = new CustomCollection<>();
        collection.add("one");

        // When:
        final Iterator<RawElement> elements = inspector.getElements(collection, null);

        // Then:
        assertThat(elements.next().getValue(), is("one"));
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void shouldThrowOnSetElement() throws Exception {
        // Given:
        final Collection<String> collection = new CustomCollection<>();
        collection.add("bob");
        final Iterator<RawElement> elements = inspector.getElements(collection, null);

        // When:
        elements.next().setValue("hello");
    }

    @Test
    public void shouldHandleExistingNullElements() throws Exception {
        // Given:
        final Collection<String> collection = new CustomCollection<>();
        collection.add(null);

        // When:
        final List<RawElement> elements = collectAll(inspector.getElements(collection, null));

        // Then:
        assertThat(elements.get(0).getValue(), is(nullValue()));
    }
}