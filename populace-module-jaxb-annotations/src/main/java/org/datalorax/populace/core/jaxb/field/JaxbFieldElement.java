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

package org.datalorax.populace.core.jaxb.field;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.jaxb.util.JaxbUtils;
import org.datalorax.populace.core.walk.field.RawField;

import javax.xml.bind.annotation.XmlTransient;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * Field representing a property, as defined by the JaxB standard. // Todo(ac): clarify
 *
 * @author Andrew Coates - 13/03/2015.
 */
public class JaxbFieldElement implements RawField {
    private final Field field;

    public JaxbFieldElement(final Field field) {
        Validate.notNull(field, "field null");
        JaxbUtils.validateField(field);
        this.field = field;
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    @Override
    public Type getGenericType() {
        return field.getGenericType();
    }

    @Override
    public void ensureAccessible() {
        field.setAccessible(true);
    }

    @Override
    public Object getValue(final Object owningInstance) throws ReflectiveOperationException {
        return field.get(owningInstance);
    }

    @Override
    public void setValue(final Object owningInstance, Object value) throws ReflectiveOperationException {
        field.set(owningInstance, value);
    }

    /**
     * Retrieve the requested {@link java.lang.annotation.Annotation annotation} {@code type} for this {@link JaxbFieldElement}
     *
     * @param type the {@link java.lang.annotation.Annotation} type requested.
     * @param <T>  the type of the annotation requested
     * @return the annotation if found, null otherwise.
     */
    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> type) {
        return field.getAnnotation(type);
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
        return Modifier.isFinal(field.getModifiers());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final JaxbFieldElement that = (JaxbFieldElement) o;
        return field.equals(that.field);
    }

    @Override
    public int hashCode() {
        return field.hashCode();
    }

    @Override
    public String toString() {
        return "JaxbFieldElement{" +
            "field=" + field +
            '}';
    }
}
