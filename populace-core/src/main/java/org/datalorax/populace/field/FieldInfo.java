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

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author Andrew Coates - 04/03/2015.
 */
public class FieldInfo {
    private final Field field;
    private final Type genericType;
    private final Object owningInstance;

    public FieldInfo(final Field field, final Type genericType, final Object owningInstance) {
        Validate.notNull(field, "field null");
        Validate.notNull(genericType, "genericType null");
        Validate.notNull(owningInstance, "owningInstance null");
        this.field = field;
        this.genericType = genericType;
        this.owningInstance = owningInstance;
    }

    public Field getField() {
        return field;
    }

    public Type getGenericType() {
        return genericType;
    }

    public Object getOwningInstance() {
        return owningInstance;
    }

    public Object getValue() {
        try {
            return field.get(owningInstance);
        } catch (IllegalAccessException e) {
            throw new FieldAccessException("Failed to access field: " + field, e);  // Todo(ac): include stack?
        }
    }

    public void setValue(Object value) {
        try {
            field.set(owningInstance, value);
        } catch (IllegalAccessException e) {
            throw new FieldAccessException("Failed to access field: " + field, e);  // Todo(ac): include stack?
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final FieldInfo fieldInfo = (FieldInfo) o;

        if (!field.equals(fieldInfo.field)) return false;
        if (!genericType.equals(fieldInfo.genericType)) return false;
        if (!owningInstance.equals(fieldInfo.owningInstance)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = field.hashCode();
        result = 31 * result + genericType.hashCode();
        result = 31 * result + owningInstance.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FieldInfo{" +
            "field=" + field +
            ", genericType=" + genericType +
            ", owningInstance=" + owningInstance +
            '}';
    }
}
