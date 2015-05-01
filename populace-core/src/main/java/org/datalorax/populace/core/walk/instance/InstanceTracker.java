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
import org.datalorax.populace.core.walk.element.filter.ElementFilter;
import org.datalorax.populace.core.walk.field.FieldInfo;
import org.datalorax.populace.core.walk.field.filter.FieldFilter;

import java.util.HashSet;
import java.util.Set;

/**
 * Tracks instances that have been walked and excludes them from being walked again.
 * <p>
 * If a single instance of this class is installed as both a
 * {@link org.datalorax.populace.core.walk.field.filter.FieldFilter field filter} and an
 * {@link org.datalorax.populace.core.walk.element.filter.ElementFilter element filter} for a
 * {@link org.datalorax.populace.core.walk.GraphWalker graph walker} then if the same instance is seen more than once
 * during the walk, it will be ignored and not walked again.
 * <p>
 * The tracker can be installed like this:
 * <p>
 * <pre>
 *     {@code
 *     InstanceTracker tracker = new InstanceTracker();
 *     GraphWalker.Builder builder = GraphWalker.newBuilder();
 *     GraphWalker walker = builder
 *        .withFieldFilter(FieldFilters.and(builder.getFieldFilter(), tracker))
 *        .withElementFilter(ElementFilters.and(builder.getElementFilter(), tracker))
 *        .build();
 *     }
 * </pre>
 *
 * @author Andrew Coates - 29/04/2015.
 */
public class InstanceTracker implements FieldFilter, ElementFilter {
    private final Set<InstanceIdentity> seen = new HashSet<>();

    @Override
    public boolean include(final FieldInfo field) {
        field.ensureAccessible();           // Todo(ac): ... hummm... either document or just have this do this in one place!
        return include(field.getValue());
    }

    @Override
    public boolean include(final ElementInfo element) {
        return include(element.getValue());
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
