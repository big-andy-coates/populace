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

package org.datalorax.populace.field;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Field;

public class FieldInfoMatcher extends TypeSafeMatcher<FieldInfo> {
    private final Matcher<Field> fieldMatcher;
    private final Matcher<Object> instanceMatcher;

    public static Matcher<? extends FieldInfo> hasField(final Matcher<Field> field) {
        return new FieldInfoMatcher(field, null);
    }

    public static Matcher<? extends FieldInfo> hasField(final Matcher<Field> field, final Matcher<Object> instance) {
        return new FieldInfoMatcher(field, instance);
    }

    private FieldInfoMatcher(final Matcher<Field> field, final Matcher<Object> expectedInstance) {
        this.fieldMatcher = field;
        this.instanceMatcher = expectedInstance;
    }

    @Override
    protected boolean matchesSafely(final FieldInfo info) {
        return fieldMatcher.matches(info.getField()) && (instanceMatcher == null || instanceMatcher.matches(info.getOwningInstance()));
    }

    @Override
    public void describeTo(final Description description) {
        fieldMatcher.describeTo(description);
        if (instanceMatcher != null) {
            description.appendText(" and ");
            instanceMatcher.describeTo(description);
        }
    }

    @Override
    protected void describeMismatchSafely(final FieldInfo item, final Description mismatchDescription) {
        if (fieldMatcher.matches(item)) {
            instanceMatcher.describeMismatch(item, mismatchDescription);
        } else {
            fieldMatcher.describeMismatch(item, mismatchDescription);
        }
    }
}