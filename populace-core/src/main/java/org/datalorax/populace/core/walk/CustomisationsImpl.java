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

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.walk.element.ElementInfo;
import org.datalorax.populace.core.walk.field.FieldInfo;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Customisations implementation for {@link org.datalorax.populace.core.walk.GraphWalker}
 *
 * @author Andrew Coates - 04/06/2015.
 */
class CustomisationsImpl implements GraphWalker.Customisations {
    private final Optional<Predicate<FieldInfo>> fieldFilter;
    private final Optional<Predicate<ElementInfo>> elementFilter;

    CustomisationsImpl(final Optional<Predicate<FieldInfo>> fieldFilter, final Optional<Predicate<ElementInfo>> elementFilter) {
        Validate.notNull(fieldFilter, "fieldFilter null");
        Validate.notNull(elementFilter, "elementFilter null");
        this.fieldFilter = fieldFilter;
        this.elementFilter = elementFilter;
    }

    public Optional<Predicate<FieldInfo>> getAdditionalFieldFilter() {
        return fieldFilter;
    }

    public Optional<Predicate<ElementInfo>> getAdditionalElementFilter() {
        return elementFilter;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final CustomisationsImpl that = (CustomisationsImpl) o;

        if (!elementFilter.equals(that.elementFilter)) return false;
        if (!fieldFilter.equals(that.fieldFilter)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fieldFilter.hashCode();
        result = 31 * result + elementFilter.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CustomisationsImpl{" +
            "fieldFilter=" + fieldFilter +
            ", elementFilter=" + elementFilter +
            '}';
    }
}
