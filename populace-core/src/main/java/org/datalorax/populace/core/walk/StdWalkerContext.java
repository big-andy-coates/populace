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
import org.datalorax.populace.core.walk.inspector.Inspector;
import org.datalorax.populace.core.walk.inspector.Inspectors;

import java.lang.reflect.Type;
import java.util.function.Predicate;

/**
 * Holds information about the configuration of the walker
 *
 * @author Andrew Coates - 28/02/2015.
 */
class StdWalkerContext implements WalkerContext {
    private final Predicate<FieldInfo> fieldFilter;
    private final Predicate<ElementInfo> elementFilter;
    private final Inspectors inspectors;

    public StdWalkerContext(final Predicate<FieldInfo> fieldFilter,
                            final Predicate<ElementInfo> elementFilter,
                            final Inspectors inspectors) {
        Validate.notNull(fieldFilter, "fieldFilter null");
        Validate.notNull(elementFilter, "elementFilter null");
        Validate.notNull(inspectors, "inspector null");
        this.fieldFilter = fieldFilter;
        this.elementFilter = elementFilter;
        this.inspectors = inspectors;
    }

    @Override
    public boolean isExcludedField(final FieldInfo field) {
        return !fieldFilter.test(field);
    }

    @Override
    public boolean isExcludedElement(final ElementInfo element) {
        return !elementFilter.test(element);
    }

    @Override
    public Inspector getInspector(final Type type) {
        return inspectors.get(type);
    }

    @Override
    public Inspectors getInspectors() {
        return inspectors;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final StdWalkerContext that = (StdWalkerContext) o;
        return fieldFilter.equals(that.fieldFilter)
            && elementFilter.equals(that.elementFilter)
            && inspectors.equals(that.inspectors);
    }

    @Override
    public int hashCode() {
        int result = fieldFilter.hashCode();
        result = 31 & result + elementFilter.hashCode();
        result = 31 * result + inspectors.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "WalkerContext{" +
            "fieldFilter=" + fieldFilter +
            ", elementFilter=" + elementFilter +
            ", inspectors=" + inspectors +
            '}';
    }
}
