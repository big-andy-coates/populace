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
 * A implementation of {@link org.datalorax.populace.core.walk.element.filter.ElementFilter} that always returns true
 * i.e. a filter that doesn't filter anything out.
 *
 * @author Andrew Coates - 29/04/2015.
 */
public class PassThroughElementFilter implements ElementFilter {
    public static final PassThroughElementFilter INSTANCE = new PassThroughElementFilter();

    @Override
    public boolean include(final ElementInfo element) {
        return true;
    }

    // Todo(ac): The usual + test.
}
