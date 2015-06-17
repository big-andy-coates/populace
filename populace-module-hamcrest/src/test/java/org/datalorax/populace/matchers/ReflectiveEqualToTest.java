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

package org.datalorax.populace.matchers;

import com.sun.jmx.remote.internal.ArrayQueue;
import org.hamcrest.Description;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.datalorax.populace.matchers.ReflectiveEqualTo.reflectiveEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
public class ReflectiveEqualToTest {

    private Description mismatch;

    @BeforeMethod
    public void setUp() throws Exception {
        mismatch = mockDescription();
    }

    @Test
    public void shouldDetectWhenInstancesEqual() throws Exception {
        // Given:
        class SomeType {
            String field;
            List<String> list = new ArrayList<>();
        }

        final SomeType expected = new SomeType();
        expected.field = "v1";
        expected.list.add("v2");
        final SomeType actual = new SomeType();
        actual.field = "v1";
        actual.list.add("v2");

        // Then:
        assertThat(actual, is(reflectiveEqualTo(expected)));
    }

    @Test
    public void shouldDetectFieldValueDifferent() throws Exception {
        // Given:
        class SomeType {
            String field;
        }

        final SomeType expected = new SomeType();
        expected.field = "expected";
        final SomeType actual = new SomeType();
        actual.field = "actual";

        // Then:
        assertThat(actual, is(not(reflectiveEqualTo(expected))));
    }

    @Test
    public void shouldDescribeValueDifferences() throws Exception {
        // Given:
        class SomeType {
            String field;
        }

        final SomeType expected = new SomeType();
        expected.field = "expected";
        final SomeType actual = new SomeType();
        actual.field = "actual";

        // When:
        reflectiveEqualTo(expected).describeMismatch(actual, mismatch);

        // Then:
        verify(mismatch).appendValue(expected.field);
        verify(mismatch).appendValue(actual.field);
    }

    @Test
    public void shouldDescribeValueDifferencesWithActualNull() throws Exception {
        // Given:
        class SomeType {
            String field;
        }

        final SomeType expected = new SomeType();
        expected.field = "expected";
        final SomeType actual = new SomeType();

        // When:
        reflectiveEqualTo(expected).describeMismatch(actual, mismatch);

        // Then:
        verify(mismatch).appendValue(expected.field);
        verify(mismatch).appendValue(null);
    }

    @Test
    public void shouldDescribeValueDifferencesWithExpectedNull() throws Exception {
        // Given:
        class SomeType {
            String field;
        }

        final SomeType expected = new SomeType();
        final SomeType actual = new SomeType();
        actual.field = "actual";

        // When:
        reflectiveEqualTo(expected).describeMismatch(actual, mismatch);

        // Then:
        verify(mismatch).appendValue(null);
        verify(mismatch).appendValue(actual.field);
    }

    @Test
    public void shouldDetectFieldTypeDifference() throws Exception {
        // Given:
        class SomeType {
            Object field;
        }

        final SomeType expected = new SomeType();
        expected.field = "expected";
        final SomeType actual = new SomeType();
        actual.field = 10;

        // Then:
        assertThat(actual, is(not(reflectiveEqualTo(expected))));
    }

    @Test
    public void shouldDescribeFieldTypeDifference() throws Exception {
        // Given:
        class SomeType {
            Object field;
        }

        final SomeType expected = new SomeType();
        expected.field = "expected";
        final SomeType actual = new SomeType();
        actual.field = 10;

        // When:
        reflectiveEqualTo(expected).describeMismatch(actual, mismatch);

        // Then:
        verify(mismatch).appendValue(expected.field.getClass());
        verify(mismatch).appendValue(actual.field.getClass());
    }

    @Test
    public void shouldDetectDifferenceInContainerType() throws Exception {
        // Given:
        class SomeType {
            List<String> list;
        }

        final SomeType expected = new SomeType();
        expected.list = new ArrayList<>();
        expected.list.add("v1");
        final SomeType actual = new SomeType();
        actual.list = new ArrayQueue<>(10);
        actual.list.add("v1");

        // Then:
        assertThat(actual, is(not(reflectiveEqualTo(expected))));
    }

    @Test
    public void shouldDescribeDifferenceInContainerType() throws Exception {
        // Given:
        class SomeType {
            List<String> list;
        }

        final SomeType expected = new SomeType();
        expected.list = new ArrayList<>();
        expected.list.add("v1");
        final SomeType actual = new SomeType();
        actual.list = new ArrayQueue<>(10);
        actual.list.add("v1");

        // When:
        reflectiveEqualTo(expected).describeMismatch(actual, mismatch);

        // Then:
        verify(mismatch).appendValue(expected.list.getClass());
        verify(mismatch).appendValue(actual.list.getClass());
    }

