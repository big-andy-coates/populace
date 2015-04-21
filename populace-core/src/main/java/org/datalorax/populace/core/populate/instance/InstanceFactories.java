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

package org.datalorax.populace.core.populate.instance;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.util.ImmutableTypeMap;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Collection of InstanceFactories to handle different types.
 *
 * @author Andrew Coates - 02/03/2015.
 */
public class InstanceFactories {
    private final NullObjectInstanceFactory nullObjectFactory;
    private final ImmutableTypeMap<InstanceFactory> factories;

    InstanceFactories(final NullObjectStrategy nullObjectFactory, final ImmutableTypeMap<InstanceFactory> factories) {
        Validate.notNull(factories, "factories null");
        this.nullObjectFactory = new NullObjectInstanceFactory(nullObjectFactory);
        this.factories = factories;
    }

    /**
     * @return the default set of {@link InstanceFactory instance factories} defined by the system
     */
    public static InstanceFactories defaults() {
        return InstanceFactoriesBuilder.defaults();
    }

    /**
     * @return a new InstanceFactories builder, initialised with the defaults in the system.
     */
    public static Builder newBuilder() {
        return asBuilder(defaults());
    }

    /**
     * Convert an existing immutable set of instance factories into a new builder instance
     *
     * @param source the source set of instance factories. The builder will be pre configured with all the factories in this set
     * @return a new InstanceFactories builder, initialised with the factories in {@code source}
     */
    public static Builder asBuilder(final InstanceFactories source) {
        return new InstanceFactoriesBuilder(source.nullObjectFactory.getStrategy(), source.factories);
    }

    /**
     * Chain two {@link InstanceFactory factories} together.  The {@code second}
     * {@link InstanceFactory} will only be called if the {@code first} returns {@code null}, indicating it doesn't
     * support the requested {@code rawType}.
     * <p>
     * Multiple {@link InstanceFactory InstanceFactories} can be chained together using this method
     *
     * @param first      the {@link InstanceFactory} that is the start of the chain.
     * @param second     the {@link InstanceFactory} that should be called if the first returns {@code null}.
     * @param additional additional {@link InstanceFactory} that can be called if previous factories return {@code null}
     * @return a {@link InstanceFactory} that encapsulates the chained factories.
     */
    public static InstanceFactory chain(final InstanceFactory first, final InstanceFactory second,
                                        final InstanceFactory... additional) {
        return ChainedInstanceFactory.chain(first, second, additional);
    }

    /**
     * Get the instance factory most specific to the provided {@code type}.
     *
     * @param type the specific type to find
     * @return the instance factory
     * @see org.datalorax.populace.core.util.ImmutableTypeMap#get(java.lang.reflect.Type) for details
     */
    public InstanceFactory get(final Type type) {
        if (Object.class.equals(type)) {
            return nullObjectFactory;
        }

        return factories.get(type);
    }

    /**
     * Get the instance factory registered against the specific {@code type} provided, is present.
     *
     * @param type the specific type to find.
     * @return the instance factory if found, else Optional.empty()
     */
    public Optional<InstanceFactory> getSpecific(final Type type) {
        return Optional.ofNullable(factories.getSpecific(type));
    }

    /**
     * Get the instance factory registered against the super {@code type} provided, is present.
     *
     * @param type the super type to find.
     * @return the instance factory if found, else Optional.empty()
     */
    public Optional<InstanceFactory> getSuper(final Class<?> type) {
        return Optional.ofNullable(factories.getSuper(type));
    }

    /**
     * Get the default instance factory for array types.
     *
     * @return the instance factory
     */
    public InstanceFactory getArrayDefault() {
        return factories.getArrayDefault();
    }

    /**
     * Get the default instance factory for none-array types.
     *
     * @return the instance factory
     */
    public InstanceFactory getDefault() {
        return factories.getDefault();
    }

    // Todo(ac): expose the same accessors across mutators & inspectors?

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final InstanceFactories that = (InstanceFactories) o;
        return factories.equals(that.factories) && nullObjectFactory.equals(that.nullObjectFactory);
    }

    @Override
    public int hashCode() {
        int result = nullObjectFactory.hashCode();
        result = 31 * result + factories.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "InstanceFactories{" +
            "nullObjectFactory=" + nullObjectFactory +
            ", factories=" + factories +
            '}';
    }

    public interface Builder {
        Builder withSpecificFactory(final Type type, final InstanceFactory factory);

        Builder withSuperFactory(final Class<?> baseClass, final InstanceFactory factory);

        Builder withArrayDefaultFactory(final InstanceFactory factory);

        Builder withDefaultFactory(final InstanceFactory factory);

        Builder withNullObjectStrategy(final NullObjectStrategy strategy);

        // Todo(ac): add getters

        InstanceFactories build();
    }

    static class NullObjectInstanceFactory implements InstanceFactory {
        private final NullObjectStrategy strategy;

        NullObjectInstanceFactory(final NullObjectStrategy strategy) {
            Validate.notNull(strategy, "strategy null");
            this.strategy = strategy;
        }

        @Override
        public <T> T createInstance(final Class<? extends T> rawType, final Object parent, final InstanceFactories instanceFactories) {
            strategy.onNullObject(parent);
            return null;
        }

        public NullObjectStrategy getStrategy() {
            return strategy;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final NullObjectInstanceFactory that = (NullObjectInstanceFactory) o;
            return strategy.equals(that.strategy);
        }

        @Override
        public int hashCode() {
            return strategy.hashCode();
        }

        @Override
        public String toString() {
            return "NullObjectInstanceFactory{" +
                "strategy=" + strategy +
                '}';
        }
    }
}

