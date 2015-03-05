package org.datalorax.populace.field;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;

public class FieldInfoMatcher extends TypeSafeMatcher<FieldInfo> {
    private final Field expectedField;
    private final Object expectedInstance;

    public static Matcher<? extends FieldInfo> hasField(final Field field) {
        return new FieldInfoMatcher(field, null);
    }

    public static Matcher<? extends FieldInfo> hasField(final Field field, final Object instance) {
        return new FieldInfoMatcher(field, instance);
    }

    private FieldInfoMatcher(final Field field, final Object expectedInstance) {
        this.expectedField = field;
        this.expectedInstance = expectedInstance;
    }

    @Override
    protected boolean matchesSafely(final FieldInfo info) {
        return fieldMatcher().matches(info.getField()) && objectMatcher().matches(info.getOwningInstance());
    }

    @Override
    public void describeTo(final Description description) {
        fieldMatcher().describeTo(description);
        description.appendText(" and ");
        objectMatcher().describeTo(description);
    }

    @Override
    protected void describeMismatchSafely(final FieldInfo item, final Description mismatchDescription) {
        if (fieldMatcher().matches(item)) {
            objectMatcher().describeMismatch(item, mismatchDescription);
        } else {
            fieldMatcher().describeMismatch(item, mismatchDescription);
        }
    }

    private Matcher<Object> objectMatcher() {
        return expectedInstance != null ? equalTo(expectedInstance) : any(Object.class);
    }

    private Matcher<Field> fieldMatcher() {
        return equalTo(expectedField);
    }
}