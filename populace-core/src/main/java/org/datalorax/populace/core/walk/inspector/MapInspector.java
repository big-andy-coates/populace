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
 * An inspector that exposes the child entries of the map.
 * <p>
 * Often used in conjunction with {@link org.datalorax.populace.core.walk.inspector.MapEntryInspector}
 *
 * @author Andrew Coates - 01/03/2015.
 */
public class MapInspector implements Inspector {
    public static final MapInspector INSTANCE = new MapInspector();
    private static final TypeVariable<Class<Map>> MAP_KEY_TYPE_VARIABLE = Map.class.getTypeParameters()[0];
    private static final TypeVariable<Class<Map>> MAP_VALUE_TYPE_VARIABLE = Map.class.getTypeParameters()[1];

    @Override
    public Iterator<RawElement> getElements(final Object instance, final Inspectors inspectors) {
        final Map<?, ?> map = ensureMap(instance);
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

    private Iterator<RawElement> toRawElements(final Map<?, ?> map) {
        final List<RawElement> elements = map.entrySet().stream()
            .map(MapElement::new)
            .collect(Collectors.toList());
        return elements.iterator();
    }

    private static Map<?, ?> ensureMap(final Object instance) {
        Validate.isInstanceOf(Map.class, instance);
        return (Map<?, ?>) instance;
    }

    private class MapElement implements RawElement {
        private final Map.Entry<?, ?> entry;

        public MapElement(final Map.Entry<?, ?> entry) {
            Validate.notNull(entry, "entry null");
            this.entry = entry;
        }

        @Override
        public Type getGenericType(final Type containerType) {
            final Type keyTypeArg = TypeUtils.getTypeArgument(containerType, MAP_KEY_TYPE_VARIABLE);
            final Type valueTypeArg = TypeUtils.getTypeArgument(containerType, MAP_VALUE_TYPE_VARIABLE);
            return TypeUtils.parameterise(entry.getClass(), keyTypeArg, valueTypeArg);
        }

        @Override
        public Object getValue() {
            return entry;
        }

        @Override
        public void setValue(final Object value) {
            throw new UnsupportedOperationException("Map.Entry can not be modified");
        }
    }
}
