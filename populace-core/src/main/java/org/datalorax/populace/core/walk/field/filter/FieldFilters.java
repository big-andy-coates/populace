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

import org.datalorax.populace.core.walk.field.FieldInfo;

import java.util.function.Predicate;

/**
 * Utils class containing help methods for working with and combining
 * {@link FieldFilter fieldFilters}
 * @author Andrew Coates - 28/02/2015.
 */
@SuppressWarnings("deprecation")
public class FieldFilters {
    protected FieldFilters() {
    }

    public static Predicate<FieldInfo> excludeStaticFields() {
        return f -> !f.isStatic();
    }

    public static Predicate<FieldInfo> excludeTransientFields() {
        return f -> !f.isTransient();
    }

    public static Predicate<FieldInfo> excludeFinalFields() {
        return f -> !f.isFinal();
    }

    /**
     * Get the default filter filters used for populator runs
     *
     * @return the default filters used for populator runs
     * @deprecated use {@link org.datalorax.populace.core.populate.GraphPopulator#newBuilder() GraphPopulator.newBuilder()}
     * {@link org.datalorax.populace.core.populate.GraphPopulator.Builder#getFieldFilter() .getFieldFilter()}
     */
    @Deprecated
    public static FieldFilter defaults() {
        return and(ExcludeStaticFieldsFilter.INSTANCE, ExcludeTransientFieldsFilter.INSTANCE);
    }

    /**
     * Logically ANDs two filters together
     *
     * @param first  the first filter to logically AND
     * @param second the second filter to logically AND
     * @return a filter representing the logical AND of the two filters provided
     * @deprecated Use {@link java.util.function.Predicate#and(java.util.function.Predicate)}
     */
    @Deprecated
    public static FieldFilter and(final FieldFilter first, final FieldFilter second) {
        return new AndFieldFilter(first, second);
    }

    /**
     * Logically ORs two filters together.
     *
     * @param first  the first filter to logically OR
     * @param second the second filter to logically OR
     * @return a filter representing the logical OR of the two filters provided.
     * @deprecated Use {@link java.util.function.Predicate#or(java.util.function.Predicate)}
     */
    @Deprecated
    public static FieldFilter or(final FieldFilter first, final FieldFilter second) {
        return new OrFieldFilter(first, second);
    }

    /**
     * Logically ANDs many filters together
     *
     * @param first   the first filter to logically AND
     * @param theRest the other filters to logically AND
     * @return a filter representing the logical AND of all the provided filters
     * @deprecated Use chained {@link java.util.function.Predicate#and(java.util.function.Predicate)} calls
     */
    @Deprecated
    public static FieldFilter all(final FieldFilter first, final FieldFilter... theRest) {
        if (theRest.length == 0) {
            return first;
        }
        return new AllFieldFilter(first, theRest);
    }

    /**
     * Logically ORs many filters together
     *
     * @param first   the first filter to logically OR
     * @param theRest the other filters to logically OR
     * @return a filter representing the logical OR of all the provided filters
     * @deprecated Use chained {@link java.util.function.Predicate#or(java.util.function.Predicate)} calls
     */
    @Deprecated
    public static FieldFilter any(final FieldFilter first, final FieldFilter... theRest) {
        if (theRest.length == 0) {
            return first;
        }
        return new AnyFieldFilter(first, theRest);
    }
}
