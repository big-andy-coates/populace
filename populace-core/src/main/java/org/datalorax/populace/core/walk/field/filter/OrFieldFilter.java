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

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.walk.field.FieldInfo;

import java.util.function.Predicate;

/**
 * Combine two field filters with logical OR.
 * Field will be handled if either child filters return
 * @author Andrew Coates - 28/02/2015.
 * @deprecated Use {@link java.util.function.Predicate#or}
 */
@SuppressWarnings("deprecation")
@Deprecated
public class OrFieldFilter implements FieldFilter {
    private final Predicate<FieldInfo> first;
    private final Predicate<FieldInfo> second;

    public OrFieldFilter(final Predicate<FieldInfo> first, final Predicate<FieldInfo> second) {
        Validate.notNull(first, "first null");
        Validate.notNull(second, "second null");
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean include(final FieldInfo field) {
        return test(field);
    }

    @Override
    public boolean test(final FieldInfo field) {
        return first.test(field) || second.test(field);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final OrFieldFilter that = (OrFieldFilter) o;
        return first.equals(that.first) && second.equals(that.second);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "(" + first + " || " + second + ")";
    }
}
