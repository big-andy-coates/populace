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
import org.datalorax.populace.core.util.GetterSetterValidator;
import org.datalorax.populace.core.util.TypeUtils;
import org.datalorax.populace.core.walk.inspector.annotation.AnnotationInspector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Implementation of RawField that wraps getter and setter methods on a class
 *
 * @author Andrew Coates - 12/06/2015.
 */
public class GetterSetterRawField implements RawField {
    private final String name;
    private final Method getter;
    private final Method setter;
    private final transient AnnotationInspector annotationInspector;

    public GetterSetterRawField(final String name, final Method getter, final Method setter, final AnnotationInspector annotationInspector) {
        this(name, getter, setter, annotationInspector, new GetterSetterValidator());
    }

    GetterSetterRawField(final String name, final Method getter, final Method setter,
                         final AnnotationInspector annotationInspector, GetterSetterValidator validator) {
        Validate.notEmpty(name, "name empty");
        Validate.notNull(getter, "getter null");
        Validate.notNull(setter, "setter null");
        Validate.notNull(annotationInspector, "annotationInspector null");
        validator.validate(getter, setter);
        this.name = name;
        this.getter = getter;
        this.setter = setter;
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
        return TypeUtils.getMostDerivedClass(getter.getDeclaringClass(), setter.getDeclaringClass());
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
        setter.invoke(owningInstance, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> type) {
        return annotationInspector.getAnnotation(type, getter, setter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccessible() {
        return getter.isAccessible() && setter.isAccessible();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ensureAccessible() {
        getter.setAccessible(true);
        setter.setAccessible(true);
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

        final GetterSetterRawField that = (GetterSetterRawField) o;
        return getter.equals(that.getter) && name.equals(that.name) && setter.equals(that.setter);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + getter.hashCode();
        result = 31 * result + setter.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GetterSetterRawField{" +
            "name='" + name + '\'' +
            ", getter=" + getter +
            ", setter=" + setter + '}';
    }
}
