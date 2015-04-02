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

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An inspector that exposes arrays as having no fields, just a collection of child elements
 *
 * @author Andrew Coates - 27/02/2015.
 */
public class ArrayInspector implements Inspector {
    public static final Inspector INSTANCE = new ArrayInspector();

    @Override
    public Iterator<RawElement> getElements(final Object instance, final Inspectors inspectors) {
        Validate.isTrue(instance.getClass().isArray(), "Expected array type, got: " + instance.getClass());
        final int length = Array.getLength(instance);

        return new Iterator<RawElement>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index != length;
            }

            @Override
            public RawElement next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return new ArrayElement(index++, instance);
            }
        };
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

    private class ArrayElement implements RawElement {
        private final int index;
        private final Object array;

        public ArrayElement(final int index, final Object array) {
            Validate.isTrue(index >= 0, "negative index");
            this.index = index;
            this.array = array;
        }

        @Override
        public Type getGenericType(final Type containerType) {
            return TypeUtils.getArrayComponentType(containerType);
        }

        @Override
        public Object getValue() {
            return Array.get(array, index);
        }

        @Override
        public void setValue(final Object value) {
            Array.set(array, index, value);
        }
    }
}
