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

package org.datalorax.populace.core.walk;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datalorax.populace.core.walk.element.ElementInfo;
import org.datalorax.populace.core.walk.element.RawElement;
import org.datalorax.populace.core.walk.element.filter.ElementFilter;
import org.datalorax.populace.core.walk.field.FieldInfo;
import org.datalorax.populace.core.walk.field.PathProvider;
import org.datalorax.populace.core.walk.field.RawField;
import org.datalorax.populace.core.walk.field.filter.FieldFilter;
import org.datalorax.populace.core.walk.inspector.Inspector;
import org.datalorax.populace.core.walk.inspector.Inspectors;
import org.datalorax.populace.core.walk.visitor.ElementVisitor;
import org.datalorax.populace.core.walk.visitor.FieldVisitor;

import java.lang.reflect.Type;
import java.util.Iterator;

import static org.datalorax.populace.core.util.TypeUtils.abbreviatedName;

/**
 * Type that recursively walks an object graph, calling back to the client code on the provided
 * {@link org.datalorax.populace.core.walk.visitor.FieldVisitor field visitor} and
 * {@link org.datalorax.populace.core.walk.visitor.ElementVisitor element visitor} interfaces.
 *
 * @author Andrew Coates - 28/02/2015.
 */
public class GraphWalker {
    private static final Log LOG = LogFactory.getLog(GraphWalker.class);

    private final WalkerContext context;

    /**
     * Construct using the {@link GraphWalker.Builder builder}, which can
     * be obtained via {@link #newBuilder()}
     *
     * @param context the walkers configuration
     */
    GraphWalker(final WalkerContext context) {
        this.context = context;
    }

    /**
     * Obtain a builder capable of configuring and building an immutable {@link GraphWalker} instance. The builder returned
     * is already configured with a sensible defaults, but these can be overridden or augmented as required.
     *
     * @return the new {@link GraphWalker.Builder builder} instance.
     */
    public static Builder newBuilder() {
        return new GraphWalkerBuilder();
    }

    /**
     * Recursively walk the fields on {@code instance} and any objects it links too, calling back on {@code fieldVisitor}
     * for each field as it is discovered and {@code elementVisitor} for each child element of each collection field.
     *
     * @param instance       the instance to walk
     * @param fieldVisitor   the visitor to call back on for each discovered field.
     * @param elementVisitor the visitor to call back on for each element of a collection field.
     */
    public void walk(final Object instance, final FieldVisitor fieldVisitor, final ElementVisitor elementVisitor) {
        final Visitors visitors = new Visitors(fieldVisitor, elementVisitor);
        final WalkerStack walkerStack = WalkerStack.newStack(instance);
        walk(instance.getClass(), instance, visitors, walkerStack);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final GraphWalker that = (GraphWalker) o;
        return context.equals(that.context);
    }

    @Override
    public int hashCode() {
        return context.hashCode();
    }

    @Override
    public String toString() {
        return "GraphWalker{" +
            "context=" + context +
            '}';
    }

    private void walk(final Type type, final Object instance, final Visitors visitors, final WalkerStack stack) {
        final Type resolvedType = stack.getTypeResolver().resolve(instance.getClass());
        final Inspector inspector = context.getInspector(resolvedType);
        logInfo("Walking type: " + abbreviatedName(type) + ", inspector: " + abbreviatedName(inspector.getClass()), stack);

        walkFields(instance, visitors, inspector, stack);
        walkElements(type, instance, visitors, inspector, stack);
    }

    private void walkFields(final Object instance, final Visitors visitors, final Inspector inspector, final WalkerStack instanceStack) {
        final Iterable<RawField> fields = inspector.getFields(instance.getClass(), context.getInspectors());
        if (!fields.iterator().hasNext()) {
            return;
        }

        for (RawField field : fields) {
            final WalkerStack fieldStack = instanceStack.push(field);
            final FieldInfo fieldInfo = new FieldInfo(field, instance, fieldStack.getTypeResolver(), fieldStack);

            if (context.isExcludedField(fieldInfo)) {
                logDebug("Skipping excluded field: " + fieldInfo.getName(), fieldStack);
                continue;
            }

            logInfo("Visiting field: " + fieldInfo, fieldStack);

            try {
                visitors.visitField(fieldInfo);
            } catch (Exception e) {
                throw new WalkerException("Visitor threw exception while visiting field.", fieldStack, e);
            }

            final Object value = fieldInfo.getValue();
            if (value == null) {
                logDebug("Skipping null field: " + fieldInfo.getName(), fieldStack);
                continue;
            }

            walk(fieldInfo.getGenericType(), value, visitors, fieldStack);
        }
    }

