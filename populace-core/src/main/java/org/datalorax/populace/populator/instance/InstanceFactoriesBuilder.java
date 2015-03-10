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

package org.datalorax.populace.populator.instance;

import org.datalorax.populace.type.TypeUtils;
import org.datalorax.populace.typed.ImmutableTypeMap;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

/**
 * Helper functions for working with {@link org.datalorax.populace.populator.instance.InstanceFactory instance factories}
 *
 * @author Andrew Coates - 02/03/2015.
 */
final class InstanceFactoriesBuilder implements InstanceFactories.Builder {
    private static final InstanceFactories DEFAULT;
    private final ImmutableTypeMap.Builder<InstanceFactory> factoriesBuilder;
    private NullObjectStrategy nullObjectStrategy = LoggingInstanceFactory.INSTANCE;

    InstanceFactoriesBuilder(final NullObjectStrategy nullObjectStrategy,
                             final ImmutableTypeMap<InstanceFactory> factories) {
        this.nullObjectStrategy = nullObjectStrategy;
        this.factoriesBuilder = ImmutableTypeMap.asBuilder(factories);
    }

    private InstanceFactoriesBuilder() {
        this.factoriesBuilder = ImmutableTypeMap.newBuilder(DefaultConstructorInstanceFactory.INSTANCE);
    }

    static {
        final InstanceFactoriesBuilder builder = new InstanceFactoriesBuilder();

        TypeUtils.getPrimitiveTypes().forEach(type -> builder.withSpecificFactory(type, PrimitiveInstanceFactory.INSTANCE));
        TypeUtils.getBoxedPrimitiveTypes().forEach(type -> builder.withSpecificFactory(type, PrimitiveInstanceFactory.INSTANCE));

        builder.withSuperFactory(Enum.class, EnumInstanceFactory.INSTANCE);
        builder.withSuperFactory(Map.class, new DefaultTypeInstanceFactory(Map.class, HashMap.class, DefaultConstructorInstanceFactory.INSTANCE));
        builder.withSuperFactory(Set.class, new DefaultTypeInstanceFactory(Set.class, HashSet.class, DefaultConstructorInstanceFactory.INSTANCE));
        builder.withSuperFactory(List.class, new DefaultTypeInstanceFactory(List.class, ArrayList.class, DefaultConstructorInstanceFactory.INSTANCE));
        builder.withSuperFactory(Collection.class, new DefaultTypeInstanceFactory(Collection.class, ArrayList.class, DefaultConstructorInstanceFactory.INSTANCE));
        builder.withSpecificFactory(BigDecimal.class, BigDecimalInstanceFactory.INSTANCE);

        DEFAULT = builder
            .withArrayDefaultFactory(DefaultConstructorInstanceFactory.INSTANCE)   // Todo(ac): we'll need specific array factory
            .build();
    }

    public static InstanceFactories defaults() {
        return DEFAULT;
    }

    @Override
    public InstanceFactories.Builder withSpecificFactory(final Type type, final InstanceFactory factory) {
        factoriesBuilder.withSpecificType(type, factory);
        return this;
    }

    @Override
    public InstanceFactories.Builder withSuperFactory(final Class<?> baseClass, final InstanceFactory factory) {
        factoriesBuilder.withSuperType(baseClass, factory);
        return this;
    }

    @Override
    public InstanceFactories.Builder withArrayDefaultFactory(final InstanceFactory factory) {
        factoriesBuilder.withArrayDefault(factory);
        return this;
    }

    @Override
    public InstanceFactories.Builder withDefaultFactory(final InstanceFactory factory) {
        factoriesBuilder.withDefault(factory);
        return this;
    }

    @Override
    public InstanceFactories.Builder withNullObjectStrategy(final NullObjectStrategy strategy) {
        nullObjectStrategy = strategy;
        return this;
    }

    @Override
    public InstanceFactories build() {
        return new InstanceFactories(nullObjectStrategy, factoriesBuilder.build());
    }
}
