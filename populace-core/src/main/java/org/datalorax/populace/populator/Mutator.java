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

package org.datalorax.populace.populator;

import java.lang.reflect.Type;

/**
 * Mutator interface.
 *
 * @author Andrew Coates - 26/02/2015.
 */
public interface Mutator {

    /**
     * Called to mutate the instance passed in <code>currentValue</code>, or create a new instance if the current value is null.
     *
     * @param type         the type of current value
     * @param currentValue the current value. Implementations are free to mutate this instance and return it, as opposed
     *                     to creating a new instance, if the type is mutable.
     * @param parent       the parent object to current value. This can be of use when working with inner classes.
     * @param config       the populator config
     * @return the mutated version of <code>currentValue</code>. This can be the same instance as
     * <code>currentValue</code> or a new instance.
     */
    Object mutate(final Type type, final Object currentValue, final Object parent, final PopulatorContext config);

    // Todo(ac): wrap type, currentValue and parent in an immuatable type to pass through (allow for extension)
}