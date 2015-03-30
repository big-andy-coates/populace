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

package org.datalorax.populace.core.walk.inspector;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.util.ImmutableTypeMap;
import org.datalorax.populace.core.walk.inspector.annotation.AnnotationInspector;
import org.datalorax.populace.core.walk.inspector.annotation.SimpleAnnotationInspector;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Builder for Inspectors collection
 *
 * @author Andrew Coates - 01/03/2015.
 */
final class InspectorsBuilder implements Inspectors.Builder {
    private static final Inspectors DEFAULT;

    private final ImmutableTypeMap.Builder<Inspector> inspectorsBuilder;
    private AnnotationInspector annotationInspector;

    InspectorsBuilder(final ImmutableTypeMap<Inspector> inspectors, final AnnotationInspector annotationInspector) {
        this.inspectorsBuilder = ImmutableTypeMap.asBuilder(inspectors);
        this.annotationInspector = annotationInspector;
    }

    private InspectorsBuilder() {
        this.inspectorsBuilder = ImmutableTypeMap.newBuilder(FieldInspector.INSTANCE);
        this.annotationInspector = SimpleAnnotationInspector.INSTANCE;
    }

    static {
        final InspectorsBuilder builder = new InspectorsBuilder();

        builder.withSuperInspector(Collection.class, CollectionInspector.INSTANCE);
        builder.withSuperInspector(List.class, ListInspector.INSTANCE);
        builder.withSuperInspector(Map.class, MapValueInspector.INSTANCE);

        builder.withPackageInspector("java", TerminalInspector.INSTANCE);

        DEFAULT = builder
            .withArrayDefaultInspector(ArrayInspector.INSTANCE)
            .build();
    }

    public static Inspectors defaults() {
        return DEFAULT;
    }

    @Override
    public Inspectors.Builder withSpecificInspector(final Type type, final Inspector inspector) {
        inspectorsBuilder.withSpecificType(type, inspector);
        return this;
    }

    @Override
    public Inspectors.Builder withSuperInspector(final Class<?> baseClass, final Inspector inspector) {
        inspectorsBuilder.withSuperType(baseClass, inspector);
        return this;
    }

    @Override
    public Inspectors.Builder withPackageInspector(final String packageName, final Inspector inspector) {
        inspectorsBuilder.withPackageType(packageName, inspector);
        return this;
    }

    @Override
    public Inspectors.Builder withArrayDefaultInspector(final Inspector inspector) {
        inspectorsBuilder.withArrayDefault(inspector);
        return this;
    }

    @Override
    public Inspectors.Builder withDefaultInspector(final Inspector inspector) {
        inspectorsBuilder.withDefault(inspector);
        return this;
    }

    @Override
    public Inspectors.Builder withAnnotationInspector(final AnnotationInspector annotationInspector) {
        Validate.notNull(annotationInspector);
        this.annotationInspector = annotationInspector;
        return this;
    }

    @Override
    public AnnotationInspector getAnnotationInspector() {
        return annotationInspector;
    }

    @Override
    public Inspectors build() {
        return new Inspectors(inspectorsBuilder.build(), annotationInspector);
    }
}
