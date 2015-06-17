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
import org.datalorax.populace.core.walk.field.StdRawField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * An inspector that exposes instances as having fields, but no child elements.
 *
 * @author Andrew Coates - 28/02/2015.
 */
public class FieldInspector implements Inspector {
    public static final Inspector INSTANCE = new FieldInspector();

    @Override
    public Iterable<RawField> getFields(final Class<?> type, final Inspectors inspectors) {
        final List<RawField> collected = new ArrayList<>();
        collectFields(type, inspectors, collected);
        collectSuperFields(type, inspectors, collected);
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

    private void collectFields(final Class<?> type, final Inspectors inspectors, final List<RawField> collected) {
        for (Field field : type.getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }

            collected.add(new StdRawField(field, inspectors.getAnnotationInspector()));
        }
    }

    private void collectSuperFields(final Class<?> type, final Inspectors inspectors, final List<RawField> collected) {
        final Class<?> superClass = type.getSuperclass();
        if (Object.class.equals(superClass)) {
            return;
        }

        final Inspector superInspector = inspectors.get(superClass);
        final Iterable<RawField> superFields = superInspector.getFields(superClass, inspectors);
        for (RawField superField : superFields) {
            collected.add(superField);
        }
    }
}
