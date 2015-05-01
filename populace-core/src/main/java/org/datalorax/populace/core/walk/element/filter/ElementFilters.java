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

/**
 * Utils class containing help methods for working with and combining
 * {@link org.datalorax.populace.core.walk.element.filter.ElementFilter element filters}
 *
 * @author Andrew Coates - 29/04/2015.
 */
public final class ElementFilters {
    private ElementFilters() {
    }

    public static ElementFilter defaults() {
        return PassThroughElementFilter.INSTANCE;
    }

    // Big fat Todo(ac):  Ideally sharing code base with FieldFilter.
//    public static ElementFilter and(final ElementFilter first, final ElementFilter second) {
//        return new AndElementFilter(first, second);
//    }
//
//    public static ElementFilter or(final ElementFilter first, final ElementFilter second) {
//        return new OrElementFilter(first, second);
//    }
//
//    public static ElementFilter all(final ElementFilter first, final ElementFilter... theRest) {
//        if (theRest.length == 0) {
//            return first;
//        }
//        return new AllElementFilter(first, theRest);
//    }
//
//    public static ElementFilter any(final ElementFilter first, final ElementFilter... theRest) {
//        if (theRest.length == 0) {
//            return first;
//        }
//        return new AnyElementFilter(first, theRest);
//    }
//
    // Also add to FieldFilters.
//    public static ElementFilter not(final ElementFilter filter) {
//        return new NotElementFilter(filter);
//    }
}