    private void walkElements(final Type containerType, final Object instance, final Visitors visitors, final Inspector inspector, final WalkerStack stack) {
        // Todo(ac): Figure out best return type and standardise...
        final Iterator<RawElement> elements = inspector.getElements(instance, context.getInspectors());

        while (elements.hasNext()) {
            final RawElement element = elements.next();
            final WalkerStack elementStack = stack.push(element);
            final ElementInfo elementInfo = new ElementInfo(element, containerType, elementStack.getTypeResolver(), elementStack);

            if (context.isExcludedElement(elementInfo)) {
                logDebug("Skipping excluded element", elementStack);
                continue;
            }

            logInfo("Visiting element: " + elementInfo, elementStack);

            element.preWalk();

            try {
                visitors.visitElement(elementInfo);
            } catch (Exception e) {
                throw new WalkerException("Visitor threw exception while visiting element.", elementStack, e);
            }

            final Object value = elementInfo.getValue();
            if (value == null) {
                logDebug("Skipping null child", stack);
            } else {
                walk(value.getClass(), value, visitors, elementStack);
            }

            element.postWalk();
        }
    }

    private static void logDebug(String message, PathProvider path) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(path.getPath() + " - " + message);
        }
    }

    private static void logInfo(String message, PathProvider path) {
        if (LOG.isInfoEnabled()) {
            LOG.info(path.getPath() + " - " + message);
        }
    }

    public interface Builder {
        /**
         * Install a filter to control what fields are included and excluded from the walk.
         *
         * This replaces the currently installed filter. The existing filter can be obtained by calling
         * {@link #getFieldFilter()}. Filters can be combined using the functions in
         * {@link org.datalorax.populace.core.walk.field.filter.FieldFilters}, such as logical
         * {@link org.datalorax.populace.core.walk.field.filter.FieldFilters#and AND} and
         * {@link org.datalorax.populace.core.walk.field.filter.FieldFilters#or OR}, etc.
         *
         * @param filter the filter to install.
         * @return the builder
         */
        Builder withFieldFilter(final FieldFilter filter);

        /**
         * Get the currently installed {@link org.datalorax.populace.core.walk.field.filter.FieldFilter}
         * @return the currently installed field filter.
         */
        FieldFilter getFieldFilter();

        /**
         * Install a filter to control what elements are included and excluded from the walk.
         *
         * This replaces the currently installed filter. The existing filter can be obtained by calling
         * {@link #getElementFilter()}. Filters can be combined using the functions in
         * {@link org.datalorax.populace.core.walk.element.filter.ElementFilters}, such as logical
         * {@link org.datalorax.populace.core.walk.element.filter.ElementFilters#and AND} and
         * {@link org.datalorax.populace.core.walk.element.filter.ElementFilters#or OR}, etc.
         *
         * @param filter the filter to install
         * @return the builder
         */
        Builder withElementFilter(final ElementFilter filter);

        /**
         * Get the currently installed {@link org.datalorax.populace.core.walk.element.filter.ElementFilter}
         *
         * @return the currently installed element filter.
         */
        ElementFilter getElementFilter();

        /**
         * Install the inspectors that will be used to inspect each object that is encountered when walking the graph.
         * {@link org.datalorax.populace.core.walk.inspector.Inspector inspectors} are used to obtain that set of fields
         * an object has and, for container types, the child objects it contains.
         * <p>
         * This call replaces the currently installer set of
         * {@link org.datalorax.populace.core.walk.inspector.Inspector inspectors}. A builder initialised with the
         * currently configured inspectors can be obtained by calling {@link #inspectorsBuilder()}
         *
         * @param inspectors the set of inspectors to install.
         * @return the builder itself
         */
        Builder withInspectors(final Inspectors inspectors);

        /**
         * Obtain a {@link org.datalorax.populace.core.walk.inspector.Inspectors.Builder builder} initialised with the currently
         * installed {@link org.datalorax.populace.core.walk.inspector.Inspector inspectors}. The builder can be used to build
         * a modified set of inspectors by either adding new, or replacing existing, inspectors. The newly built set of
         * Inspectors can the be used to replace any previously configured inspectors by calling
         * {@link #withInspectors(org.datalorax.populace.core.walk.inspector.Inspectors)}
         *
         * @return the inspectors builder
         */
        Inspectors.Builder inspectorsBuilder();

        /**
         * Build an immutable instance of {@link GraphWalker} from the configuration provided.
         *
         * @return the newly constructed {@link GraphWalker} instance.
         */
        GraphWalker build();
    }

    private static class Visitors {
        private final FieldVisitor fieldVisitor;
        private final ElementVisitor elementVisitor;

        private Visitors(final FieldVisitor fieldVisitor, final ElementVisitor elementVisitor) {
            Validate.notNull(fieldVisitor, "fieldVisitor null");
            Validate.notNull(elementVisitor, "elementVisitor null");
            this.fieldVisitor = fieldVisitor;
            this.elementVisitor = elementVisitor;
        }

        public void visitField(final FieldInfo fieldInfo) {
            fieldVisitor.visit(fieldInfo);
        }

        public void visitElement(final ElementInfo elementInfo) {
            elementVisitor.visit(elementInfo);
        }
    }
}