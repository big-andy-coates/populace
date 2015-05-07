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

package org.datalorax.populace.core.walk.instance;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;
import org.datalorax.populace.core.walk.element.ElementInfo;
import org.datalorax.populace.core.walk.field.FieldInfo;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InstanceTrackerTest {
    private InstanceTracker tracker;
    private Predicate<FieldInfo> fieldFilter;
    private Predicate<ElementInfo> elementFilter;

    @BeforeMethod
    public void setUp() throws Exception {
        tracker = new InstanceTracker();
        fieldFilter = tracker.getFieldFilter();
        elementFilter = tracker.getElementFilter();
    }

    @Test
    public void shouldIncludeFieldWithUnseenValue() throws Exception {
        // Given:
        final FieldInfo fieldInfo = givenFieldWithValue(new Object());

        // When:
        final boolean include = fieldFilter.test(fieldInfo);

        // Then:
        assertThat("should include", include, is(true));
    }

    @Test
    public void shouldIncludeElementWithUnseenValue() throws Exception {
        // Given:
        final ElementInfo elementInfo = givenElementWithValue(new Object());

        // When:
        final boolean include = elementFilter.test(elementInfo);

        // Then:
        assertThat("should include", include, is(true));
    }

    @Test
    public void shouldExcludeFieldWithValueSeeOnOtherField() throws Exception {
        // Given:
        final String value = "someValue";
        final FieldInfo fieldInfo = givenFieldWithValue(value);
        givenTrackerHasVisitedFieldContaining(tracker, value);

        // When:
        final boolean include = fieldFilter.test(fieldInfo);

        // Then:
        assertThat("should exclude", include, is(false));
    }

    @Test
    public void shouldExcludeFieldWithValueSeeOnOtherElement() throws Exception {
        // Given:
        final String value = "someValue";
        final FieldInfo fieldInfo = givenFieldWithValue(value);
        givenTrackerHasVisitedElementContaining(tracker, value);

        // When:
        final boolean include = fieldFilter.test(fieldInfo);

        // Then:
        assertThat("should exclude", include, is(false));
    }

    @Test
    public void shouldExcludeElementWithValueSeeOnOtherField() throws Exception {
        // Given:
        final String value = "someValue";
        final ElementInfo elementInfo = givenElementWithValue(value);
        givenTrackerHasVisitedFieldContaining(tracker, value);

        // When:
        final boolean include = elementFilter.test(elementInfo);

        // Then:
        assertThat("should exclude", include, is(false));
    }

    @Test
    public void shouldExcludeElementWithValueSeeOnOtherElement() throws Exception {
        // Given:
        final String value = "someValue";
        final ElementInfo elementInfo = givenElementWithValue(value);
        givenTrackerHasVisitedElementContaining(tracker, value);

        // When:
        final boolean include = elementFilter.test(elementInfo);

        // Then:
        assertThat("should exclude", include, is(false));
    }

    @SuppressWarnings("RedundantStringConstructorCall")
    @Test
    public void shouldNotExcludeWhenInstanceIsEqualToOneAlreadySeenButDifferentIdentify() throws Exception {
        // Given:
        final ElementInfo elementInfo = givenElementWithValue(new String("value"));
        givenTrackerHasVisitedElementContaining(tracker, new String("value"));

        // When:
        final boolean include = elementFilter.test(elementInfo);

        // Then:
        assertThat("should include", include, is(true));
    }

    @Test
    public void shouldClearState() throws Exception {
        // Given:
        final Object fieldValue = mock(Object.class, "field");
        final Object elementValue = mock(Object.class, "element");
        final FieldInfo fieldInfo = givenFieldWithValue(fieldValue);
        final ElementInfo elementInfo = givenElementWithValue(elementValue);
        givenTrackerHasVisitedFieldContaining(tracker, fieldValue);
        givenTrackerHasVisitedElementContaining(tracker, elementValue);

        // When:
        tracker.clear();

        // Then:
        assertThat("should include", fieldFilter.test(fieldInfo), is(true));
        assertThat("should include", elementFilter.test(elementInfo), is(true));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        // Given:
        final InstanceTracker one = new InstanceTracker();
        final InstanceTracker two = new InstanceTracker();
        final Object fieldValue = new Object();
        final Object elementValue = new Object();
        givenTrackerHasVisitedFieldContaining(one, fieldValue);
        givenTrackerHasVisitedFieldContaining(two, fieldValue);
        givenTrackerHasVisitedElementContaining(one, elementValue);
        givenTrackerHasVisitedElementContaining(two, elementValue);

        // Then:
        new EqualsTester()
            .addEqualityGroup(
                new InstanceTracker(),
                new InstanceTracker())
            .addEqualityGroup(
                one, two)
            .testEquals();
    }

    @Test
    public void shouldThrowNPEsOnConstructorParams() throws Exception {
        new NullPointerTester()
            .testAllPublicConstructors(InstanceTracker.class);
    }

    private FieldInfo givenFieldWithValue(final Object fieldValue) {
        final FieldInfo fieldInfo = mock(FieldInfo.class);
        when(fieldInfo.getValue()).thenReturn(fieldValue);
        return fieldInfo;
    }

    private ElementInfo givenElementWithValue(final Object elementValue) {
        final ElementInfo elementInfo = mock(ElementInfo.class);
        when(elementInfo.getValue()).thenReturn(elementValue);
        return elementInfo;
    }

    private void givenTrackerHasVisitedFieldContaining(final InstanceTracker tracker, final Object fieldValue) {
        tracker.getFieldFilter().test(givenFieldWithValue(fieldValue));
    }

    private void givenTrackerHasVisitedElementContaining(final InstanceTracker tracker, final Object elementValue) {
        tracker.getElementFilter().test(givenElementWithValue(elementValue));
    }

}