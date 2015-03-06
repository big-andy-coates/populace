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

package org.datalorax.populace.graph.inspector;

import org.datalorax.populace.type.TypeUtils;
import org.datalorax.populace.typed.ImmutableTypeMap;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Builder for Inspectors collection
 *
 * @author Andrew Coates - 01/03/2015.
 */
final class InspectorsBuilder implements Inspectors.Builder {
    private static final Inspectors DEFAULT;

    private final ImmutableTypeMap.Builder<Inspector> inspectorsBuilder;

    public static Inspectors.Builder defaults() {
        return Inspectors.asBuilder(DEFAULT);
    }

    @Override
    public Inspectors.Builder withSpecificInspectors(final Map<Type, Inspector> inspectors) {
        inspectorsBuilder.withSpecificTypes(inspectors);
        return this;
    }

    @Override
    public Inspectors.Builder withSpecificInspector(final Type type, final Inspector inspector) {
        inspectorsBuilder.withSpecificType(type, inspector);
        return this;
    }

    @Override
    public Inspectors.Builder withSuperInspectors(final Map<Class<?>, Inspector> inspectors) {
        inspectorsBuilder.withSuperTypes(inspectors);
        return this;
    }

    @Override
    public Inspectors.Builder withSuperInspector(final Class<?> baseClass, final Inspector inspector) {
        inspectorsBuilder.withSuperType(baseClass, inspector);
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
    public Inspectors build() {
        return new Inspectors(inspectorsBuilder.build());
    }

    InspectorsBuilder(ImmutableTypeMap<Inspector> inspectors) {
        this.inspectorsBuilder = ImmutableTypeMap.asBuilder(inspectors);
    }

    private InspectorsBuilder() {
        this.inspectorsBuilder = ImmutableTypeMap.newBuilder(ObjectInspector.INSTANCE);
    }

    static {
        final InspectorsBuilder builder = new InspectorsBuilder();

        TypeUtils.getPrimitiveTypes().forEach(type -> builder.withSpecificInspector(type, TerminalInspector.INSTANCE));       // Todo(ac): replace with 'don't go into package java.*' type behaviour
        TypeUtils.getBoxedPrimitiveTypes().forEach(type -> builder.withSpecificInspector(type, TerminalInspector.INSTANCE));

        // Todo(ac): is it not just the case we want to not walk fields of anything under java.lang? Maybe need a 'package' walker concept?
        builder.withSpecificInspector(String.class, TerminalInspector.INSTANCE);
        builder.withSpecificInspector(Date.class, TerminalInspector.INSTANCE);

        builder.withSuperInspector(Collection.class, CollectionInspector.INSTANCE);
        builder.withSuperInspector(Map.class, MapValueInspector.INSTANCE);

        DEFAULT = builder
            .withArrayDefaultInspector(ArrayInspector.INSTANCE)
            .build();
    }
}
