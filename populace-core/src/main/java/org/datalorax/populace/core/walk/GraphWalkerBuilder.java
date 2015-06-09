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
import org.datalorax.populace.core.walk.field.filter.FieldFilter;
import org.datalorax.populace.core.walk.field.filter.FieldFilters;
import org.datalorax.populace.core.walk.inspector.Inspectors;

import java.util.function.Predicate;

/**
 * @author Andrew Coates - 01/03/2015.
 */
public class GraphWalkerBuilder implements GraphWalker.Builder {
    private Predicate<FieldInfo> fieldFilter = FieldFilters.excludeStaticFields()
        .and(FieldFilters.excludeTransientFields());
    private Predicate<ElementInfo> elementFilter = e -> true;
    private Inspectors inspectors = Inspectors.defaults();

    @Override
    public GraphWalkerBuilder withFieldFilter(final Predicate<FieldInfo> filter) {
        Validate.notNull(filter, "filter null");
        fieldFilter = filter;
        return this;
    }

    @SuppressWarnings("deprecation")
    @Override
    public FieldFilter getFieldFilter() {
        return fieldFilter::test;
    }

    @Override
    public GraphWalker.Builder withElementFilter(final Predicate<ElementInfo> filter) {
        Validate.notNull(filter, "filter null");
        elementFilter = filter;
        return this;
    }

    @Override
    public Predicate<ElementInfo> getElementFilter() {
        return elementFilter;
    }

    @Override
    public GraphWalkerBuilder withInspectors(final Inspectors inspectors) {
        Validate.notNull(inspectors, "inspectors null");
        this.inspectors = inspectors;
        return this;
    }

    @Override
    public Inspectors.Builder inspectorsBuilder() {
        return Inspectors.asBuilder(inspectors);
    }

    @Override
    public GraphWalker build() {
        return new GraphWalker(buildConfig());
    }

    private WalkerContext buildConfig() {
        return new StdWalkerContext(fieldFilter, elementFilter, inspectors);
    }
}
