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

import com.google.common.testing.EqualsTester;
import org.datalorax.populace.core.walk.field.RawField;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MapEntryInspectorTest {
    @Mock
    private Inspectors inspectors;
    @Mock
    private Map.Entry<String, Integer> entry;
    private MapEntryInspector inspector;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        inspector = MapEntryInspector.INSTANCE;
    }

    @Test
    public void shouldReturnEmptyElements() throws Exception {
        assertThat(inspector.getElements(entry, inspectors).hasNext(), is(false));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIfUnsupportedType() throws Exception {
        inspector.getFields(HashMap.Entry.class, inspectors);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldExposeEntryKeyAndValue() throws Exception {
        // Given:
        when(entry.getKey()).thenReturn("theKey");
        when(entry.getValue()).thenReturn(22);

        // When:
        final Map<String, RawField> fields = toFieldMap(inspector.getFields(Map.Entry.class, inspectors));

        // Then:
        assertThat(fields.keySet(), containsInAnyOrder("key", "value"));
        assertThat(fields.get("key").getValue(entry), is("theKey"));
        assertThat(fields.get("value").getValue(entry), is(22));
    }

    @Test(expectedExceptions = NoSuchElementException.class)
    public void shouldThrowFromNextWhenNoMoreElements() throws Exception {
        // Given:
        final Iterable<RawField> fields = inspector.getFields(HashMap.Entry.class, inspectors);

        // When:
        fields.iterator().next();
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void shouldThrowOnSetKey() throws Exception {
        // Given:
        final Map<String, Integer> map = new HashMap<>();
        map.put("oldValue", 22);
        final Map<String, RawField> fields = toFieldMap(inspector.getFields(Map.Entry.class, inspectors));

        // When:
        fields.get("key").setValue(map, "newValue");

        // Then:
        // It throws
    }

    @Test
    public void shouldAllowSetValue() throws Exception {
        // Given:
        final Map<Integer, String> map = new HashMap<>();
        map.put(1, "oldValue");
        final Map<String, RawField> fields = toFieldMap(inspector.getFields(Map.Entry.class, inspectors));

        // When:
        fields.get("value").setValue(map, "newValue");

        // Then:
        assertThat(map.get(1), is("newValue"));
    }

    @Test
    public void shouldGetGenericKeyType() throws Exception {
        // Given:
        final RawField field = toFieldMap(inspector.getFields(Map.Entry.class, inspectors)).get("key");

        // When:
        final Type genericType = field.getGenericType();

        // Then:
        assertThat(genericType, is(Map.Entry.class.getTypeParameters()[0]));
    }

    @Test
    public void shouldGetGenericValueType() throws Exception {
        // Given:
        final RawField field = toFieldMap(inspector.getFields(Map.Entry.class, inspectors)).get("value");

        // When:
        final Type genericType = field.getGenericType();

        // Then:
        assertThat(genericType, is(Map.Entry.class.getTypeParameters()[1]));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                MapInspector.INSTANCE,
                new MapInspector())
            .addEqualityGroup(
                mock(Inspector.class))
            .testEquals();
    }

    private static Map<String, RawField> toFieldMap(final Iterable<RawField> fields) {
        return StreamSupport.stream(fields.spliterator(), false)
            .collect(Collectors.toMap(RawField::getName, field -> field));
    }
}