    @Test
    public void shouldDetectMissingComponent() throws Exception {
        // Given:
        class SomeType {
            List<Object> list = new ArrayList<>();
        }

        final SomeType expected = new SomeType();
        expected.list.add("expected");
        final SomeType actual = new SomeType();

        // Then:
        assertThat(actual, is(not(reflectiveEqualTo(expected))));
    }

    @Test
    public void shouldDescribeMissingComponent() throws Exception {
        // Given:
        class SomeType {
            List<Object> list = new ArrayList<>();
        }

        final SomeType expected = new SomeType();
        expected.list.add("expected");
        final SomeType actual = new SomeType();

        // When:
        reflectiveEqualTo(expected).describeMismatch(actual, mismatch);

        // Then:
        verify(mismatch).appendText(" expected value has unmatched element");
    }

    @Test
    public void shouldDetectExtraComponent() throws Exception {
        // Given:
        class SomeType {
            List<Object> list = new ArrayList<>();
        }

        final SomeType expected = new SomeType();
        final SomeType actual = new SomeType();
        actual.list.add("extra");

        // Then:
        assertThat(actual, is(not(reflectiveEqualTo(expected))));
    }

    @Test
    public void shouldDescribeExtraComponent() throws Exception {
        // Given:
        class SomeType {
            List<Object> list = new ArrayList<>();
        }

        final SomeType expected = new SomeType();
        final SomeType actual = new SomeType();
        actual.list.add("extra");

        // When:
        reflectiveEqualTo(expected).describeMismatch(actual, mismatch);

        // Then:
        verify(mismatch).appendText(" actual value has unmatched element");
    }

    @Test
    public void shouldIncludePathInDescription() throws Exception {
        // Given:
        class SomeType {
            List<Object> list = new ArrayList<>();
        }

        final SomeType expected = new SomeType();
        expected.list.add("expected");
        final SomeType actual = new SomeType();

        // When:
        reflectiveEqualTo(expected).describeMismatch(actual, mismatch);

        // Then:
        verify(mismatch).appendText(SomeType.class.getSimpleName() + ".list[0]");
    }

    @Test
    public void shouldDetectMismatchesOnSuperTypeFields() throws Exception {
        // Given:
        class SuperType {
            String field;
        }
        class SubType extends SuperType {
        }

        final SubType expected = new SubType();
        expected.field = "expected";
        final SubType actual = new SubType();
        actual.field = "actual";

        // Then:
        assertThat(actual, is(not(reflectiveEqualTo(expected))));
    }

    @Test
    public void shouldDetectMismatchEvenIfEqualsMethodIsWrong() throws Exception {
        // Given:
        class SomeType {
            String field;

            @Override
            public boolean equals(final Object o) {
                return true;
            }

            @Override
            public int hashCode() {
                return 1;
            }
        }

        final SomeType expected = new SomeType();
        expected.field = "expected";
        final SomeType actual = new SomeType();
        actual.field = "actual";

        // Then:
        assertThat(actual, is(not(reflectiveEqualTo(expected))));
    }

    @Test
    public void shouldDetectDifferenceOnOrderedSetsWithEntriesInDifferentOrder() throws Exception {
        // Given:
        class SomeType {
            Set<String> field = new CopyOnWriteArraySet<>();
        }

        final SomeType expected = new SomeType();
        expected.field.add("v1");
        expected.field.add("v2");
        expected.field.add("v3");
        final SomeType actual = new SomeType();
        actual.field.add("v3");
        actual.field.add("v2");
        actual.field.add("v1");

        final List<String> l1 = asList(expected.field.iterator());
        final List<String> l2 = asList(actual.field.iterator());
        assertThat("precondition violated", l1, is(not(l2)));

        // Then:
        assertThat(actual, is(not(reflectiveEqualTo(expected))));
    }

