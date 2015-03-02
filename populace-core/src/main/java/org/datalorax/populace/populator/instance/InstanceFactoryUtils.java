package org.datalorax.populace.populator.instance;

import org.datalorax.populace.typed.TypeMap;

/**
 * Helper functions for working with {@link org.datalorax.populace.populator.instance.InstanceFactory instance factories}
 *
 * @author datalorax - 02/03/2015.
 */
public class InstanceFactoryUtils {
    public static TypeMap.Builder<InstanceFactory> defaultFactories() {
        return setDefaultFactories(TypeMap.<InstanceFactory>newBuilder());
    }

    public static TypeMap.Builder<InstanceFactory> setDefaultFactories(TypeMap.Builder<InstanceFactory> builder) {
        builder.withSuperType(Enum.class, EnumInstanceFactory.INSTANCE)
                .withDefaultArray(DefaultInstanceFactory.INSTANCE)   // Todo(ac): we'll need specific array factory
                .withDefault(DefaultInstanceFactory.INSTANCE);
        return builder;
    }
}
