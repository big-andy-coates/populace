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
    private final Matcher<Object> value;
    private final Matcher<Class<?>> declaringClass;
    private final Matcher<Object> instance;

    private FieldInfoMatcher(final Matcher<String> fieldName, final Matcher<Object> value,
                             final Matcher<Class<?>> declaringClass, final Matcher<Object> expectedInstance) {
        this.fieldName = fieldName;
        this.value = value;
        this.declaringClass = declaringClass;
        this.instance = expectedInstance;
    }

    public static Matcher<? extends FieldInfo> fieldInfo(final String fieldName) {
        return fieldInfo(equalTo(fieldName));
    }

    public static Matcher<? extends FieldInfo> fieldInfo(final Matcher<String> fieldName) {
        return fieldInfo(fieldName, null, null);
    }

    public static Matcher<? extends FieldInfo> fieldInfo(final String fieldName,
                                                         final Class<?> declaringClass) {
        return fieldInfo(equalTo(fieldName), equalTo(declaringClass));
    }

    public static Matcher<? extends FieldInfo> fieldInfo(final Matcher<String> fieldName,
                                                         final Matcher<Class<?>> declaringClass) {
        return fieldInfo(fieldName, declaringClass, null);
    }

    public static Matcher<? extends FieldInfo> fieldInfo(final String fieldName,
                                                         final Class<?> declaringClass,
                                                         final Object instance) {
        return fieldInfo(equalTo(fieldName), equalTo(declaringClass), equalTo(instance));
    }

    public static Matcher<? extends FieldInfo> fieldInfo(final Matcher<String> fieldName,
                                                         final Matcher<Class<?>> declaringClass,
                                                         final Matcher<Object> instance) {
        return new FieldInfoMatcher(fieldName, null, declaringClass, instance);
    }

    public static Matcher<? extends FieldInfo> fieldWithValue(final String fieldName,
                                                              final Object value) {
        return fieldWithValue(equalTo(fieldName), equalTo(value));
    }

    public static Matcher<? extends FieldInfo> fieldWithValue(final Matcher<String> fieldName,
                                                              final Matcher<Object> value) {
        return new FieldInfoMatcher(fieldName, value, null, null);
    }

    public static Matcher<? extends FieldInfo> fieldWithValue(final String fieldName,
                                                              final Object value,
                                                              final Class<?> declaringClass) {
        return fieldWithValue(equalTo(fieldName), equalTo(value), equalTo(declaringClass));
    }

    public static Matcher<? extends FieldInfo> fieldWithValue(final Matcher<String> fieldName,
                                                              final Matcher<Object> value,
                                                              final Matcher<Class<?>> declaringClass) {
        return new FieldInfoMatcher(fieldName, value, declaringClass, null);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Field with name ");
        fieldName.describeTo(description);
        if (value != null) {
            description.appendText(" and value ");
            value.describeTo(description);
        }
        if (declaringClass != null) {
            description.appendText(" and declaringClass ");
            declaringClass.describeTo(description);
        }
        if (instance != null) {
            description.appendText(" and owningInstance");
            instance.describeTo(description);
        }
    }

    @Override
    protected boolean matchesSafely(final FieldInfo info) {
        return fieldName.matches(info.getName())
            && (value == null || value.matches(info.getValue()))
            && (declaringClass == null || declaringClass.matches(info.getDeclaringClass()))
            && (instance == null || instance.matches(info.getOwningInstance()));
    }

    @Override
    protected void describeMismatchSafely(final FieldInfo item, final Description mismatchDescription) {
        if (!fieldName.matches(item.getName())) {
            mismatchDescription.appendText("name was ");
            fieldName.describeMismatch(item, mismatchDescription);
        }
        if (!value.matches(item.getOwningInstance())) {
            mismatchDescription.appendText("value was ");
            value.describeMismatch(item, mismatchDescription);
        }
        if (!declaringClass.matches(item.getOwningInstance())) {
            mismatchDescription.appendText("declaringClass was ");
            declaringClass.describeMismatch(item, mismatchDescription);
        }
        if (!instance.matches(item.getOwningInstance())) {
            mismatchDescription.appendText("owningInstance was ");
            instance.describeMismatch(item, mismatchDescription);
        }
    }
}