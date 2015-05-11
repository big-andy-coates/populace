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
import org.datalorax.populace.core.util.TypeResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Represents the information and operations available for a particular field on a particular instance.
 *
 * @author Andrew Coates - 04/03/2015.
 */
public class FieldInfo {
    private final RawField field;
    private final Object owningInstance;
    private final TypeResolver typeResolver;
    private final PathProvider path;

    /**
     * Construct a new FieldInfo object
     *
     * @param field          the raw field this instance should augment
     * @param owningInstance the instance from which to get / set the current value of this field
     * @param typeResolver   the resolver to use to resolve generic types
     * @param path           the provider of the path to this instance.
     */
    public FieldInfo(final RawField field, final Object owningInstance,
                     final TypeResolver typeResolver, final PathProvider path) {
        Validate.notNull(field, "field null");
        Validate.notNull(owningInstance, "owningInstance null");
        Validate.notNull(typeResolver, "typeResolver null");
        Validate.notNull(path, "path null");
        this.field = field;
        this.owningInstance = owningInstance;
        this.typeResolver = typeResolver;
        this.path = path;
    }

    /**
     * @return the name of the field
     * @see RawField#getName()
     */
    public String getName() {
        return field.getName();
    }

    /**
     * @return the type of the declaring class
     * @see RawField#getDeclaringClass()
     */
    public Class<?> getDeclaringClass() {
        return field.getDeclaringClass();
    }

    /**
     * @return the type of the field
     * @see RawField#getType()
     */
    public Class<?> getType() {
        return field.getType();
    }

    /**
     * Returns the generic type of the field, resolved using all available type information e.g. type variables are resolved
     * where type information higher up the stack is available.
     *
     * <ul>
     * <li>For <b>primitive types</b> this method returns the type of the field.</li>
     * <li>For <b>non-primitive types with a null value</b> this method returns the resolved generic type of the field</li>
     * <li>For <b>non-primitive types with a non-null value</b> this method returns the resolved generic type of the value</li>
     * </ul>
     *
     * <b>Note: This method will make use of the runtime type information available within the fields value if the field
     * is accessible. It is strongly recommended that you install the
     * {@link org.datalorax.populace.core.walk.visitor.SetAccessibleFieldVisitor} so that Populace can make use of this
     * additional type information</b>
     *
     * @return the generic type of the field
     * @see RawField#getGenericType()
     */
    public Type getGenericType() {
        if (field.getType().isPrimitive()) {
            return field.getType();
        }

        if (isAccessible()) {
            final Object value = getValue();
            if (value != null) {
                return typeResolver.resolve(value.getClass());
            }
        }

        return typeResolver.resolve(field.getGenericType());
    }

    /**
     * Returns the instance of this field's declaring class that this FieldInfo is wrapping.
     * <p>
     * The type returned is guaranteed to be equal to or a sub type of the type returned by {@link #getDeclaringClass()}
     *
     * @return the owning instance of the field
     */
    public Object getOwningInstance() {
        return owningInstance;
    }

    /**
     * @return true if field is accessible, false otherwise.
     * @see RawField#isAccessible()
     */
    public boolean isAccessible() {
        return field.isAccessible();
    }

    /**
     * @see RawField#ensureAccessible()
     */
    public void ensureAccessible() {
        field.ensureAccessible();
    }

    /**
     * @return the current value of the {@code RawField} in the {@code owningInstance}.
     * @throws org.datalorax.populace.core.walk.field.FieldAccessException if the field is not accessible
     * @see RawField#getValue(Object)
     */
    public Object getValue() {
        try {
            return field.getValue(getOwningInstance());
        } catch (ReflectiveOperationException e) {
            throw new FieldAccessException(field, path, e);
        }
    }

    /**
     * Sets the current value of the {@code RawField} in the {@code owningInstance}.
     *
     * @param value the new value for the field of {@code owningInstance} being modified
     * @throws org.datalorax.populace.core.walk.field.FieldAccessException if the field is not accessible
     * @see RawField#setValue(Object, Object)
     */
    public void setValue(Object value) {
        try {
            field.setValue(getOwningInstance(), value);
        } catch (ReflectiveOperationException e) {
            throw new FieldAccessException(field, path, e);
        }
    }

    /**
     * @param type the type of the {@link java.lang.annotation.Annotation Annotation} to retrieve.
     * @param <T>  the type of the {@link java.lang.annotation.Annotation Annotation} to retrieve.
     * @return the annotation if found, else {@code null}
     * @see RawField#getAnnotation(Class)
     */
    public <T extends Annotation> T getAnnotation(final Class<T> type) {
        return field.getAnnotation(type);
    }

    /**
     * @return true if the field is {@code transient}, false if not
     * @see RawField#isTransient()
     */
    public boolean isTransient() {
        return field.isTransient();
    }

    /**
     * @return true if the field is {@code static}, false if not
     * @see RawField#isStatic()
     */
    public boolean isStatic() {
        return field.isStatic();
    }

    /**
     * @return true if the field is {@code final}, false if not
     * @see RawField#isFinal()
     */
    public boolean isFinal() {
        return field.isFinal();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final FieldInfo that = (FieldInfo) o;
        return path.getPath().equals(that.path.getPath());
    }

    @Override
    public int hashCode() {
        return path.getPath().hashCode();
    }

    @Override
    public String toString() {

        return "FieldInfo{" +
            "path=" + path.getPath() +
            ", type=" + getGenericType() +
            '}';
    }
}

