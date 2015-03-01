package org.datalorax.populace.graph.inspector;

import org.datalorax.populace.typed.TypedCollection;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Helper functions for working with {@link org.datalorax.populace.graph.inspector.Inspector inspectors}
 *
 * @author datalorax - 01/03/2015.
 */
public final class InspectorUtils {
    private static final Map<Type, Inspector> DEFAULT_SPECIFIC_WALKERS;
    private static final Map<Class<?>, Inspector> DEFAULT_SUPER_WALKERS;

    public static TypedCollection.Builder<Inspector> defaultInspectors() {
        return setDefaultInspectors(TypedCollection.<Inspector>newBuilder());
    }

    public static TypedCollection.Builder<Inspector> setDefaultInspectors(TypedCollection.Builder<Inspector> builder) {
        builder.withSpecificTypes(DEFAULT_SPECIFIC_WALKERS)
                .withSuperTypes(DEFAULT_SUPER_WALKERS)
                .withDefault(ObjectInspector.INSTANCE)
                .withDefaultArray(ArrayInspector.INSTANCE);
        return builder;
    }


    static {
        final Map<Type, Inspector> specificWalkers = new HashMap<Type, Inspector>();

        final Type[] primitiveTypes = {boolean.class, byte.class, char.class, short.class, int.class, long.class, float.class, double.class,
                Boolean.class, Byte.class, Character.class, Short.class, Integer.class, Long.class, Float.class, Double.class};
        for (Type primitiveType : primitiveTypes) {
            specificWalkers.put(primitiveType, TerminalInspector.INSTANCE);
        }

        // Todo(ac): is it not just the case we want to not walk fields of anything under java.lang? Maybe need a 'package' walker concept?
        specificWalkers.put(String.class, TerminalInspector.INSTANCE);
        specificWalkers.put(Date.class, TerminalInspector.INSTANCE);

        DEFAULT_SPECIFIC_WALKERS = Collections.unmodifiableMap(specificWalkers);

        Map<Class<?>, Inspector> superWalkers = new HashMap<Class<?>, Inspector>();
        superWalkers.put(Collection.class, CollectionInspector.INSTANCE);
        superWalkers.put(Map.class, MapValueInspector.INSTANCE);

        DEFAULT_SUPER_WALKERS = Collections.unmodifiableMap(superWalkers);
    }

    private InspectorUtils() {
    }
}
