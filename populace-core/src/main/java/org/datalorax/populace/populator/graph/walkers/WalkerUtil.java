package org.datalorax.populace.populator.graph.walkers;

import org.datalorax.populace.populator.typed.TypedCollectionBuilder;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper functions for working with walkers
 *
 * @author datalorax - 01/03/2015.
 */
public final class WalkerUtil {
    private static final Map<Type, Walker> DEFAULT_SPECIFIC_WALKERS;
    private static final Map<Class<?>, Walker> DEFAULT_SUPER_WALKERS;

    public static TypedCollectionBuilder<Walker> defaultWalkers(TypedCollectionBuilder<Walker> builder) {
        builder
                .withSpecificTypes(DEFAULT_SPECIFIC_WALKERS)
                .withSuperTypes(DEFAULT_SUPER_WALKERS)
                .withDefault(StdWalker.INSTANCE)
                .withDefaultArray(StdArrayWalker.INSTANCE);
        return builder;
    }


    static {
        final Map<Type, Walker> specificWalkers = new HashMap<Type, Walker>();
//        final Walker primitivesWalker = new PrimitiveWalker();
//        final Type[] primitiveTypes = {boolean.class, byte.class, char.class, short.class, int.class, long.class, float.class, double.class,
//                Boolean.class, Byte.class, Character.class, Short.class, Integer.class, Long.class, Float.class, Double.class};
//        for (Type primitiveType : primitiveTypes) {
//            specificWalkers.put(primitiveType, primitivesWalker);
//        }
//
//        specificWalkers.put(String.class, new StringWalker());
//        specificWalkers.put(Date.class, new DateWalker());

        DEFAULT_SPECIFIC_WALKERS = Collections.unmodifiableMap(specificWalkers);

        Map<Class<?>, Walker> superWalkers = new HashMap<Class<?>, Walker>();
//        superWalkers.put(List.class, new ListWalker(ArrayList.class));
//        superWalkers.put(Set.class, new SetWalker(HashSet.class));
//        superWalkers.put(Map.class, new MapWalker(HashMap.class));

        DEFAULT_SUPER_WALKERS = Collections.unmodifiableMap(superWalkers);
    }

    private WalkerUtil() {
    }
}
