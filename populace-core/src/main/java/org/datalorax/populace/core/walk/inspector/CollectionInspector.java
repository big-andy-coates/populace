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

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.util.TypeUtils;
import org.datalorax.populace.core.walk.element.RawElement;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An inspector that exposes collections as having no fields, just a collection of child elements
 *
 * @author Andrew Coates - 01/03/2015.
 */
public class CollectionInspector implements Inspector {
    public static final CollectionInspector INSTANCE = new CollectionInspector();
    private static final TypeVariable<Class<Collection>> COLLECTION_TYPE_VARIABLE = Collection.class.getTypeParameters()[0];

    @SuppressWarnings("unchecked")
    private static Collection<?> ensureCollection(final Object instance) {
        Validate.isInstanceOf(Collection.class, instance);
        Validate.isTrue(!Set.class.isAssignableFrom(instance.getClass()), "Set types are not supported");
        return (Collection<?>) instance;
    }

    private static Iterator<RawElement> toRawElements(final Collection<?> collection) {
        final List<RawElement> elements = collection.stream()
            .map(CollectionElement::new)
            .collect(Collectors.toList());
        return elements.iterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<RawElement> getElements(final Object instance, final Inspectors inspectors) {
        final Collection<?> collection = ensureCollection(instance);
        return toRawElements(collection);
    }

    @Override
    public boolean equals(final Object that) {
        return this == that || (that != null && getClass() == that.getClass());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    private static class CollectionElement implements RawElement {
        private final Object element;

        public CollectionElement(final Object element) {
            this.element = element;
        }

        @Override
        public Type getGenericType(final Type containerType) {
            return TypeUtils.getTypeArgument(containerType, Collection.class, COLLECTION_TYPE_VARIABLE);
        }

        @Override
        public Object getValue() {
            return element;
        }

        @Override
        public void setValue(final Object value) {
            throw new UnsupportedOperationException("Collection API is not wide enough to support replacing individual elements.\n" +
                "Likely cause of this exception is an unsupported collection type containing an immutable object.\n" +
                "Consider adding a custom inspector that knows how to replace entries within the custom collection type\n" +
                "Or switch to a logging or terminal inspector for the collection type.");
            // Todo(ac): error message is populate-centric, not walk-centric...
        }
    }
}
