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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author Andrew Coates - 12/06/2015.
 */
public interface RawField {
    /**
     * @return the name of the field represented by this {@code RawField} object
     */
    String getName();

    /**
     * @return the {@code Class} object representing the class or interface that declares the field represented by this
     * {@code Field} object.
     */
    Class<?> getDeclaringClass();

    /**
     * Returns a {@code Class} object that represents the declared type for the field represented by this {@code
     * RawField}.
     *
     * @return the type of the field.
     */
    Class<?> getType();

    /**
     * Returns a {@code Type} object that represents the declared type for the field represented by this {@code RawField}
     * object.
     * <p>
     * If the {@code Type} is a parameterized type, the {@code Type} object returned must accurately reflect the actual
     * type parameters used in the source code.
     * <p>
     * If the type of the underlying field is a type variable or a parameterized type, it is created. Otherwise, it is
     * resolved.
     * <p>
     * The implementation of this method must follow the contract given for {@link java.lang.reflect.Field#getGenericType()}
     *
     * @return the generic type of the field
     * @see java.lang.reflect.Field#getGenericType()
     */
    Type getGenericType();

    /**
     * Returns the value of the field represented by this {@code RawField}, on the specified {@code owningInstance}. The
     * value is automatically wrapped in an object if it has a primitive type.
     * <p>
     * The implementation of this method must follow the contract given for {@link java.lang.reflect.Field#get(Object)}
     *
     * @param owningInstance object from which the represented field's value is
     *                       to be extracted
     * @return the value of the represented field in {@code owningInstance}. primitive values are wrapped in an appropriate
     * object before being returned
     * @throws IllegalAccessException if this {@code RawField} object is enforcing Java language access
     *                                          control and the underlying field is inaccessible.
     * @see java.lang.reflect.Field#get(Object)
     */
    Object getValue(Object owningInstance) throws ReflectiveOperationException;

    /**
     * Sets the field represented by this {@code RawField} object on the specified object argument to the specified new
     * value. The new value is automatically unwrapped if the underlying field has a primitive type.
     * <p>
     * The implementation of this method must follow the contract given for {@link java.lang.reflect.Field#set(Object, Object)}.
     *
     * @param owningInstance the object whose field should be modified
     * @param value          the new value for the field of {@code owningInstance}
     *                       being modified
     * @throws IllegalAccessException if this {@code RawField} object is enforcing Java language access
     *                                          control and the underlying field is inaccessible.
     * @see java.lang.reflect.Field#set(Object, Object)
     */
    void setValue(Object owningInstance, Object value) throws ReflectiveOperationException;

    /**
     * return the instance of the annotation if it is present on the field represented by this {@code RawField} object,
     * or otherwise null.
     *
     * @param type the type of the {@link java.lang.annotation.Annotation Annotation} to retrieve.
     * @param <T>  the type of the {@link java.lang.annotation.Annotation Annotation} to retrieve.
     * @return the {@link java.lang.annotation.Annotation Annotation} if found, else {@code null}
     */
    <T extends Annotation> T getAnnotation(Class<T> type);

    /**
     * Determine if this field is accessible i.e. that its value can be retrieved and/or set.
     *
     * @return true if this field is accessible, false otherwise.
     */
    boolean isAccessible();

    /**
     * Ensure the field represented by this {@code RawField} is accessible i.e that calls to {@link #getValue(Object)}
     * and {@link #setValue(Object, Object)} won't through {@link IllegalAccessException}
     */
    void ensureAccessible();

    /**
     * @return true if the field represented by this {@code RawField} is transient, false otherwise
     */
    boolean isTransient();

    /**
     * @return true if the field represented by this {@code RawField} is static, false otherwise
     */
    boolean isStatic();

    /**
     * @return true if the field represented by this {@code RawField} is final, false otherwise
     */
    boolean isFinal();
}
