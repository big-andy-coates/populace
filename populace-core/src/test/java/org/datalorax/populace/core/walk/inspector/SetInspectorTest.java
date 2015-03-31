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

import org.datalorax.populace.core.walk.element.RawElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SetInspectorTest {
    private SetInspector inspector;

    private static List<RawElement> collectAll(Iterator<RawElement> it) {
        final List<RawElement> elements = new ArrayList<>();
        while (it.hasNext()) {
            elements.add(it.next());
        }
        return elements;
    }

    @BeforeMethod
    public void setUp() throws Exception {
        inspector = SetInspector.INSTANCE;
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnUnsupportedType() throws Exception {
        // When:
        inspector.getElements(new ArrayList<String>(), null);
    }

    @Test
    public void shouldReturnElements() throws Exception {
        // Given:
        final Set<String> set = new HashSet<>();
        set.add("one");
        set.add("two");

        // When:
        final Iterator<RawElement> elements = inspector.getElements(set, null);

        // Then:
        assertThat(collectAll(elements), hasSize(2));
    }

    @Test
    public void shouldBeAbleToGetElementValue() throws Exception {
        // Given:
        final Set<String> set = new HashSet<>();
        set.add("one");

        // When:
        final Iterator<RawElement> elements = inspector.getElements(set, null);

        // Then:
        assertThat(elements.next().getValue(), is("one"));
    }

    @Test
    public void shouldBeAbleToSetElementValue() throws Exception {
        // Given:
        final Set<String> set = new HashSet<>();
        set.add("one");
        final RawElement element = inspector.getElements(set, null).next();

        // When:
        element.preWalk();
        element.setValue("new");
        element.postWalk();

        // Then:
        assertThat(set, contains("new"));
    }

    @Test
    public void shouldNotInvalidateSetWhenSettingImmutableValues() throws Exception {
        // Given:
        final Set<String> set = new HashSet<>();
        set.add("one");
        set.add("two");

        final List<RawElement> elements = collectAll(inspector.getElements(set, null));

        // When:
        elements.get(1).preWalk();
        elements.get(1).setValue("one");
        elements.get(1).postWalk();

        // Then:
        assertThat(set, hasSize(1));
        assertThat(set, contains("one"));
    }

    @Test
    public void shouldNotInvalidateSetWhenSettingMutableValues() throws Exception {
        // Given:
        class SomeMutableType {
            String field;

            SomeMutableType(final String initial) {
                this.field = initial;
            }

            @Override
            public boolean equals(final Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                final SomeMutableType that = (SomeMutableType) o;
                return field.equals(that.field);
            }

            @Override
            public int hashCode() {
                return field.hashCode();
            }
        }

        final Set<SomeMutableType> set = new HashSet<>();
        final SomeMutableType one = new SomeMutableType("one");
        final SomeMutableType two = new SomeMutableType("two");
        set.add(one);
        set.add(two);

        final List<RawElement> elements = collectAll(inspector.getElements(set, null));

        // When:
        elements.get(1).preWalk();
        two.field = "one";
        elements.get(1).postWalk();

        // Then:
        assertThat(set, hasSize(1));
        assertThat(set, contains(new SomeMutableType("one")));
        final Set<SomeMutableType> copy = new HashSet<>(set);
        assertThat(set, is(equalTo(copy)));
        assertThat(copy, is(equalTo(set)));
    }

    @Test
    public void shouldHandleReadingExistingNullElements() throws Exception {
        // Given:
        final Set<String> set = new HashSet<>();
        set.add(null);

        // When:
        final RawElement element = inspector.getElements(set, null).next();

        // Then:
        assertThat(element.getValue(), is(nullValue()));
    }

    @Test
    public void shouldHandleSettingExistingNullElements() throws Exception {
        // Given:
        final Set<String> set = new HashSet<>();
        set.add(null);
        final RawElement element = inspector.getElements(set, null).next();

        // When:
        element.preWalk();
        element.setValue("new");
        element.postWalk();

        // Then:
        assertThat(set, contains("new"));
    }

    @Test
    public void shouldHandleSettingElementToNull() throws Exception {
        // Given:
        final Set<String> set = new HashSet<>();
        set.add("not null");
        final RawElement element = inspector.getElements(set, null).next();

        // When:
        element.preWalk();
        element.setValue(null);
        element.postWalk();

        // Then:
        assertThat(element.getValue(), is(nullValue()));
        assertThat(set, contains(nullValue()));
    }
}