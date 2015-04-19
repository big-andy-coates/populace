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

package org.datalorax.populace.core.util;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.lang.reflect.*;

/**
 * @author Andrew Coates - 15/04/2015.
 */
public final class TypeMatchers {
    private TypeMatchers() {
    }

    public static Matcher<? super Type> typeEqualTo(final Type type) {
        return new TypeEqualTo(type);
    }

    public static class TypeEqualTo extends TypeSafeDiagnosingMatcher<Type> {
        private final Type expected;

        public TypeEqualTo(final Type type) {
            expected = type;
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("a type equal to ").appendValue(expected.getTypeName());
        }

        @Override
        protected boolean matchesSafely(final Type item, final Description mismatchDescription) {
            return matches(expected, item, "", mismatchDescription);
        }

        private static boolean matches(final Type expected, final Type actual, final String path, final Description mismatchDescription) {
            if (expected == null) {
                if (actual == null) {
                    return true;
                }

                mismatchDescription.appendText("expected null at " + path + ". Actual: ").appendValue(actual);
                return false;
            }
            if (expected instanceof Class) {
                return matchesClass((Class) expected, actual, path, mismatchDescription);
            }
            if (expected instanceof ParameterizedType) {
                return matchesParameterizedType((ParameterizedType) expected, actual, path, mismatchDescription);
            }
            if (expected instanceof TypeVariable) {
                return matchesTypeVariable((TypeVariable) expected, actual, path, mismatchDescription);
            }
            if (expected instanceof WildcardType) {
                return matchesWildcardType((WildcardType) expected, actual, path, mismatchDescription);
            }
            if (expected instanceof GenericArrayType) {
                return matchesGenericArrayType((GenericArrayType) expected, actual, path, mismatchDescription);
            }

            mismatchDescription.appendText(" item was not a supported type:").appendValue(actual.getClass());
            return false;
        }

        private static boolean matchesClass(final Class expected, final Type item, final String path,
                                            final Description mismatchDescription) {
            if (expected.equals(item)) {
                return true;
            }

            mismatchDescription.appendText(" mismatch at " + path + ", expected: ").appendValue(expected)
                .appendText(" found: ").appendValue(item);
            return false;
        }

        @SuppressWarnings("SimplifiableIfStatement")
        private static boolean matchesParameterizedType(final ParameterizedType expected, final Type item,
                                                        final String path, final Description mismatchDescription) {
            if (!(item instanceof ParameterizedType)) {
                mismatchDescription.appendText(" expected ParameterizedType at " + path + ". Actual: ").appendValue(item);
                return false;
            }

            final ParameterizedType actual = (ParameterizedType) item;
            if (!matches(expected.getRawType(), actual.getRawType(), path + "rawType", mismatchDescription)) {
                return false;
            }

            if (!matches(expected.getOwnerType(), actual.getOwnerType(), path + "owningType", mismatchDescription)) {
                return false;
            }

            return typeArraysMatch(expected.getActualTypeArguments(), actual.getActualTypeArguments(),
                expected.getTypeName(), actual.getTypeName(), "typeArgument", path, mismatchDescription);
        }

        private static boolean matchesTypeVariable(final TypeVariable expected, final Type item, final String path,
                                                   final Description mismatchDescription) {
            if (!(item instanceof TypeVariable)) {
                mismatchDescription.appendText(" expected TypeVariable at " + path + ". Actual: ").appendValue(item);
                return false;
            }

            final TypeVariable actual = (TypeVariable) item;
            if (!expected.getName().equals(actual.getName())) {
                mismatchDescription.appendText(" at " + path + ". Expected: ").appendValue(expected.getTypeName())
                    .appendText(". Actual: ").appendValue(actual);
                return false;
            }

            return typeArraysMatch(expected.getBounds(), actual.getBounds(),
                expected.getTypeName(), actual.getTypeName(), "bound", path, mismatchDescription);
        }

        private static boolean matchesWildcardType(final WildcardType expected, final Type item, final String path,
                                                   final Description mismatchDescription) {
            if (!(item instanceof WildcardType)) {
                mismatchDescription.appendText(" expected WildcardType at " + path + ". Actual: ").appendValue(item);
                return false;
            }

            final WildcardType actual = (WildcardType) item;
            return typeArraysMatch(expected.getLowerBounds(), actual.getLowerBounds(), expected.getTypeName(), actual.getTypeName(), "lowerBound", path, mismatchDescription) &&
                typeArraysMatch(expected.getUpperBounds(), actual.getUpperBounds(), expected.getTypeName(), actual.getTypeName(), "upperBound", path, mismatchDescription);
        }

        private static boolean matchesGenericArrayType(final GenericArrayType expected, final Type item, final String path,
                                                       final Description mismatchDescription) {

            if (!(item instanceof GenericArrayType)) {
                mismatchDescription.appendText(" expected GenericArrayType at " + path + ". Actual: ").appendValue(item);
                return false;
            }

            final GenericArrayType actual = (GenericArrayType) item;
            return matches(expected.getGenericComponentType(), actual.getGenericComponentType(), path + "componentType", mismatchDescription);
        }

        private static boolean typeArraysMatch(final Type[] expected, final Type[] actual,
                                               final String expectedTypeName, final String actualTypeName,
                                               final String elementName, final String path,
                                               final Description mismatchDescription) {
            if (actual.length != expected.length) {
                mismatchDescription.appendText(" number of " + elementName + "s differed at " + path + ". Expected:")
                    .appendValue(expectedTypeName).appendText(". Actual:").appendValue(actualTypeName);
                return false;
            }

            for (int i = 0; i != actual.length; ++i) {
                if (!matches(actual[i], expected[i], path + elementName + "[" + i + "]", mismatchDescription)) {
                    return false;
                }
            }
            return true;
        }
    }
}
