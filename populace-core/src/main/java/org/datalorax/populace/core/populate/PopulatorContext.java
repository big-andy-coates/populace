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
import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.core.populate.instance.InstanceFactories;
import org.datalorax.populace.core.populate.instance.InstanceFactory;
import org.datalorax.populace.core.populate.mutator.Mutators;

import java.lang.reflect.Type;

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
        final InstanceFactory factory = instanceFactories.get(type);
        final Class<?> rawType = TypeUtils.getRawType(type, null);
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
}
