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
import org.datalorax.populace.core.util.GetterValidator;
import org.datalorax.populace.core.walk.inspector.annotation.AnnotationInspector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Implementation of RawField that wraps only a getter on a class and is therefore immutable.
 *
 * @author Andrew Coates - 12/06/2015.
 */
public class ImmutableGetterRawField implements RawField {
    private final String name;
    private final Method getter;
    private final transient AnnotationInspector annotationInspector;

    public ImmutableGetterRawField(final String name, final Method getter, final AnnotationInspector annotationInspector) {
        this(name, getter, annotationInspector, new GetterValidator());
    }

    ImmutableGetterRawField(final String name, final Method getter,
                            final AnnotationInspector annotationInspector, final GetterValidator validator) {
        Validate.notEmpty(name, "name empty");
        Validate.notNull(getter, "getter null");
        Validate.notNull(annotationInspector, "annotationInspector null");
        validator.validate(getter);
        this.name = name;
        this.getter = getter;
        this.annotationInspector = annotationInspector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getDeclaringClass() {
        return getter.getDeclaringClass();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getType() {
        return getter.getReturnType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type getGenericType() {
        return getter.getGenericReturnType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(final Object owningInstance) throws ReflectiveOperationException {
        return getter.invoke(owningInstance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(final Object owningInstance, final Object value) throws ReflectiveOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> type) {
        return annotationInspector.getAnnotation(type, getter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccessible() {
        return getter.isAccessible();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ensureAccessible() {
        getter.setAccessible(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTransient() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStatic() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ImmutableGetterRawField that = (ImmutableGetterRawField) o;
        return getter.equals(that.getter) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        return 31 * result + getter.hashCode();
    }

    @Override
    public String toString() {
        return "ImmutableGetterRawField{" +
            "name='" + name + '\'' +
            ", getter=" + getter + '}';
    }
}
