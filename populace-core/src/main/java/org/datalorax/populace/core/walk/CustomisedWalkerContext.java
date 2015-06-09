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

/**
 * Holds information about the configuration of the walker and any customisations applied
 *
 * @author Andrew Coates - 28/02/2015.
 */
class CustomisedWalkerContext implements WalkerContext {
    private final WalkerContext baseContext;
    private final GraphWalker.Customisations customisations;

    public CustomisedWalkerContext(final WalkerContext baseContext,
                                   final GraphWalker.Customisations customisations) {
        Validate.notNull(baseContext, "baseContext null");
        Validate.notNull(customisations, "customisations null");
        this.baseContext = baseContext;
        this.customisations = customisations;
    }

    @Override
    public boolean isExcludedField(final FieldInfo field) {
        return baseContext.isExcludedField(field) ||
            customisations.getAdditionalFieldFilter().map(filter -> !filter.test(field)).orElse(false);
    }

    @Override
    public boolean isExcludedElement(final ElementInfo element) {
        return baseContext.isExcludedElement(element) ||
            customisations.getAdditionalElementFilter().map(filter -> !filter.test(element)).orElse(false);
    }

    @Override
    public Inspector getInspector(final Type type) {
        return baseContext.getInspector(type);
    }

    @Override
    public Inspectors getInspectors() {
        return baseContext.getInspectors();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final CustomisedWalkerContext that = (CustomisedWalkerContext) o;
        return baseContext.equals(that.baseContext) &&
            customisations.equals(that.customisations);
    }

    @Override
    public int hashCode() {
        int result = baseContext.hashCode();
        result = 31 * result + customisations.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CustomisedWalkerContext{" +
            "baseContext=" + baseContext +
            ", customisations=" + customisations +
            '}';
    }
}
