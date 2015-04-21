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
import org.datalorax.populace.core.walk.element.RawElement;
import org.datalorax.populace.core.walk.field.RawField;

import java.util.Collections;
import java.util.Iterator;

/**
 * Interface for 'pluggable' walking of fields. Inspectors for non-collection types should just implement
 * {@link Inspector#getFields}. Inspectors for collection types e.g. arrays, lists, maps, etc, should
 * provide implementation for {@link Inspector#getElements(Object, Inspectors)}. Hybrid inspectors that return both
 * fields and children are supported.
 *
 * @author Andrew Coates - 28/02/2015.
 */
public interface Inspector {
    /**
     * Return the set of fields this instance supports
     *
     * @param type the type to inspect
     * @param inspectors all the inspectors configured in the walker.
     * @return the set of fields this type supports
     */
    default Iterable<RawField> getFields(final Class<?> type, final Inspectors inspectors) {
        return ImmutableSet.of();
    }

    /**
     * If the type the inspector supports is a collection, the this method will return the collection of its child elements.
     *
     * @param instance the instance.
     * @param inspectors all the inspectors configured in the walker.
     * @return the set of child elements, or an empty collection for non-collection types.
     */
    default Iterator<RawElement> getElements(final Object instance, final Inspectors inspectors) {
        return Collections.emptyIterator();
    }
    // Todo(aC): getElements should probably take either a RawField or a FieldInfo object, rather than instance, as this would allow elements to know their container type, and maintain the link to the field.  Might want a base class here, rather than an interface, to ensure the field is maintained etc.
}
