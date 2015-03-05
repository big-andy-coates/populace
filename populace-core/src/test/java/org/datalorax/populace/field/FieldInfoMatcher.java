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