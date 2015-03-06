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

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.typed.ImmutableTypeMap;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Collection of InstanceFactories to handle different types.
 *
 * @author Andrew Coates - 02/03/2015.
 */
public class InstanceFactories {
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

        Builder withArrayDefaultFactory(final InstanceFactory factory);

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

    // Todo(ac): nullObjectFactory... different interface? ObjectStrategy? Pass it more info... like field, parent class?
    // Todo(aC): yes! Just a marker interface NullObjectStrategy
    InstanceFactories(final InstanceFactory nullObjectFactory, final ImmutableTypeMap<InstanceFactory> factories) {
        Validate.notNull(nullObjectFactory, "no instance factory provided for Object.class");
        Validate.notNull(factories, "factories null");
        this.nullObjectFactory = nullObjectFactory;
        this.factories = factories;
    }
}

