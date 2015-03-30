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

package org.datalorax.populace.core.populate.instance;

/**
 * Extension point to support 'pluggable' factories for creating new instances of types.
 *
 * @author Andrew Coates - 02/03/2015.
 */
public interface InstanceFactory {
    /**
     * Create a new instance of {@code rawType}. If the factory does not support the type it will return null. If an error
     * occurred while attempting to create an instance it will through a
     * {@link org.datalorax.populace.core.populate.PopulatorException}. Otherwise the call will return a new instance.
     *
     * @param <T>               the type to create
     * @param type              the type to create
     * @param parent            the parent instance, which is needed to create inner class instances. Can be null.
     * @param instanceFactories the set of instance factories defined in the system
     * @return a new instance or null if the type is not supported by the factory.
     * @throws org.datalorax.populace.core.populate.PopulatorException on failure to instantiate new instance
     */
    <T> T createInstance(Class<? extends T> type, final Object parent, final InstanceFactories instanceFactories);
}
