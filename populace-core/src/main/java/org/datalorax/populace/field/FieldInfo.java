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

package org.datalorax.populace.field;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.field.visitor.FieldAccessException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * @author Andrew Coates - 04/03/2015.
 */
public class FieldInfo {
    private final Field field;
    private final Object owningInstance;
    private final GenericTypeProvider type;
    private final PathProvider path;

    public FieldInfo(final Field field, final Object owningInstance, final GenericTypeProvider type, final PathProvider path) {
        Validate.notNull(field, "field null");
        Validate.notNull(type, "type null");
        Validate.notNull(owningInstance, "owningInstance null");
        Validate.notNull(path, "path null");
        this.field = field;
        this.type = type;
        this.owningInstance = owningInstance;
        this.path = path;
    }

    public String getName() {
        return field.getName();
    }

    public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    public Type getGenericType() {
        return type.resolveType(field.getGenericType());
    }

    public Object getOwningInstance() {
        return owningInstance;
    }

    public void ensureAccessible() {
        field.setAccessible(true);
    }

    public Object getValue() {
        try {
            return field.get(owningInstance);
        } catch (IllegalAccessException e) {
            throw new FieldAccessException("Failed to access field: " + field + ", with path: " + path.getPath(), e);
        }
    }

    public void setValue(Object value) {
        try {
            field.set(owningInstance, value);
        } catch (IllegalAccessException e) {
            throw new FieldAccessException("Failed to access field: " + field + ", with path: " + path.getPath(), e);
        }
    }

    public <T extends Annotation> T getAnnotation(final Class<T> type) {
        return field.getAnnotation(type);
    }

    public boolean isTransient() {
        return Modifier.isTransient(field.getModifiers());
    }

    public boolean isStatic() {
        return Modifier.isStatic(field.getModifiers());
    }

    public boolean isFinal() {
        return Modifier.isFinal(field.getModifiers());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final FieldInfo fieldInfo = (FieldInfo) o;
        return field.equals(fieldInfo.field) &&
            getGenericType().equals(fieldInfo.getGenericType()) &&
            owningInstance.equals(fieldInfo.owningInstance);
    }

    @Override
    public int hashCode() {
        int result = field.hashCode();
        result = 31 * result + getGenericType().hashCode();
        result = 31 * result + owningInstance.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FieldInfo{" +
            "field=" + field +
            ", genericType=" + getGenericType() +
            ", owningInstance=" + owningInstance +
            '}';
    }
}

// Todo(ac): test
// Todo(ac): Have two packages under org.datalorax.populace - 'walk' and 'populate'
