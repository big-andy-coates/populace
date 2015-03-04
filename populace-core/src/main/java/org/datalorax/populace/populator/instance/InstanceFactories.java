package org.datalorax.populace.populator.instance;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.typed.ImmutableTypeMap;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Collection of InstanceFactories to handle different types.
 *
 * @author datalorax - 02/03/2015.
 */
public final class InstanceFactories {
    private final InstanceFactory nullObjectFactory;
    private final ImmutableTypeMap<InstanceFactory> factories;

    public static Builder newBuilder() {
        return InstanceFactoriesBuilder.defaults();
    }

    public static Builder asBuilder(final InstanceFactories source) {
        return new InstanceFactoriesBuilder(source.nullObjectFactory, source.factories);
    }

    public interface Builder {
        Builder withSpecificFactories(final Map<Type, ? extends InstanceFactory> factories);

        Builder withSpecificFactory(final Type type, final InstanceFactory factory);

        Builder withSuperFactories(final Map<Class<?>, ? extends InstanceFactory> factories);

        Builder withSuperFactory(final Class<?> baseClass, final InstanceFactory factory);

        Builder withDefaultArrayFactory(final InstanceFactory factory);

        Builder withDefaultFactory(final InstanceFactory factory);

        Builder withNullObjectFactory(final InstanceFactory factory);

        // Todo(ac): add getters

        InstanceFactories build();
    }

    public InstanceFactory get(final Type key) {
        if (Object.class.equals(key)) {
            return nullObjectFactory;
        }
        return factories.get(key);
    }

    // Todo(ac): nullObjectFactory... different interface? ObjectStrategy? Pass it more info... like field, parent class?
    InstanceFactories(final InstanceFactory nullObjectFactory, final ImmutableTypeMap<InstanceFactory> factories) {
        Validate.notNull(nullObjectFactory, "no instance factory provided for Object.class");
        Validate.notNull(factories, "factories null");
        Validate.notNull(factories.getDefault(), "No default instance factory provided");
        Validate.notNull(factories.getArrayDefault(), "No default instance factory provided for array types");
        this.nullObjectFactory = nullObjectFactory;
        this.factories = factories;
    }
}

