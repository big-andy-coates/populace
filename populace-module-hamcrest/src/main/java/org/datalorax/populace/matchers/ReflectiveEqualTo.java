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

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.util.StreamUtils;
import org.datalorax.populace.core.walk.GraphComponent;
import org.datalorax.populace.core.walk.GraphWalker;
import org.datalorax.populace.core.walk.field.FieldInfo;
import org.datalorax.populace.core.walk.field.filter.FieldFilters;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Hamcrest Matcher that uses recursion to compare the fields of an object, rather than relying on a potentially buggy
 * hashCode or equals methods
 *
 * @author Andrew Coates - 12/05/2015.
 */
public class ReflectiveEqualTo<T> extends TypeSafeMatcher<T> {
    private final T expected;
    private final GraphWalker walker;

    /**
     * Construct a matcher using the supplied walker.
     *
     * @param expected the value to compare too.
     * @param walker   the walker to use to walk the fields and elements of the expected and actual objects
     */
    public ReflectiveEqualTo(final T expected, final GraphWalker walker) {
        Validate.notNull(walker, "walker null");
        this.expected = expected;
        this.walker = walker;
    }

    /**
     * Construct a matcher using a default walker instance, which will exclude static and transient fields from
     * comparison
     *
     * @param expected the value to compare too.
     * @param <T>      the type of the entity to compare
     * @return the new matcher
     */
    public static <T> Matcher<T> reflectiveEqualTo(final T expected) {
        return reflectiveEqualTo(expected,
            FieldFilters.excludeStaticFields().and(FieldFilters.excludeTransientFields()));
    }

    /**
     * Construct a matcher using a walker configured with the supplied field filter. Note: the filter will be logically
     * AND'd with one that excludes static fields.
     *
     * @param expected    the value to compare too.
     * @param fieldFilter the filter that will control what fields are included and excluded from the comparison
     * @param <T>         the type of the entity to compare
     * @return the new matcher
     */
    public static <T> Matcher<T> reflectiveEqualTo(final T expected, final Predicate<? super FieldInfo> fieldFilter) {
        final GraphWalker walker = GraphWalker.newBuilder()
            .withFieldFilter(FieldFilters.excludeStaticFields().and(fieldFilter))
            .build();
        return reflectiveEqualTo(expected, walker);
    }

    /**
     * Construct a matcher using the supplied walker.
     *
     * @param expected the value to compare too.
     * @param walker   the walker to use to walk the fields and elements of the expected and actual objects
     * @param <T>      the type of the entity to compare
     * @return the new matcher
     */
    public static <T> Matcher<T> reflectiveEqualTo(final T expected, final GraphWalker walker) {
        return new ReflectiveEqualTo<T>(expected, walker);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendValue(this.expected);
        description.appendText(", using walker: ").appendValue(walker);
    }

    @Override
    protected boolean matchesSafely(final T actual) {
        class NoOpVisitor implements Visitor {
            @Override
            public void componentMissing(final String path,
                                         final Optional<GraphComponent> expected, final Optional<GraphComponent> actual) {
            }

            @Override
            public void valueDifference(final String path,
                                        final Optional<Object> expected, final Optional<Object> actual) {
            }

            @Override
            public void typeDifference(final String path, final Class<?> expected, final Class<?> actual) {
            }
        }

        final Visitor visitor = new NoOpVisitor();
        final Stream<GraphComponent> expectedComponents = walker.walk(expected, GraphWalker.Customisations.empty());
        final Stream<GraphComponent> actualComponents = walker.walk(actual, GraphWalker.Customisations.empty());

        return !StreamUtils.outerZip(expectedComponents, actualComponents, Spliterator.ORDERED)
            .filter(pair -> isMismatched(pair.getFirst(), pair.getSecond(), visitor))
            .findAny().isPresent();
    }

