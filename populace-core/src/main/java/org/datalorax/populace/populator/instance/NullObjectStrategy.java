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

package org.datalorax.populace.populator.instance;

/**
 * An extension point to support 'pluggable' strategies for handling null {@link java.lang.Object objects}.
 * <p>
 * A null object strategy is used to define what should be done when a null object is found for which there is no type
 * information to use to instantiate an appropriate type.  For example this can occur when:
 * <ul>
 * <li>The type is actual defined as type {@link java.lang.Object}</li>
 * <li>The type is a {@link java.lang.reflect.TypeVariable} that can't be resolved due to type erasure</li>
 * <li>The type is a {@link java.lang.reflect.WildcardType} that can't be resolved due to type erasure</li>
 * </ul>
 *
 * @author Andrew Coates - 10/03/2015.
 */
public interface NullObjectStrategy {
    /**
     * Called when a null object is found, for which there is no type information available.
     *
     * @param parent the parent instance. Can be null.
     */
    void onNullObject(final Object parent);     /// Todo(ac): need context: what field on what class?
}
