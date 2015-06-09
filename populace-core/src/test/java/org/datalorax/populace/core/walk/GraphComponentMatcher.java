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

package org.datalorax.populace.core.walk;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * @author Andrew Coates - 01/06/2015.
 */
public class GraphComponentMatcher<T extends GraphComponent> extends TypeSafeMatcher<GraphComponent> {
    private final Class<T> type;
    private final Matcher<? extends GraphComponent> matcher;

    public GraphComponentMatcher(final Class<T> type, final Matcher<? extends T> matcher) {
        this.type = type;
        this.matcher = matcher;
    }

    public static <T extends GraphComponent>
    Matcher<GraphComponent> isComponentMatching(final Class<T> type,
                                                final Matcher<? extends T> matcher) {
        return new GraphComponentMatcher<T>(type, matcher);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText(" a ").appendValue(type);
        description.appendText(" that ").appendDescriptionOf(matcher);
    }

    @Override
    protected boolean matchesSafely(final GraphComponent item) {
        return type.isAssignableFrom(item.getClass()) && matcher.matches(item);
    }
}
