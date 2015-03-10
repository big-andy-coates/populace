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
 * Extension point to support 'pluggable' factories for creating new instances of types.
 * @author Andrew Coates - 02/03/2015.
 */
public interface InstanceFactory {
    /**
     * @param <T> the type to create
     * @param rawType the type to create
     * @param parent the parent instance, which is needed to create inner class instances. Can be null.
     * @param instanceFactories the set of instance factories defined in the sytem
     * @return a new instance
     * @throws org.datalorax.populace.populator.PopulatorException on failure to instantiate new instance
     */
    <T> T createInstance(Class<? extends T> rawType, final Object parent, final InstanceFactories instanceFactories);

    // Todo(ac): do the generics work / add anything to this class?
}
