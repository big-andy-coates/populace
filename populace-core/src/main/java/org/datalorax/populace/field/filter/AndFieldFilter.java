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

package org.datalorax.populace.field.filter;

import org.apache.commons.lang3.Validate;

import java.lang.reflect.Field;

/**
 * Combine two field filters with logical AND.
 * Field will only be handled if both child filters return true from their
 * {@link FieldFilter#evaluate evaluate} call
 * @author Andrew Coates - 28/02/2015.
 */
public class AndFieldFilter implements FieldFilter {
    private final FieldFilter first;
    private final FieldFilter second;

    public AndFieldFilter(final FieldFilter first, final FieldFilter second) {
        Validate.notNull(first, "fist null");
        Validate.notNull(second, "second null");
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean evaluate(final Field field) {
        return first.evaluate(field) && second.evaluate(field);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AndFieldFilter that = (AndFieldFilter) o;
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
        return "AND {" +  first + ", " + second + "}";
    }
}
