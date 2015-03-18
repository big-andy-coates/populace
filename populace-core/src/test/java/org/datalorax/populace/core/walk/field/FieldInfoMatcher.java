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

package org.datalorax.populace.core.walk.field;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.equalTo;

public class FieldInfoMatcher extends TypeSafeMatcher<FieldInfo> {
    private final Matcher<String> fieldName;
    private final Matcher<Class<?>> declaringClass;
    private final Matcher<Object> instance;

    private FieldInfoMatcher(final Matcher<String> fieldName, final Matcher<Class<?>> declaringClass, final Matcher<Object> expectedInstance) {
        this.fieldName = fieldName;
        this.declaringClass = declaringClass;
        this.instance = expectedInstance;
    }

    public static Matcher<? extends FieldInfo> hasField(final String fieldName) {
        return hasField(equalTo(fieldName));
    }

    public static Matcher<? extends FieldInfo> hasField(final Matcher<String> fieldName) {
        return hasField(fieldName, null, null);
    }

    public static Matcher<? extends FieldInfo> hasField(final String fieldName,
                                                        final Class<?> declaringClass) {
        return hasField(equalTo(fieldName), equalTo(declaringClass));
    }

    public static Matcher<? extends FieldInfo> hasField(final Matcher<String> fieldName,
                                                        final Matcher<Class<?>> declaringClass) {
        return hasField(fieldName, declaringClass, null);
    }

    public static Matcher<? extends FieldInfo> hasField(final String fieldName,
                                                        final Class<?> declaringClass,
                                                        final Object instance) {
        return hasField(equalTo(fieldName), equalTo(declaringClass), equalTo(instance));
    }

    public static Matcher<? extends FieldInfo> hasField(final Matcher<String> fieldName,
                                                        final Matcher<Class<?>> declaringClass,
                                                        final Matcher<Object> instance) {
        return new FieldInfoMatcher(fieldName, declaringClass, instance);
    }

    @Override
    protected boolean matchesSafely(final FieldInfo info) {
        return fieldName.matches(info.getName())
            && (declaringClass == null || declaringClass.matches(info.getDeclaringClass()))
            && (instance == null || instance.matches(info.getOwningInstance()));
    }

    @Override
    public void describeTo(final Description description) {
        fieldName.describeTo(description);
        if (instance != null) {
            description.appendText(" and ");
            instance.describeTo(description);
        }
    }

    @Override
    protected void describeMismatchSafely(final FieldInfo item, final Description mismatchDescription) {
        if (fieldName.matches(item)) {
            instance.describeMismatch(item, mismatchDescription);
        } else {
            fieldName.describeMismatch(item, mismatchDescription);
        }
    }
}