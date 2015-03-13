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
import org.datalorax.populace.core.populate.mutator.Mutators;
import org.datalorax.populace.core.walk.GraphWalker;
import org.datalorax.populace.core.walk.field.FieldInfo;
import org.datalorax.populace.core.walk.field.filter.FieldFilter;
import org.datalorax.populace.core.walk.inspector.Inspectors;
import org.datalorax.populace.core.walk.visitor.FieldVisitor;
import org.datalorax.populace.core.walk.visitor.FieldVisitors;
import org.datalorax.populace.core.walk.visitor.SetAccessibleFieldVisitor;

import java.lang.reflect.Type;

/**
 * Given an instance, it will populate all fields, recursively, with values.
 *
 * @author Andrew Coates - 25/02/2015.
 */
public final class GraphPopulator {
    private final GraphWalker walker;
    private final PopulatorContext config;

    GraphPopulator(final GraphWalker walker, final PopulatorContext config) {
        Validate.notNull(walker, "walker null");
        Validate.notNull(config, "config null");
        this.config = config;
        this.walker = walker;
    }

    public static Builder newBuilder() {
        return new GraphPopulatorBuilder();
    }

    // Todo(ac): needs a TypeReference<T> parameter...
    public <T> T populate(final T instance) {
        walker.walk(instance, FieldVisitors.chain(SetAccessibleFieldVisitor.INSTANCE, new Visitor()));
        return instance;
    }

    public <T> T populate(final Class<T> type) {
        //noinspection unchecked
        final T instance = (T) config.createInstance(type, null);
        return populate(instance);
    }

    public PopulatorContext getConfig() {
        return config;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final GraphPopulator that = (GraphPopulator) o;
        return config.equals(that.config) && walker.equals(that.walker);
    }

    @Override
    public int hashCode() {
        int result = walker.hashCode();
        result = 31 * result + config.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "GraphPopulator{" +
            "walker=" + walker +
            ", config=" + config +
            '}';
    }

    public interface Builder {
        Builder withFieldFilter(final FieldFilter filter);

        FieldFilter getFieldFilter();

        Builder withInspectors(final Inspectors inspectors);

        Inspectors.Builder inspectorsBuilder();

        Builder withMutators(final Mutators mutators);

        Mutators.Builder mutatorsBuilder();

        Builder withInstanceFactories(final InstanceFactories instanceFactories);

        InstanceFactories.Builder instanceFactoriesBuilder();

        GraphPopulator build();
    }

    private class Visitor implements FieldVisitor {
        @Override
        public void visit(final FieldInfo field) {
            final Type type = field.getGenericType();
            final Object currentValue = field.getValue();
            // Todo(ac): Add debug log line here...
            final Mutator mutator = config.getMutator(type);
            final Object mutated = mutator.mutate(type, currentValue, field.getOwningInstance(), config);
            if (mutated != currentValue) {
                field.setValue(mutated);
            }
        }
    }
}
