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

import java.lang.reflect.Type;

/**
 * An type representing a component of a type / instance.
 *
 * @author Andrew Coates - 08/05/2015.
 */
public interface GraphComponent {
    /**
     * Get the depth of this component into the walk
     *
     * @return the number of parent objects between this component and the root of the walk
     */
    int getDepth();

    /**
     * Get the generic type of the component
     *
     * @return the generic type of the component
     */
    Type getGenericType();

    /**
     * Get the current value of the component
     *
     * @return the current value of the component.
     */
    Object getValue();

    /**
     * Set the current value of the component
     *
     * @param value the new value for the component
     */
    void setValue(final Object value);

    /**
     * Get the string representation of the component, indented by the current depth of the component.
     * <p>
     * This method can be used to output the string representation of components indented to match the depth of the
     * walk recursion, as returned by the components {@link #getDepth()} method.
     *
     * @return the string representation of the component, indented by its depth.
     */
    default String toIndentedString() {
        final StringBuilder builder = new StringBuilder();
        for (int d = 0; d != getDepth(); ++d) {
            builder.append("\t");
        }
        return builder.append(toString()).toString();
    }
}
