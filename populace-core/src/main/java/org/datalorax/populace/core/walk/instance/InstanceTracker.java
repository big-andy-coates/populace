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
 * <b>Note:</b> InstanceTracker requires all fields to be accessible so that it can track their values. It therefore
 * calls {@link org.datalorax.populace.core.walk.field.FieldInfo#ensureAccessible()} on all visited fields.
 *
 * @author Andrew Coates - 29/04/2015.
 */
public class InstanceTracker {
    private final Set<InstanceIdentity> seen = new HashSet<>();

    public Predicate<FieldInfo> getFieldFilter() {
        return f -> {
            f.ensureAccessible();
            return include(f.getValue());
        };
    }

    public Predicate<ElementInfo> getElementFilter() {
        return e -> include(e.getValue());
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
