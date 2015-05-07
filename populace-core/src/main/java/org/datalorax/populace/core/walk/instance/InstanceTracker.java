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

package org.datalorax.populace.core.walk.instance;

import org.datalorax.populace.core.walk.element.ElementInfo;
import org.datalorax.populace.core.walk.field.FieldInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Tracks instances that have been walked and excludes them from being walked again.
 *
 * If a single instance of this class is installed as both a field and instance filter for a
 * {@link org.datalorax.populace.core.walk.GraphWalker graph walker} it will track each object visited, (i.e. field values
 * and elements), and exclude them from the walk should they be encountered as second time.
 *
 * The tracker can be installed like this:
 *
 * <pre>
 *     {@code
 *     InstanceTracker tracker = new InstanceTracker();
 *     GraphWalker.Builder builder = GraphWalker.newBuilder();
 *     GraphWalker walker = builder
 *        .withFieldFilter(builder.getFieldFilter().and(tracker.getFieldFilter()))
 *        .withElementFilter(builder.getElementFilter().and(tracker.getElementFilter()))
 *        .build();
 *     }
 * </pre>
 *
 * InstanceTracker requires all fields to be accessible so that it can track their values. It therefore
 * calls {@link org.datalorax.populace.core.walk.field.FieldInfo#ensureAccessible()} on all visited fields.
 *
 * InstanceTracker is stateful. If you don't want the result of one walk to affect another, then please call
 * {@link InstanceTracker#clear()} between walks.
 *
 * InstanceTracker is not thread-safe. If you need a thread-safe tracker, then please implement your own using a
 * thread-safe set implementation.
 *
 * @author Andrew Coates - 29/04/2015.
 */
public class InstanceTracker {
    private final Set<InstanceIdentity> seen = new HashSet<>();

    /**
     * Get the field filter of the tracker.
     * <p>
     * The filter will add the value of all fields it encounters to the set of seen instances, and exclude any instances
     * previously seen as either a field or element value.
     * <p>
     * <b>Note:</b> This method requires all fields to be accessible so that it can track their values. It therefore
     * calls {@link org.datalorax.populace.core.walk.field.FieldInfo#ensureAccessible()} on all visited fields.
     *
     * @return the field filter of the tracker.
     */
    public Predicate<FieldInfo> getFieldFilter() {
        return f -> {
            f.ensureAccessible();
            return include(f.getValue());
        };
    }

    /**
     * Get the element filter of the tracker.
     *
     * The filter will add the value of all elements it encounters to the set of seen instances, and exclude any instances
     * previously seen as either a field or element value.
     *
     * @return the element filter of the tracker.
     */
    public Predicate<ElementInfo> getElementFilter() {
        return e -> include(e.getValue());
    }

    /**
     * Clear the state of the tracker.
     * <p>
     * This method can be useful to clear the state of the tracker between
     * {@link org.datalorax.populace.core.walk.GraphWalker#walk} calls.
     */
    public void clear() {
        seen.clear();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final InstanceTracker that = (InstanceTracker) o;
        return seen.equals(that.seen);
    }

    @Override
    public int hashCode() {
        return seen.hashCode();
    }

    @Override
    public String toString() {
        return "InstanceTracker{seen=" + seen + '}';
    }

    private boolean include(final Object value) {
        return value == null || seen.add(new InstanceIdentity(value));
    }
}
