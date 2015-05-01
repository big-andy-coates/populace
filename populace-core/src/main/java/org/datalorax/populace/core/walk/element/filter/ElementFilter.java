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

package org.datalorax.populace.core.walk.element.filter;

import org.datalorax.populace.core.walk.element.ElementInfo;

/**
 * Interface for 'pluggable' filtering of elements i.e. elements of containers such as {@link java.util.List}
 *
 * @author Andrew Coates - 29/04/2015.
 */
public interface ElementFilter {
    /**
     * Called to determine if the {@code element} should be included or excluded
     *
     * @param element the element to evaluate
     * @return true if the element should be included, false for excluded.
     */
    boolean include(final ElementInfo element);
}
