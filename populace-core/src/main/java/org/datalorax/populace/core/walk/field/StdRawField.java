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

package org.datalorax.populace.core.walk.field;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.walk.inspector.annotation.AnnotationInspector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * Implementation of RawField that wraps a (@link Field} of a class.
 *
 * @author Andrew Coates - 04/03/2015.
 */
public class StdRawField implements RawField {
    private final Field field;
    private final transient AnnotationInspector annotationInspector;

    public StdRawField(final Field field, final AnnotationInspector annotationInspector) {
        Validate.notNull(field, "field null");
        Validate.notNull(annotationInspector, "annotationInspector null");
        this.field = field;
        this.annotationInspector = annotationInspector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return field.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getType() {
        return field.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type getGenericType() {
        return field.getGenericType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(final Object owningInstance) throws ReflectiveOperationException {
        return field.get(owningInstance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(final Object owningInstance, final Object value) throws ReflectiveOperationException {
        field.set(owningInstance, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> type) {
        return annotationInspector.getAnnotation(field, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccessible() {
        return field.isAccessible();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ensureAccessible() {
        field.setAccessible(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTransient() {
        return Modifier.isTransient(field.getModifiers());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStatic() {
        return Modifier.isStatic(field.getModifiers());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFinal() {
        return Modifier.isFinal(field.getModifiers());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final StdRawField that = (StdRawField) o;
        return field.equals(that.field);
    }

    @Override
    public int hashCode() {
        return field.hashCode();
    }

    @Override
    public String toString() {
        return field.toString();
    }
}
