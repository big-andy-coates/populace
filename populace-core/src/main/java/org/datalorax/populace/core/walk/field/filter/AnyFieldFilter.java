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

import org.apache.commons.lang3.StringUtils;
import org.datalorax.populace.core.walk.field.FieldInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * A combination field filter that includes the field is anny of the child filters includes it
 *
 * @author Andrew Coates - 28/02/2015.
 * @deprecated Use chained {@link java.util.function.Predicate#or} calls
 */
@SuppressWarnings("deprecation")
@Deprecated
public class AnyFieldFilter implements FieldFilter {
    private final List<Predicate<FieldInfo>> filters;

    @SafeVarargs
    public AnyFieldFilter(final Predicate<FieldInfo> first, final Predicate<FieldInfo>... theRest) {
        final List<Predicate<FieldInfo>> fieldFilters = new ArrayList<>();
        fieldFilters.add(first);
        fieldFilters.addAll(Arrays.asList(theRest));

        if (fieldFilters.stream().map(f -> f == null).filter(f -> f).findAny().isPresent()) {
            throw new NullPointerException("at least one filter was null");
        }

        filters = Collections.unmodifiableList(fieldFilters);
    }

    @Override
    public boolean include(final FieldInfo field) {
        return test(field);
    }

    @Override
    public boolean test(final FieldInfo field) {
        for (Predicate<FieldInfo> filter : filters) {
            if (filter.test(field)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AnyFieldFilter that = (AnyFieldFilter) o;
        return this.filters.size() == that.filters.size() && this.filters.containsAll(that.filters);
    }

    @Override
    public int hashCode() {
        return filters.hashCode();
    }

    @Override
    public String toString() {
        return "(" + StringUtils.join(filters, " || ") + ")";
    }
}
