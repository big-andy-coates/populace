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
 * @author datalorax - 01/03/2015.
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
        this.inspectorsBuilder = ImmutableTypeMap.newBuilder();
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
            .withDefaultInspector(ObjectInspector.INSTANCE)
            .build();
    }
}
