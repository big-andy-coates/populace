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
import org.datalorax.populace.core.walk.element.ElementInfo;
import org.datalorax.populace.core.walk.element.filter.ElementFilter;
import org.datalorax.populace.core.walk.field.FieldInfo;
import org.datalorax.populace.core.walk.field.filter.FieldFilter;
import org.datalorax.populace.core.walk.inspector.Inspectors;
import org.datalorax.populace.core.walk.visitor.ElementVisitor;
import org.datalorax.populace.core.walk.visitor.FieldVisitor;
import org.datalorax.populace.core.walk.visitor.FieldVisitors;
import org.datalorax.populace.core.walk.visitor.SetAccessibleFieldVisitor;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * Given an instance, it will populate all fields and elements, recursively, with values.
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

    /**
     * Obtain a builder capable of building the {@link org.datalorax.populace.core.populate.GraphPopulator},
     * pre-configured with sensible defaults,
     *
     * @return the builder.
     */
    public static Builder newBuilder() {
        return new GraphPopulatorBuilder();
    }

    /**
     * Walk the object graph to the supplied {@code instance}. Mutate each field and element encountered as configured
     * to do so by the populator's config.
     *
     * @param instance the instance to populate
     * @param <T>      the type of the instance to populate
     * @return the populated instance.
     */
    public <T> T populate(final T instance) {
        // Todo(ac): needs a TypeReference<T> parameter to pass along generic info of the top level object
        final Visitor visitor = new Visitor();
        final FieldVisitor fieldVisitor = FieldVisitors.chain(SetAccessibleFieldVisitor.INSTANCE, visitor);
        walker.walk(instance, fieldVisitor, visitor);
        return instance;
    }

    /**
     * Create an instance of the supplied {@code type} and walk the object graph mutating each field and element
     * encountered using populator's config
     *
     * @param type the type to instantiate
     * @param <T>  the type to instantiate
     * @return the populated instance
     */
    public <T> T populate(final Class<T> type) {
        Validate.isTrue(isNotInnerClass(type), "Non-static inner classes are not supported");
        final T instance = createInstance(type);
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

    @SuppressWarnings("unchecked")
    private <T> T createInstance(final Class<T> type) {
        return (T) config.createInstance(type, null);
    }

    private static boolean isNotInnerClass(final Class<?> type) {
        return type.getEnclosingClass() == null || Modifier.isStatic(type.getModifiers());
    }

    public interface Builder {
        // Todo(ac): v2.x these style interfaces should expose and accept builders, not built types

        /**
         * Replace the {@link org.datalorax.populace.core.walk.field.filter.FieldFilter} to use to control the walk
         *
         * @param filter the filter to install
         * @return the builder
         * @see org.datalorax.populace.core.walk.GraphWalker.Builder#withFieldFilter(FieldFilter)
         */
        Builder withFieldFilter(final FieldFilter filter);

        /**
         * Get the currently installed {@link org.datalorax.populace.core.walk.field.filter.FieldFilter}
         *
         * @return the currently installed filter
         * @see org.datalorax.populace.core.walk.GraphWalker.Builder#getFieldFilter()
         */
        FieldFilter getFieldFilter();

        /**
         * Replace the {@link org.datalorax.populace.core.walk.element.filter.ElementFilter} used to control the walk
         *
         * @param filter the filter to install
         * @return the builder
         * @see org.datalorax.populace.core.walk.GraphWalker.Builder#withElementFilter(ElementFilter)
         */
        Builder withElementFilter(final ElementFilter filter);

        /**
         * Get the currently installed {@link org.datalorax.populace.core.walk.element.filter.ElementFilter}
         *
         * @return the currently installed filter
         * @see org.datalorax.populace.core.walk.GraphWalker.Builder#getElementFilter()
         */
        ElementFilter getElementFilter();

        /**
         * Replace the {@link org.datalorax.populace.core.walk.inspector.Inspectors} used to inspect the types
         * encountered on the walk.
         *
         * @param inspectors the inspectors to install
         * @return the builder
         * @see org.datalorax.populace.core.walk.GraphWalker.Builder#withInspectors(Inspectors)
         */
        Builder withInspectors(final Inspectors inspectors);

        /**
         * Get the currently installed {@link org.datalorax.populace.core.walk.inspector.Inspectors}
         *
         * @return the currently installed inspectors
         * @see org.datalorax.populace.core.walk.GraphWalker.Builder#inspectorsBuilder()
         */
        Inspectors.Builder inspectorsBuilder();

        /**
         * Replace the {@link org.datalorax.populace.core.populate.mutator.Mutators} used to mutate the instances
         * encountered on the walk.
         * <p>
         * This call replaces the currently installer set of
         * {@link org.datalorax.populace.core.populate.Mutator mutators}. A builder initialised with the currently
         * configured mutators can be obtained by calling {@link #mutatorsBuilder()}
         *
         * @param mutators the mutators to install
         * @return the builder
         */
        Builder withMutators(final Mutators mutators);

        /**
         * Get a builder initialised with the currently configured {@link Mutator mutators}.
         * <p>
         * The builder can be used to build a modified set of mutators by either adding new, or replacing existing,
         * mutators. The newly built set of mutators can the be used to replace any previously configured mutators by
         * calling {@link #withMutators(org.datalorax.populace.core.populate.mutator.Mutators)}
         *
         * @return the mutators builder
         */
        Mutators.Builder mutatorsBuilder();

        /**
         * Replace the {@link org.datalorax.populace.core.populate.instance.InstanceFactories} used to create new
         * instances of types encountered on the walk, where the current value is {@code null}.
         * <p>
         * This call replaces the currently installer set of
         * {@link org.datalorax.populace.core.populate.instance.InstanceFactory instance factories}. A builder
         * initialised with the currently configured factories can be obtained by calling {@link #instanceFactoriesBuilder()}
         *
         * @param instanceFactories the factories to install
         * @return the builder
         */
        Builder withInstanceFactories(final InstanceFactories instanceFactories);

        /**
         * Get a  builder initialised with the currently configured
         * {@link org.datalorax.populace.core.populate.instance.InstanceFactory instance factories}.
         * <p>
         * The builder can be used to build a modified set of factories by either adding new, or replacing existing,
         * factories. The newly built set of factories can the be used to replace any previously configured factories by
         * calling {@link #withInstanceFactories(org.datalorax.populace.core.populate.instance.InstanceFactories)}
         *
         * @return the instance factories builder
         */
        InstanceFactories.Builder instanceFactoriesBuilder();

        /**
         * Build an immutable instance of {@link GraphPopulator} from the configuration provided.
         *
         * @return the newly constructed {@link GraphPopulator} instance.
         */
        GraphPopulator build();
    }

    private class Visitor implements FieldVisitor, ElementVisitor {
        @Override
        public void visit(final FieldInfo field) {
            try {
                final Type type = field.getGenericType();
                final Object currentValue = field.getValue();
                final Mutator mutator = config.getMutator(type);
                final Object mutated = mutator.mutate(type, currentValue, field.getOwningInstance(), config);
                if (mutated != currentValue) {
                    field.setValue(mutated);
                }
            } catch (Exception e) {
                throw new PopulatorException("Failed to populate field: " + field, e);
            }
        }

        @Override
        public void visit(final ElementInfo element) {
            try {
                final Type type = element.getGenericType();
                final Object currentValue = element.getValue();
                final Mutator mutator = config.getMutator(type);
                final Object mutated = mutator.mutate(type, currentValue, null, config);
                if (mutated != currentValue) {
                    element.setValue(mutated);
                }
            } catch (Exception e) {
                throw new PopulatorException("Failed to populate element: " + element, e);
            }
        }
    }
}
