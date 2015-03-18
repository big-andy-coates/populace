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

package org.datalorax.populace.core.walk.field.filter;

import org.datalorax.populace.core.walk.field.RawField;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * @author Andrew Coates - 18/03/2015.
 */
public class RawFieldMatcher extends TypeSafeDiagnosingMatcher<RawField> {
    private final Class<?> type;
    private final String name;

    public RawFieldMatcher(final Class<?> type, final String name) {
        this.type = type;
        this.name = name;
    }

    public static Matcher<? super RawField> rawField(final Class<?> type, final String fieldName) {
        return new RawFieldMatcher(type, fieldName);
    }

    @Override
    protected boolean matchesSafely(final RawField item, final Description mismatchDescription) {
        boolean match = true;
        if (!name.equals(item.getName())) {
            match = false;
            mismatchDescription.appendText(" Actual field name ").appendValue(item.getName());
        }
        if (!type.equals(item.getDeclaringClass())) {
            match = false;
            if (type.getSimpleName().equals(item.getDeclaringClass().getSimpleName())) {
                mismatchDescription.appendText(" Actual type ").appendValue(item.getDeclaringClass());
            } else {
                mismatchDescription.appendText(" Actual type ").appendValue(item.getDeclaringClass().getSimpleName());
            }
        }
        return match;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("A field named ").appendValue(name).appendText(" on type ").appendValue(type.getSimpleName());
    }
}
