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

package org.datalorax.populace.core.walk.element;

import java.lang.reflect.Type;

/**
 * @author Andrew Coates - 04/03/2015.
 */
public interface RawElement {
    /**
     * Returns a {@code Type} object that represents the declared type for the container elements
     * <p>
     * If the {@code Type} is a parameterized type, the {@code Type} object returned must accurately reflect the actual
     * type parameters used in the source code.
     * <p>
     * If the type of the underlying field is a type variable or a parameterized type, it is created. Otherwise, it is
     * resolved.
     * <p>
     * The implementation of this method must follow the contract given for {@link java.lang.reflect.Field#getGenericType()}
     *
     * @param containerType the type of the elements container
     * @return the generic type of the container's element
     */
    Type getGenericType(final Type containerType);


    /**
     * Returns the value of the element represented by this {@code RawElement}. The value is automatically wrapped in
     * an object if it has a primitive type.
     *
     * @return the value of the represented element. primitive values are wrapped in an appropriate object before being
     * returned
     */
    Object getValue();

    /**
     * Sets the value of the element represented by this {@code RawElement} object. The new value is automatically
     * unwrapped if the underlying element has a primitive type.
     *
     * @param value the new value for the element of {@code owningInstance} being modified
     */
    void setValue(Object value);

    /**
     * Called before the {@code element} is walked. Allowing implementations to perform any initialisation needed
     * <p>
     * Other methods on this interface will not be called on an instance before this method has been called.
     */
    default void preWalk() {
    }

    /**
     * Called after the {@code element} is walked. Allowing implementations to perform any post-processing.
     * <p>
     * Other methods on this interface will not be called on an instance once this method has been called.
     */
    default void postWalk() {
    }
}
