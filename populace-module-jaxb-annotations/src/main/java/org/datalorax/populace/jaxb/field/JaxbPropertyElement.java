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

package org.datalorax.populace.jaxb.field;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.jaxb.util.JaxbUtils;
import org.datalorax.populace.core.walk.field.RawField;

import javax.xml.bind.annotation.XmlTransient;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Field representing a property, as defined by the JaxB standard. // Todo(ac): clarify
 *
 * @author Andrew Coates - 13/03/2015.
 */
public class JaxbPropertyElement implements RawField {
    private final Method getter;
    private final Method setter;

    public JaxbPropertyElement(final Method getter, final Method setter) {
        Validate.notNull(getter, "getter null");
        Validate.notNull(setter, "setter null");
        JaxbUtils.validateGetterAndSetter(getter, setter);
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public String getName() {
        final String name = setter.getName();
        return Character.toLowerCase(name.charAt(3)) + name.substring(4);
    }

    @Override
    public Class<?> getDeclaringClass() {
        final Class<?> getterDeclaringClass = getter.getDeclaringClass();
        final Class<?> setterDeclaringClass = setter.getDeclaringClass();
        return setterDeclaringClass.isAssignableFrom(getterDeclaringClass) ? getterDeclaringClass : setterDeclaringClass;
    }

    @Override
    public Type getGenericType() {
        return getter.getGenericReturnType();
    }

    @Override
    public void ensureAccessible() {
        // Do nothing, as only public properties are valid as JaxB property getter and setters
    }

    @Override
    public Object getValue(final Object owningInstance) throws ReflectiveOperationException {
        return getter.invoke(owningInstance);
    }

    @Override
    public void setValue(final Object owningInstance, Object value) throws ReflectiveOperationException {
        setter.invoke(owningInstance, value);
    }

    /**
     * Retrieve the requested {@link java.lang.annotation.Annotation annotation} {@code type} for this {@link JaxbPropertyElement}
     *
     * @param type the {@link java.lang.annotation.Annotation} type requested.
     * @param <T>  the type of the annotation requested
     * @return the annotation if found, null otherwise.
     */
    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> type) {
        T a = getter.getAnnotation(type);
        return a == null ? setter.getAnnotation(type) : a;
    }

    @Override
    public boolean isTransient() {
        return getAnnotation(XmlTransient.class) != null;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final JaxbPropertyElement that = (JaxbPropertyElement) o;
        return getter.equals(that.getter) && setter.equals(that.setter);
    }

    @Override
    public int hashCode() {
        int result = getter.hashCode();
        result = 31 * result + setter.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "JaxbBeanProperty{" +
            "getter=" + getter +
            ", setter=" + setter +
            '}';
    }
}
