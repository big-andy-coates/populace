package org.datalorax.populace.graph.inspector;

import org.datalorax.populace.type.TypeUtils;
import org.datalorax.populace.typed.ImmutableTypeMap;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Helper functions for working with {@link org.datalorax.populace.graph.inspector.Inspector inspectors}
 *
 * @author datalorax - 01/03/2015.
 */
public final class Inspectors {
    private static final ImmutableTypeMap<Inspector> DEFAULT;

    public static ImmutableTypeMap.Builder<Inspector> defaults() {
        return ImmutableTypeMap.asBuilder(DEFAULT);
    }

    static {
        final ImmutableTypeMap.Builder<Inspector> builder = ImmutableTypeMap.newBuilder();

        TypeUtils.getPrimitiveTypes().forEach(type -> builder.withSpecificType(type, TerminalInspector.INSTANCE));       // Todo(ac): replace with 'don't go into package java.*' type behaviour
        TypeUtils.getBoxedPrimitiveTypes().forEach(type -> builder.withSpecificType(type, TerminalInspector.INSTANCE));

        // Todo(ac): is it not just the case we want to not walk fields of anything under java.lang? Maybe need a 'package' walker concept?
        builder.withSpecificType(String.class, TerminalInspector.INSTANCE);
        builder.withSpecificType(Date.class, TerminalInspector.INSTANCE);

        builder.withSuperType(Collection.class, CollectionInspector.INSTANCE);
        builder.withSuperType(Map.class, MapValueInspector.INSTANCE);

        DEFAULT = builder
            .withDefaultArray(ArrayInspector.INSTANCE)
            .withDefault(ObjectInspector.INSTANCE)
            .build();
    }

    private Inspectors() {
    }
}
