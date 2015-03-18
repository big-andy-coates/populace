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
import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.walk.field.RawField;

import java.lang.reflect.Array;
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
    public Iterable<RawField> getFields(final Object instance) {
        return ImmutableSet.of();
    }

    @Override
    public boolean typeIsCollection() {
        return true;
    }

    @Override
    public Iterable<?> getChildren(final Object array) {
        Validate.isTrue(array.getClass().isArray(), "Expected array type, got: " + array.getClass());

        return () -> {
            final int length = Array.getLength(array);
            return new Iterator<Object>() {
                int index = 0;

                @Override
                public boolean hasNext() {
                    return index != length;
                }

                @Override
                public Object next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return Array.get(array, index++);
                }
            };
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
}
