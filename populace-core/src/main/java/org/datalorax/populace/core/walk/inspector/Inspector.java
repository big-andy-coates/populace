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

package org.datalorax.populace.core.walk.inspector;

import com.google.common.collect.ImmutableSet;
import org.datalorax.populace.core.walk.field.RawField;

/**
 * Interface for 'pluggable' walking of fields. Inspectors for non-collection types should just implement
 * {@link Inspector#getFields}. Inspectors for collection types e.g. arrays, lists, maps, etc, should
 * override {@link Inspector#typeIsCollection()} to return true and provide implementation for {@link Inspector#getChildren(Object)}.
 *
 * @author Andrew Coates - 28/02/2015.
 */
public interface Inspector {
    /**
     * Return the set of fields this instance supports
     *
     * @param type the type to inspect
     * @param inspectors all the inspectors configured in the system.
     * @return the set of fields this type supports
     */
    Iterable<RawField> getFields(final Class<?> type, final Inspectors inspectors);

    /**
     * @return true if the type the inspector supports is a collection type, false otherwise
     */
    default boolean typeIsCollection() {
        return false;
    }

    /**
     * If the type the inspector supports is a collection, the this method will return the collection of its child elements.
     *
     * @param instance the instance.
     * @return the set of child elements, or an empty collection for non-collection types.
     */
    default Iterable<?> getChildren(final Object instance) {
        return ImmutableSet.of();
    }
}
