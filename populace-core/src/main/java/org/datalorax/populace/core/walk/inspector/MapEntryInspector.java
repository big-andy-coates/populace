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
import org.datalorax.populace.core.walk.field.GetterSetterRawField;
import org.datalorax.populace.core.walk.field.ImmutableGetterRawField;
import org.datalorax.populace.core.walk.field.RawField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An inspector of Map.Entry implementations that exposes the key and value as child elements.
 * <p>
 * Often used in conjunction with {@link org.datalorax.populace.core.walk.inspector.MapInspector}
 * <p>
 * Be careful if using this inspector with any code that mutates objects in the graph. Mutating the key of the entry
 * may invalidate the owning map and lead to undesirable and undefined behaviour.
 *
 * @author Andrew Coates - 01/03/2015.
 *         <p>
 *         Todo(ac): Should be installed in walker by default. Populator should override.
 */
public class MapEntryInspector implements Inspector {
    public static final MapEntryInspector INSTANCE = new MapEntryInspector();

    @Override
    public Iterable<RawField> getFields(final Class<?> type, final Inspectors inspectors) {
        final Class<? extends Map.Entry<?, ?>> entryType = ensureMapEntry(type);
        final List<RawField> collected = new ArrayList<>();
        collected.add(createKeyField(entryType, inspectors));
        collected.add(createValueField(entryType, inspectors));
        return ImmutableSet.copyOf(collected);
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

    private RawField createValueField(final Class<? extends Map.Entry<?, ?>> type, final Inspectors inspectors) {
        try {
            return new GetterSetterRawField("value",
                type.getDeclaredMethod("getValue"), type.getDeclaredMethod("setValue"),
                inspectors.getAnnotationInspector());
        } catch (NoSuchMethodException e) {
            throw new InspectionException("Expected method 'getKey()' not found on type: " + type, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Map.Entry<?, ?>> ensureMapEntry(final Class<?> type) {
        Validate.isAssignableFrom(Map.Entry.class, type);
        return (Class<? extends Map.Entry<?, ?>>) type;
    }

    private static RawField createKeyField(final Class<? extends Map.Entry<?, ?>> type, final Inspectors inspectors) {
        try {
            return new ImmutableGetterRawField("key",
                type.getDeclaredMethod("getKey"),
                inspectors.getAnnotationInspector());
        } catch (NoSuchMethodException e) {
            throw new InspectionException("Expected method 'getKey()' not found on type: " + type, e);
        }
    }
}