    @Test
    public void shouldDescribeDifferenceOnOrderedMapsWithEntriesInDifferentOrder() throws Exception {
        // Given:
        class SomeType {
            Set<String> field = new CopyOnWriteArraySet<>();
        }

        final SomeType expected = new SomeType();
        expected.field.add("v1");
        expected.field.add("v2");
        expected.field.add("v3");
        final SomeType actual = new SomeType();
        actual.field.add("v3");
        actual.field.add("v2");
        actual.field.add("v1");

        final List<String> l1 = asList(expected.field.iterator());
        final List<String> l2 = asList(actual.field.iterator());
        assertThat("precondition violated", l1, is(not(l2)));

        // When:
        reflectiveEqualTo(expected).describeMismatch(actual, mismatch);

        // Then:
        InOrder inOrder = inOrder(mismatch);
        inOrder.verify(mismatch).appendText("SomeType.field[0]");
        inOrder.verify(mismatch).appendText(argThat(containsString("expected")));
        inOrder.verify(mismatch).appendValue("v1");
        inOrder.verify(mismatch).appendText(argThat(containsString("actual")));
        inOrder.verify(mismatch).appendValue("v3");

        inOrder.verify(mismatch).appendText("SomeType.field[2]");
        inOrder.verify(mismatch).appendText(argThat(containsString("expected")));
        inOrder.verify(mismatch).appendValue("v3");
        inOrder.verify(mismatch).appendText(argThat(containsString("actual")));
        inOrder.verify(mismatch).appendValue("v1");

        inOrder.verify(mismatch, never()).appendValue("v2");
    }

    @Test
    public void shouldDetectDifferenceOnMapsWithDifferentKeys() throws Exception {
        // Given:
        class SomeType {
            Map<Integer, String> field = new HashMap<>();
        }

        final SomeType expected = new SomeType();
        expected.field.put(1, "v1");
        expected.field.put(2, "v2");
        expected.field.put(3, "v3");
        final SomeType actual = new SomeType();
        actual.field.put(4, "v3");
        actual.field.put(3, "v2");
        actual.field.put(1, "v1");

        final List<String> l1 = asList(expected.field.values().iterator());
        final List<String> l2 = asList(actual.field.values().iterator());
        assertThat("precondition violated", l1, is(l2));

        // Then:
        assertThat(actual, is(not(reflectiveEqualTo(expected))));
    }

    @Test
    public void shouldDetectDifferenceOnOrderedMapsWithEntriesInDifferentOrder() throws Exception {
        // Given:
        class SomeType {
            Map<Integer, String> field;
        }

        final SomeType expected = new SomeType();
        expected.field = new LinkedHashMap<>();
        expected.field.put(1, "v1");
        expected.field.put(2, "v2");
        expected.field.put(3, "v3");
        final SomeType actual = new SomeType();
        actual.field = new LinkedHashMap<>();
        actual.field.put(3, "v3");
        actual.field.put(2, "v2");
        actual.field.put(1, "v1");

        final List<String> values1 = asList(expected.field.values().iterator());
        final List<String> values2 = asList(actual.field.values().iterator());
        assertThat("precondition violated - on keys", expected.field.keySet(), is(actual.field.keySet()));
        assertThat("precondition violated - on values", values1, is(not(values2)));

        // Then:
        assertThat(actual, is(not(reflectiveEqualTo(expected))));
    }

    @Test
    public void shouldDescribeDifferenceInOrderedMapsWithEntriesInDifferentOrder() throws Exception {
        // Given:
        class SomeType {
            Map<Integer, String> field;
        }

        final SomeType expected = new SomeType();
        expected.field = new LinkedHashMap<>();
        expected.field.put(1, "v1");
        expected.field.put(2, "v2");
        expected.field.put(3, "v3");
        final SomeType actual = new SomeType();
        actual.field = new LinkedHashMap<>();
        actual.field.put(3, "v3");
        actual.field.put(2, "v2");
        actual.field.put(1, "v1");

        final List<String> values1 = asList(expected.field.values().iterator());
        final List<String> values2 = asList(actual.field.values().iterator());
        assertThat("precondition violated - on keys", expected.field.keySet(), is(actual.field.keySet()));
        assertThat("precondition violated - on values", values1, is(not(values2)));

        // When:
        reflectiveEqualTo(expected).describeMismatch(actual, mismatch);

        // Then:
        InOrder inOrder = inOrder(mismatch);
        inOrder.verify(mismatch).appendText("SomeType.field[0]");
        inOrder.verify(mismatch).appendText(argThat(containsString("expected")));
        inOrder.verify(mismatch).appendValue(expected.field.get(1));
        inOrder.verify(mismatch).appendText(argThat(containsString("actual")));
        inOrder.verify(mismatch).appendValue(expected.field.get(3));

        inOrder.verify(mismatch).appendText("SomeType.field[2]");
        inOrder.verify(mismatch).appendText(argThat(containsString("expected")));
        inOrder.verify(mismatch).appendValue(expected.field.get(3));
        inOrder.verify(mismatch).appendText(argThat(containsString("actual")));
        inOrder.verify(mismatch).appendValue(expected.field.get(1));

        inOrder.verify(mismatch, never()).appendValue(expected.field.get(2));
    }

