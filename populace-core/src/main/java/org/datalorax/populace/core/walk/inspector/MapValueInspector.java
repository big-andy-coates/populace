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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The walker walks each value in the collection, depth first. Keys are not walked.
 *
 * @author Andrew Coates - 01/03/2015.
 */
public class MapValueInspector implements Inspector {
    public static final MapValueInspector INSTANCE = new MapValueInspector();
    private static final TypeVariable<Class<Map>> MAP_VALUE_TYPE_VARIABLE = Map.class.getTypeParameters()[1];

    @Override
    public Iterator<RawElement> getElements(final Object instance, final Inspectors inspectors) {
        final Map<?, Object> map = ensureMap(instance);
        return toRawElements(map);
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

    private Iterator<RawElement> toRawElements(final Map<?, Object> map) {
        final List<RawElement> elements = map.entrySet().stream()
            .map(MapElement::new)
            .collect(Collectors.toList());
        return elements.iterator();
    }

    @SuppressWarnings("unchecked")
    private static Map<?, Object> ensureMap(final Object instance) {
        Validate.isInstanceOf(Map.class, instance);
        return (Map<?, Object>) instance;
    }

    private class MapElement implements RawElement {
        private final Map.Entry<?, Object> entry;

        public MapElement(final Map.Entry<?, Object> entry) {
            Validate.notNull(entry, "entry null");
            this.entry = entry;
        }

        @Override
        public Type getGenericType(final Type containerType) {
            return TypeUtils.getTypeArgument(containerType, MAP_VALUE_TYPE_VARIABLE);
        }

        @Override
        public Object getValue() {
            return entry.getValue();
        }

        @Override
        public void setValue(final Object value) {
            entry.setValue(value);
        }
    }
}
