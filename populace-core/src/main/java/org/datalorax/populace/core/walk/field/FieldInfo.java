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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author Andrew Coates - 04/03/2015.
 */
public class FieldInfo {
    private final RawField field;
    private final Object owningInstance;
    private final GenericTypeResolver typeResolver;
    private final PathProvider path;

    public FieldInfo(final RawField field, final Object owningInstance, final GenericTypeResolver typeResolver, final PathProvider path) {
        Validate.notNull(field, "field null");
        Validate.notNull(owningInstance, "owningInstance null");
        Validate.notNull(typeResolver, "typeResolver null");
        Validate.notNull(path, "path null");
        this.field = field;
        this.owningInstance = owningInstance;
        this.typeResolver = typeResolver;
        this.path = path;
    }

    public String getName() {
        return field.getName();
    }

    public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    public Type getGenericType() {
        return typeResolver.resolveType(field.getGenericType());
    }

    public Object getOwningInstance() {
        return owningInstance;
    }

    public void ensureAccessible() {
        field.ensureAccessible();
    }

    public Object getValue() {
        try {
            return field.getValue(getOwningInstance());
        } catch (ReflectiveOperationException e) {
            throw new FieldAccessException(field, path.getPath(), e);
        }
    }

    public void setValue(Object value) {
        try {
            field.setValue(getOwningInstance(), value);
        } catch (ReflectiveOperationException e) {
            throw new FieldAccessException(field, path.getPath(), e);
        }
    }

    public <T extends Annotation> T getAnnotation(final Class<T> type) {
        return field.getAnnotation(type);
    }

    public boolean isTransient() {
        return field.isTransient();
    }

    public boolean isStatic() {
        return field.isStatic();
    }

    public boolean isFinal() {
        return field.isFinal();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final FieldInfo that = (FieldInfo) o;
        return field.equals(that.field) && owningInstance.equals(that.owningInstance)
            && path.equals(that.path) && typeResolver.equals(that.typeResolver);
    }

    @Override
    public int hashCode() {
        int result = field.hashCode();
        result = 31 * result + owningInstance.hashCode();
        result = 31 * result + typeResolver.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FieldInfo{" +
            "field=" + field +
            ", owningInstance=" + owningInstance +
            ", typeResolver=" + typeResolver +
            ", path=" + path +
            '}';
    }
}

// Todo(ac): test