    @Test
    public void shouldWorkWithMapKeysWithHashCodeAndEqualsThatMissFields() throws Exception {
        // Given:
        class KeyType {
            String field;
            int otherField;

            public KeyType(final int hashCode, final String field) {
                this.otherField = hashCode;
                this.field = field;
            }

            @Override
            public int hashCode() {
                return otherField;
            }

            @Override
            public boolean equals(Object other) {
                return true;
            }

            @Override
            public String toString() {
                return "KeyType{'" + field + "\'," + otherField + '}';
            }
        }
        class SomeType {
            Map<KeyType, String> field = new HashMap<>();
        }

        final SomeType expected = new SomeType();
        expected.field.put(new KeyType(1, "live parrot"), "value");
        expected.field.put(new KeyType(2, "sick parrot"), "value");
        expected.field.put(new KeyType(3, "dead parrot"), "value");
        final SomeType actual = new SomeType();
        actual.field.put(new KeyType(1, "fork handles"), "value");
        actual.field.put(new KeyType(2, "four candles"), "value");
        actual.field.put(new KeyType(3, "handle's for forks"), "value");

        // Then:
        assertThat(actual, is(not(reflectiveEqualTo(expected))));
        // Todo(ac): To fix this we need an innspector for maps that exposes the keys as well as the values as children.
    }

    @SuppressWarnings("UnusedDeclaration")
    @Test
    public void shouldNotGetStreamsOutOfSyncOnMismatchedNullField() throws Exception {
        // Given:
        class SomeOtherType {
            String field = "value";
        }
        class SomeType {
            SomeOtherType field1 = new SomeOtherType();
            SomeOtherType field2 = new SomeOtherType();
            SomeOtherType field3 = new SomeOtherType();
        }

        final SomeType expected = new SomeType();
        expected.field1 = null;

        final SomeType actual = new SomeType();
        actual.field2 = null;

        // When:
        reflectiveEqualTo(expected).describeMismatch(actual, mismatch);

        // Then:
        // It didn't throw an exception because the streams were out of sync.
    }

    @SuppressWarnings("UnusedDeclaration")
    @Test
    public void shouldNotGetStreamsOutOfSyncOnMismatchedElements() throws Exception {
        // Given:
        class SomeOtherType {
            String field = "value";
        }
        class SomeType {
            List<SomeOtherType> field = new ArrayList<>();
        }

        final SomeType expected = new SomeType();
        expected.field.add(null);
        expected.field.add(new SomeOtherType());
        expected.field.add(new SomeOtherType());

        final SomeType actual = new SomeType();
        expected.field.add(new SomeOtherType());
        expected.field.add(null);
        expected.field.add(new SomeOtherType());

        // When:
        reflectiveEqualTo(expected).describeMismatch(actual, mismatch);

        // Then:
        // It didn't throw an exception because the streams were out of sync.
        assertThat(expected, is(reflectiveEqualTo(actual)));
    }

    @SuppressWarnings("UnusedDeclaration")
    @Test
    public void shouldNotRecurseIntoUnmatchedChildElements() throws Exception {
        // Given:
        class SomeOtherType {
            String childField = "value";
        }
        class SomeType {
            List<SomeOtherType> field = new ArrayList<>();
        }

        final SomeType expected = new SomeType();
        expected.field.add(null);
        expected.field.add(new SomeOtherType());

        final SomeType actual = new SomeType();
        actual.field.add(new SomeOtherType());
        actual.field.add(null);

        // When:
        reflectiveEqualTo(expected).describeMismatch(actual, mismatch);

        // Then:
        verify(mismatch, never()).appendText(argThat(containsString("childField")));
    }

    @SuppressWarnings("UnusedDeclaration")
    @Test
    public void shouldNotRecurseIntoElementsIfOneSideIsEmpty() throws Exception {
        // Given:
        class SomeOtherType {
            String childValue = "value";
        }
        class SomeType {
            List<SomeOtherType> list1 = new ArrayList<>();
            List<SomeOtherType> list2 = new ArrayList<>();
        }

        final SomeType expected = new SomeType();
        expected.list1.add(new SomeOtherType());

        final SomeType actual = new SomeType();
        actual.list2.add(new SomeOtherType());

        // When:
        reflectiveEqualTo(expected).describeMismatch(actual, mismatch);

        // Then:
        verify(mismatch, never()).appendText(argThat(containsString("childValue")));
    }

    private static List<String> asList(final Iterator<String> iterator) {
        final ArrayList<String> values = new ArrayList<>();
        while (iterator.hasNext()) {
            values.add(iterator.next());
        }
        return values;
    }

    private static Description mockDescription() {
        final Description desc = mock(Description.class);
        when(desc.appendText(anyString())).thenReturn(desc);
        when(desc.appendValue(anyObject())).thenReturn(desc);
        return desc;
    }
}