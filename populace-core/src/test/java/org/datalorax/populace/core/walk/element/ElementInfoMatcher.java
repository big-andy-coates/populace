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

package org.datalorax.populace.core.walk.element;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.equalTo;

public class ElementInfoMatcher extends TypeSafeMatcher<ElementInfo> {
    private final Matcher<Class<?>> elementType;
    private final Matcher<Object> value;

    private ElementInfoMatcher(final Matcher<Class<?>> elementType, final Matcher<Object> value) {
        this.elementType = elementType;
        this.value = value;
    }

    public static Matcher<? extends ElementInfo> elementOfType(final Class<?> elementType) {
        return elementOfType(equalTo(elementType));
    }

    public static Matcher<? extends ElementInfo> elementOfType(final Matcher<Class<?>> elementType) {
        return new ElementInfoMatcher(elementType, null);
    }

    public static Matcher<? extends ElementInfo> elementWithValue(final Object value) {
        return elementWithValue(equalTo(value));
    }

    public static Matcher<? extends ElementInfo> elementWithValue(final Matcher<Object> value) {
        return new ElementInfoMatcher(null, value);
    }

    @Override
    protected boolean matchesSafely(final ElementInfo info) {
        return (elementType == null || elementType.matches(info.getValue().getClass())) &&
            (value == null || value.matches(info.getValue()));
    }

    @Override
    public void describeTo(final Description description) {
        if (elementType != null) {
            description.appendText("elementType ");
            elementType.describeTo(description);
        }
        if (value != null) {
            description.appendText(" and value ");
            value.describeTo(description);
        }
    }

    @Override
    protected void describeMismatchSafely(final ElementInfo item, final Description mismatchDescription) {
        if (elementType != null && !elementType.matches(item.getValue().getClass())) {
            mismatchDescription.appendText("elementType was ");
            elementType.describeMismatch(item, mismatchDescription);
        }

        if (value != null && !value.matches(item.getValue())) {
            mismatchDescription.appendText("value was ");
            value.describeMismatch(item, mismatchDescription);
        }
    }
}