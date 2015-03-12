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

package org.datalorax.populace.graph;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datalorax.populace.field.FieldInfo;
import org.datalorax.populace.field.filter.FieldFilter;
import org.datalorax.populace.field.visitor.FieldVisitor;
import org.datalorax.populace.graph.inspector.Inspector;
import org.datalorax.populace.graph.inspector.Inspectors;

import java.lang.reflect.Field;

/**
 * Type that walks an object graph
 *
 * @author Andrew Coates - 28/02/2015.
 */
public class GraphWalker {
    private static final Log LOG = LogFactory.getLog(GraphWalker.class);

    private final WalkerContext config;

    GraphWalker(final WalkerContext config) {
        this.config = config;
    }

    public static Builder newBuilder() {
        return new GraphWalkerBuilder();
    }

    private static Object getValue(final Field field, final Object instance) {
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new WalkerException("Failed to get field value - consider using SetAccessibleFieldVisitor or similar", e);
        }
    }

    public void walk(final Object instance, final FieldVisitor visitor) {
        walk(instance, visitor, WalkerStack.newStack(instance));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final GraphWalker that = (GraphWalker) o;
        return config.equals(that.config);
    }

    @Override
    public int hashCode() {
        return config.hashCode();
    }

    @Override
    public String toString() {
        return "GraphWalker{" +
            "config=" + config +
            '}';
    }

    private void walk(final Object instance, final FieldVisitor visitor, final WalkerStack stack) {
        final Inspector inspector = config.getInspector(instance.getClass());

        if (LOG.isInfoEnabled()) {
            LOG.info(stack.getPath() + " - Inspecting type: " + instance.getClass() + ", inspector: " + inspector.getClass());
        }

        for (Field field : inspector.getFields(instance)) {
            final FieldInfo fieldInfo = new FieldInfo(field, instance, stack, stack);

            if (config.isExcludedField(fieldInfo)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(stack.getPath() + " - Skipping excluded field: " + field.getName());
                }
                continue;
            }

            if (LOG.isInfoEnabled()) {
                LOG.info(stack.getPath() + " - Found field: " + field.getName() + ", type: " + fieldInfo);
            }

            visitor.visit(fieldInfo);

            final Object value = getValue(field, instance);
            if (value != null) {
                walk(value, visitor, stack.push(field));
            }
        }

        for (Object child : inspector.getChildren(instance)) {
            if (child != null) {
                walk(child, visitor, stack.push(child));
            }
        }
    }

    public interface Builder {
        Builder withFieldFilter(final FieldFilter filter);

        FieldFilter getFieldFilter();

        Builder withInspectors(final Inspectors inspectors);

        Inspectors.Builder inspectorsBuilder();

        GraphWalker build();
    }
}