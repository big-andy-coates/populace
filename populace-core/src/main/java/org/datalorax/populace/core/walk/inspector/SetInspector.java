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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An inspector that exposes {@code sets} as having no fields, just a collection of child elements.
 * <p>
 * The class returns a {@link org.datalorax.populace.core.walk.element.RawElement} implementation that can mutate
 * {@code set} elements.
 * <p>
 * To maintain the validity of the {@link Set} the implementation of {@link org.datalorax.populace.core.walk.element.RawElement#setValue(Object)}
 * used by this class must copy entire set into another set and back again, on each operation. This can be slow. If this
 * proves to be an issue for you, consider if it is possible to add a more specific inspector for your use-case.
 *
 * @author Andrew Coates - 01/03/2015.
 */
public class SetInspector implements Inspector {
    public static final SetInspector INSTANCE = new SetInspector();
    private static final TypeVariable<Class<Set>> SET_TYPE_VARIABLE = Set.class.getTypeParameters()[0];

    @SuppressWarnings("unchecked")
    private static Set<Object> ensureSet(final Object instance) {
        Validate.isInstanceOf(Set.class, instance);
        return (Set<Object>) instance;
    }

    private static Iterator<RawElement> toRawElements(final Set<Object> set) {
        final ArrayList<RawElement> elements = set.stream()
            .map(e -> new SetElement(e, set))
            .collect(Collectors.toCollection(ArrayList::new));
        return elements.iterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<RawElement> getElements(final Object instance, final Inspectors inspectors) {
        final Set<Object> set = ensureSet(instance);
        return toRawElements(set);
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

    private static class SetElement implements RawElement {
        private final Set<Object> set;
        private Object element;

        public SetElement(final Object element, final Set<Object> set) {
            this.element = element;
            this.set = set;
        }

        @Override
        public Type getGenericType(final Type containerType) {
            return TypeUtils.getTypeArgument(containerType, Set.class, SET_TYPE_VARIABLE);
        }

        @Override
        public Object getValue() {
            return element;
        }

        @Override
        public void setValue(final Object value) {
            // Just store the value for now. postWalk will ensure its part of the set.
            element = value;
        }

        @Override
        public void preWalk() {
            // To stop the set being invalid while it is being walked, and potentially changed, remove it from the set:
            set.remove(element);
        }

        @Override
        public void postWalk() {
            // The the element has been changed in-place then the set may be invalid.
            // Copy all elements out and back in again to restore validity.
            final List<Object> copy = new ArrayList<>(set);
            set.clear();
            set.addAll(copy);
            set.add(element);
        }
    }
}
