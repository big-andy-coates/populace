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

package org.datalorax.populace.core.walk;

import org.datalorax.populace.core.walk.element.ElementInfo;
import org.datalorax.populace.core.walk.field.FieldInfo;
import org.datalorax.populace.core.walk.inspector.Inspector;
import org.datalorax.populace.core.walk.inspector.Inspectors;

import java.lang.reflect.Type;

/**
 * @author Andrew Coates - 04/06/2015.
 */
public interface WalkerContext {
    /**
     * Called to determine if the provided {@code field} is excluded from the walk
     *
     * @param field the field to test for exclusion
     * @return true the field should be excluded from the walk, true otherwise
     */
    boolean isExcludedField(FieldInfo field);

    /**
     * Called to determine if the provided {@code element} is excluded from the walk
     *
     * @param element the element to test for exclusion
     * @return true the element should be excluded from the walk, true otherwise
     */
    boolean isExcludedElement(ElementInfo element);

    /**
     * Called to retrieve the inspector configured in the system to handle the supplied {@code type}
     *
     * @param type the type to retrieve the inspector for
     * @return the inspector configured in the system to handle the supplied {@code type}
     */
    Inspector getInspector(Type type);

    /**
     * Get all the {@link org.datalorax.populace.core.walk.inspector.Inspector inspectors} installed in the system
     *
     * @return all the configured inspectors.
     */
    Inspectors getInspectors();
}
