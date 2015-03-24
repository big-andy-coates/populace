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

package org.datalorax.populace.jaxb;

import org.datalorax.populace.core.populate.GraphPopulator;
import org.datalorax.populace.core.populate.instance.InstanceFactories;
import org.datalorax.populace.core.walk.field.filter.FieldFilters;
import org.datalorax.populace.core.walk.inspector.Inspectors;
import org.datalorax.populace.jaxb.field.filter.ExcludeXmlTransientFields;
import org.datalorax.populace.jaxb.instance.JaxbInstanceFactory;
import org.datalorax.populace.jaxb.walk.inspection.annotation.JaxbAnnotationInspector;

/**
 * Installer for JaxB specific handlers
 *
 * @author Andrew Coates - 12/03/2015.
 */
public final class PopulaceJaxb {
    private PopulaceJaxb() {
    }

    public static GraphPopulator.Builder install(final GraphPopulator.Builder builder) {
        final Inspectors.Builder inspectorsBuilder = builder.inspectorsBuilder();
        return builder
            .withFieldFilter(FieldFilters.and(builder.getFieldFilter(), ExcludeXmlTransientFields.INSTANCE))
            .withInspectors(inspectorsBuilder
                    .withAnnotationInspector(Inspectors.chain(JaxbAnnotationInspector.INSTANCE, inspectorsBuilder.getAnnotationInspector()))
                    .build()
            )
            .withInstanceFactories(builder.instanceFactoriesBuilder()
                    .withDefaultFactory(InstanceFactories.chain(JaxbInstanceFactory.INSTANCE, InstanceFactories.defaults().getDefault()))
                    .build()
            );
    }
}

// Todo(ac): Guice modules?
