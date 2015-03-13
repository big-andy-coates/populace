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

package org.datalorax.populace.core.walk.field.filter;

/**
 * Utils class containing help methods for working with and combining
 * {@link FieldFilter fieldFilters}
 * @author Andrew Coates - 28/02/2015.
 */
public final class FieldFilters {
    private FieldFilters() {
    }

    public static FieldFilter defaults() {
        return and(ExcludeStaticFieldsFilter.INSTANCE, ExcludeTransientFieldsFilter.INSTANCE);
    }

    public static FieldFilter and(final FieldFilter first, final FieldFilter second) {
        return new AndFieldFilter(first, second);
    }

    public static FieldFilter or(final FieldFilter first, final FieldFilter second) {
        return new OrFieldFilter(first, second);
    }

    public static FieldFilter all(final FieldFilter first, final FieldFilter... theRest) {
        if (theRest.length == 0) {
            return first;
        }
        return new AllFieldFilter(first, theRest);
    }

    public static FieldFilter any(final FieldFilter first, final FieldFilter... theRest) {
        if (theRest.length == 0) {
            return first;
        }
        return new AnyFieldFilter(first, theRest);
    }
}
