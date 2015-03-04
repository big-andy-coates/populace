package org.datalorax.populace.populator.instance;

import org.datalorax.populace.graph.inspector.PrimitiveInstanceFactory;
import org.datalorax.populace.typed.TypeMap;

import java.util.*;

/**
 * Helper functions for working with {@link org.datalorax.populace.populator.instance.InstanceFactory instance factories}
 *
 * @author datalorax - 02/03/2015.
 */
public class InstanceFactories {
    public static TypeMap.Builder<InstanceFactory> defaultFactories() {
        return setDefaultFactories(TypeMap.<InstanceFactory>newBuilder());
    }

    public static TypeMap.Builder<InstanceFactory> setDefaultFactories(TypeMap.Builder<InstanceFactory> builder) {
        // Todo(ac): move into statics?
        builder.withSuperType(Enum.class, EnumInstanceFactory.INSTANCE)
            .withSuperType(Map.class, new NonConcreteInstanceFactory(Map.class, HashMap.class, DefaultInstanceFactory.INSTANCE))
            .withSuperType(Set.class, new NonConcreteInstanceFactory(Set.class, HashSet.class, DefaultInstanceFactory.INSTANCE))
            .withSuperType(List.class, new NonConcreteInstanceFactory(List.class, ArrayList.class, DefaultInstanceFactory.INSTANCE))
            .withDefaultArray(DefaultInstanceFactory.INSTANCE)   // Todo(ac): we'll need specific array factory
            .withDefault(chain(PrimitiveInstanceFactory.INSTANCE, DefaultInstanceFactory.INSTANCE));    // Todo(ac): don't chain, just add specific types
        return builder;
    }

    public static InstanceFactory chain(final ChainableInstanceFactory first, final InstanceFactory second) {
        return ChainedInstanceFactory.chain(first, second);
    }

    public static ChainableInstanceFactory chain(final ChainableInstanceFactory first, final ChainableInstanceFactory second) {
        return ChainedInstanceFactory.chain(first, second);
    }
}

// Todo(ac): Support Object - add default version that throws exception, though it'll be overloadable.
// Todo(ac): Support ... null polymorphic fields - pass the field generic type to something the client can customise.