    @Override
    protected void describeMismatchSafely(final T actual, final Description mismatchDescription) {
        if (actual == null || expected == null) {
            mismatchDescription.appendText((actual == null ? "Actual" : "Expected") + " value was null");
            return;
        }

        mismatchDescription.appendText("found the following differences:\n");

        class MismatchVisitor implements Visitor {
            @Override
            public void componentMissing(final String path,
                                         final Optional<GraphComponent> expected, final Optional<GraphComponent> actual) {
                // Todo(ac): Can we avoid if we compare all collection/map/array types for size?
                // Fields can't be out of whack, so its only elements. But how to make this extensible for custom container types?
                // The walker knows it has elements. The matcher could get access to the same walker context and request the
                // stream of elements....
                mismatchDescription.appendText("\t\t").appendText(path);

                if (expected.isPresent()) {
                    mismatchDescription
                        .appendText(" expected value has unmatched element");
                } else {
                    mismatchDescription
                        .appendText(" actual value has unmatched element");
                }

                mismatchDescription.appendText("\n");
            }

            @Override
            public void valueDifference(final String path,
                                        final Optional<Object> expected, final Optional<Object> actual) {
                mismatchDescription.appendText("\t\t").appendText(path)
                    .appendText(" expected: ").appendValue(expected.map(Object::toString).orElse(null))
                    .appendText(", actual: ").appendValue(actual.map(Object::toString).orElse(null))
                    .appendText("\n");
            }

            @Override
            public void typeDifference(final String path, final Class<?> expected, final Class<?> actual) {
                mismatchDescription.appendText("\t\t").appendText(path)
                    .appendText(" expectedType: ").appendValue(expected)
                    .appendText(", actualType: ").appendValue(actual)
                    .appendText("\n");
            }
        }

        final Stream<GraphComponent> expectedComponents = walker.walk(expected, GraphWalker.Customisations.empty());
        final Stream<GraphComponent> actualComponents = walker.walk(actual, GraphWalker.Customisations.empty());
        final Visitor visitor = new MismatchVisitor();

        StreamUtils.outerZip(expectedComponents, actualComponents, Spliterator.ORDERED)
            .forEach(pair -> isMismatched(pair.getFirst(), pair.getSecond(), visitor));
    }

    private static <T extends Visitor> boolean isMismatched(final Optional<GraphComponent> expected,
                                                            final Optional<GraphComponent> actual,
                                                            final T visitor) {
        if (!expected.isPresent() || !actual.isPresent()) {
            final String path = expected.map(GraphComponent::getPath).orElse(actual.map(GraphComponent::getPath).orElse(""));
            visitor.componentMissing(path, expected, actual);
            return true;
        }

        final GraphComponent expectedComponent = expected.get();
        final GraphComponent actualComponent = actual.get();
        final String expectedPath = expectedComponent.getPath();
        if (!expectedPath.equals(actualComponent.getPath())) {
            throw new IllegalStateException(String.format("Graph components out of sink! expectedPath: %s, actualPath: %s",
                expectedPath, actualComponent.getPath()));   // Todo(ac): Need to terminate the comparison or work out how to avoid...
        }

        return isMismatchedValue(expectedPath, Optional.ofNullable(expectedComponent.getValue()),
            Optional.ofNullable(actualComponent.getValue()),
            visitor);
    }

    private static <T extends Visitor> boolean isMismatchedValue(final String path,
                                                                 final Optional<Object> possibleExpected,
                                                                 final Optional<Object> possibleActual,
                                                                 final T visitor) {
        final Object expected = possibleExpected.orElse(null);
        final Object actual = possibleActual.orElse(null);

        if (expected == actual) {
            return false;
        }

        if (expected == null || actual == null) {
            visitor.valueDifference(path, possibleExpected, possibleActual);
            return true;
        }

        if (!expected.getClass().equals(actual.getClass())) {
            visitor.typeDifference(path, expected.getClass(), actual.getClass());
            return true;
        }

        if (expected.getClass().isArray()) {
            // Array walk will compare elements
            return false;
        }

        if (!expected.equals(actual)) {
            visitor.valueDifference(path, possibleExpected, possibleActual);
            return true;
        }
        return false;
    }

    private interface Visitor {
        void componentMissing(final String path, final Optional<GraphComponent> expected, final Optional<GraphComponent> actual);

        void valueDifference(final String path, final Optional<Object> expected, final Optional<Object> actual);

        void typeDifference(final String path, final Class<?> expected, final Class<?> actual);
    }
}