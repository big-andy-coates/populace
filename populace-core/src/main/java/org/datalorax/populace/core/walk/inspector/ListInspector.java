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

/**
 * An inspector that exposes {@link java.util.List} as having no fields, just a collection of child elements
 *
 * @author Andrew Coates - 01/03/2015.
 */
public class ListInspector implements Inspector {
    public static final Inspector INSTANCE = new ListInspector();
    private static final TypeVariable<Class<List>> LIST_TYPE_VARIABLE = List.class.getTypeParameters()[0];
    private static final TypeVariable<Class<Collection>> COLLECTION_TYPE_VARIABLE = Collection.class.getTypeParameters()[0];

    @SuppressWarnings("unchecked")
    private static List<Object> ensureList(final Object instance) {
        Validate.isInstanceOf(List.class, instance);
        return (List<Object>) instance;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<RawElement> getElements(final Object instance, final Inspectors inspectors) {
        final List<Object> collection = ensureList(instance);
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

    private Iterator<RawElement> toRawElements(final List<Object> list) {
        final int size = list.size();

        return new Iterator<RawElement>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public RawElement next() {
                return new ListElement(index++, list);
            }
        };
    }

    private static class ListElement implements RawElement {
        private final List<Object> list;
        private final int index;

        public ListElement(final int index, final List<Object> list) {
            this.index = index;
            this.list = list;
        }

        @Override
        public Type getGenericType(final Type containerType) {
            final Class<?> rawType = TypeUtils.getRawType(containerType, null);
            if (List.class.isAssignableFrom(rawType)) {
                return TypeUtils.getTypeArgument(containerType, List.class, LIST_TYPE_VARIABLE);
            }
            return TypeUtils.getTypeArgument(containerType, Collection.class, COLLECTION_TYPE_VARIABLE);
        }

        @Override
        public Object getValue() {
            return list.get(index);
        }

        @Override
        public void setValue(final Object value) {
            list.set(index, value);
        }
    }
}
