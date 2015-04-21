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

package org.datalorax.populace.core.populate;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.populate.instance.InstanceFactories;
import org.datalorax.populace.core.populate.instance.InstanceFactory;
import org.datalorax.populace.core.populate.mutator.Mutators;
import org.datalorax.populace.core.util.TypeUtils;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Optional;

/**
 * Holds details of a populator's configuration
 *
 * @author Andrew Coates - 26/02/2015.
 */
public class PopulatorContext {
    private final Mutators mutators;
    private final InstanceFactories instanceFactories;

    public PopulatorContext(final Mutators mutators,
                            final InstanceFactories instanceFactories) {
        Validate.notNull(mutators, "mutators null");
        Validate.notNull(instanceFactories, "instanceFactories null");
        this.mutators = mutators;
        this.instanceFactories = instanceFactories;
    }

    public Mutator getMutator(final Type type) {
        return mutators.get(type);
    }

    public Object createInstance(final Type type, final Object parent) {
        if (type instanceof WildcardType) {
            return createInstanceFromWildcard((WildcardType) type, parent);
        }
        if (type instanceof TypeVariable) {
            return createInstance(Object.class, parent);
        }

        final Class<?> rawType = TypeUtils.getRawType(type, null);
        final InstanceFactory factory = instanceFactories.get(type);
        return factory.createInstance(rawType, parent, instanceFactories);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PopulatorContext that = (PopulatorContext) o;
        return instanceFactories.equals(that.instanceFactories) && mutators.equals(that.mutators);
    }

    @Override
    public int hashCode() {
        int result = mutators.hashCode();
        result = 31 * result + instanceFactories.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PopulatorContext{" +
            "mutators=" + mutators +
            ", instanceFactories=" + instanceFactories +
            '}';
    }

    private Object createInstanceFromWildcard(final WildcardType type, final Object parent) {
        final Type[] upperBounds = type.getUpperBounds();
        final Optional<InstanceFactory> factory = upperBounds.length == 1 ? instanceFactories.getSpecific(type) : Optional.empty();
        final Class<?> rawType = factory.isPresent() ? TypeUtils.getRawType(upperBounds[0], null) : null;

        if (rawType != null) {
            return factory.get().createInstance(rawType, parent, instanceFactories);
        }

        if (upperBounds.length == 1) {
            return createInstance(upperBounds[0], parent);
        }

        return createInstance(Object.class, parent);
    }
}
