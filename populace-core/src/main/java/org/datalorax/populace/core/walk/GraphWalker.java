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
import org.datalorax.populace.core.util.Pair;
import org.datalorax.populace.core.util.StreamUtils;
import org.datalorax.populace.core.walk.element.ElementInfo;
import org.datalorax.populace.core.walk.element.RawElement;
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
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
     * @param context the walker's configuration
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
     * Recursively walk the fields and elements of the supplied {@code instance} and any objects it links too, calling
     * back on {@code fieldVisitor} for each field as it is discovered and {@code elementVisitor} for each child element
     * of each collection field. The object graph is walked depth first.
     *
     * @param instance       the instance to walk
     * @param fieldVisitor   the visitor to call back on for each discovered field.
     * @param elementVisitor the visitor to call back on for each element of a collection field.
     * @deprecated since 1.3. Use {@link #walk(Object, FieldVisitor, ElementVisitor, GraphWalker.Customisations)}
     */
    @Deprecated
    public void walk(final Object instance, final FieldVisitor fieldVisitor, final ElementVisitor elementVisitor) {
        final Visitors visitors = new Visitors(fieldVisitor, elementVisitor);
        final WalkerStack walkerStack = WalkerStack.newStack(instance);
        walk(instance.getClass(), instance, visitors, walkerStack);
    }

    /**
     * Recursively walk the fields and elements of the supplied {@code instance} and any objects it links too, calling
     * back on {@code fieldVisitor} for each field as it is discovered and {@code elementVisitor} for each child element
     * of each collection field. The object graph is walked depth first.
     *
     * @param instance       the instance to walk
     * @param fieldVisitor   the visitor to call back on for each discovered field.
     * @param elementVisitor the visitor to call back on for each element of a collection field.
     * @param customisations customisations to use for this walk, or {@link GraphWalker.Customisations#empty()}.
     */
    public void walk(final Object instance,
                     final FieldVisitor fieldVisitor, final ElementVisitor elementVisitor,
                     final Customisations customisations) { // Todo(aC): use customisations!
        final Visitors visitors = new Visitors(fieldVisitor, elementVisitor);
        final WalkerStack walkerStack = WalkerStack.newStack(instance);
        walk(instance.getClass(), instance, visitors, walkerStack);
    }

    /**
     * Obtain a stream of the fields and elements of the supplied {@code instance} and any object it links too. The
     * object graph is walked depth first.
     * <p>
     * Note: Due to the ordering of calls when using Streams, this method can not rely on users ensuring fields are
     * accessible. Instead it will internally call {@link FieldInfo#ensureAccessible()} on all encountered fields. If
     * you need explicit control over this then you should either use {@link #walk(Object, FieldVisitor, ElementVisitor)}
     * passing in an instance of {@link org.datalorax.populace.core.walk.visitor.SetAccessibleFieldVisitor}
     *
     * @param instance       the instance to walk
     * @param customisations defines the customisations to apply to the walk.
     * @return a stream of {@link org.datalorax.populace.core.walk.GraphComponent GraphComponents} that represent the
     * fields and elements of the object graph.
     */
    public Stream<GraphComponent> walk(final Object instance, final Customisations customisations) {
        return new GraphComponentStream(new CustomisedWalkerContext(context, customisations)).stream(instance);
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

    public interface Builder {
        /**
         * Install a filter to control what fields are included and excluded from the walk.
         * <p>
         * This replaces the currently installed filter. The existing filter can be obtained by calling
         * {@link #getFieldFilter()}. Filters can be combined using the functions in
         * {@link java.util.function.Predicate}, such as logical
         * {@link java.util.function.Predicate#and AND} and
         * {@link java.util.function.Predicate#or OR}, etc.
         *
         * @param filter the filter to install.
         * @return the builder
         */
        Builder withFieldFilter(final Predicate<FieldInfo> filter);

        /**
         * Get the currently installed field filter
         *
         * @return the currently installed field filter.
         */
        @SuppressWarnings("deprecation")
        FieldFilter getFieldFilter();

        /**
         * Install a filter to control what elements are included and excluded from the walk.
         * <p>
         * This replaces the currently installed filter. The existing filter can be obtained by calling
         * {@link #getElementFilter()}. Filters can be combined using the functions on
         * {@link java.util.function.Predicate}, such as logical
         * {@link java.util.function.Predicate#and AND} and
         * {@link java.util.function.Predicate#or OR}, etc.
         *
         * @param filter the filter to install
         * @return the builder
         */
        Builder withElementFilter(final Predicate<ElementInfo> filter);

        /**
         * Get the currently installed element filter
         *
         * @return the currently installed element filter.
         */
        Predicate<ElementInfo> getElementFilter();

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

    public interface CustomisationsBuilder {
        /**
         * Install an additional field filter. For a field to be walked it must pass the standard filters installed
         * in the walker, plus this additional filter.
         *
         * @param filter the additional field filter.
         * @return the builder to allow method chaining
         */
        CustomisationsBuilder withAdditionalFieldFilter(final Predicate<FieldInfo> filter);

        /**
         * Install an additional element filter. For an element to be walked it must pass the standard filters installed
         * in the walker, plus this additional filter.
         *
         * @param filter the additional element filter.
         * @return the builder to allow method chaining
         */
        CustomisationsBuilder withAdditionalElementFilter(final Predicate<ElementInfo> filter);

        /**
         * Builds an immutable {@link Customisations}
         *
         * @return the newly built customisations instance.
         */
        Customisations build();
    }

    public interface Customisations {
        /**
         * An empty set of customisations
         *
         * @return an empty set of customisations
         */
        static Customisations empty() {
            return CustomisationBuilderImpl.empty();
        }

        /**
         * Get a new builder for building {@link Customisations}
         *
         * @return a new builder
         */
        static CustomisationsBuilder newBuilder() {
            return new CustomisationBuilderImpl();
        }

        /**
         * Get the additional field filter, if configured.
         *
         * @return the additional field filter, or Optional.empty() if one is not configured
         */
        Optional<Predicate<FieldInfo>> getAdditionalFieldFilter();

        /**
         * Get the additional element filter, if configured.
         *
         * @return the additional element filter, or Optional.empty() if one is not configured
         */
        Optional<Predicate<ElementInfo>> getAdditionalElementFilter();
    }

    private Stream<GraphComponent> getComponents(final Type type, final Object instance, final WalkerStack stack) {
        final Type resolvedType = stack.getTypeResolver().resolve(instance.getClass());
        final Inspector inspector = context.getInspector(resolvedType);
        logInfo("Walking type: " + abbreviatedName(type) + ", inspector: " + abbreviatedName(inspector.getClass()), stack);

        return Stream.concat(getFields(instance, inspector, stack), getElements(type, instance, inspector, stack));
    }

    private Stream<GraphComponent> getFields(final Object instance, final Inspector inspector,
                                             final WalkerStack instanceStack) {
        final Function<RawField, Pair<FieldInfo, WalkerStack>> converter = rawField -> {
            final WalkerStack fieldStack = instanceStack.push(rawField);
            final FieldInfo fieldInfo = new FieldInfo(rawField, instance, fieldStack.getTypeResolver(), fieldStack);
            return new Pair<>(fieldInfo, fieldStack);
        };

        final Predicate<Pair<FieldInfo, WalkerStack>> fieldFilter = pair -> {
            final FieldInfo fieldInfo = pair.getFirst();
            if (context.isExcludedField(fieldInfo)) {
                logDebug("Skipping excluded field: " + fieldInfo.getName(), pair.getSecond());
                return false;
            }
            return true;
        };

        final Function<Pair<FieldInfo, WalkerStack>, Stream<GraphComponent>> childFlatMapper = pair -> {
            final FieldInfo fieldInfo = pair.getFirst();
            fieldInfo.ensureAccessible();       // Todo(ac): need a better way!
            final Object value = fieldInfo.getValue();
            if (value == null) {
                logDebug("Skipping null field: " + fieldInfo.getName(), pair.getSecond());
                return Stream.empty();
            }
            return getComponents(fieldInfo.getGenericType(), value, pair.getSecond());
        };

        final Function<Pair<FieldInfo, WalkerStack>, Stream<GraphComponent>> flatMapper = pair -> {
//            class LazyIterator implements Iterator<GraphComponent> {
//                private final FieldInfo field;
//                private final WalkerStack stack;
//                private Iterator<GraphComponent> iterator;
//
//                LazyIterator(final FieldInfo field, final WalkerStack stack) {
//                    this.field = field;
//                    this.stack = stack;
//                }
//
//                @Override
//                public boolean hasNext() {
//                    ensureInitialised();
//                    return iterator.hasNext();
//                }
//
//                @Override
//                public GraphComponent next() {
//                    ensureInitialised();
//                    return iterator.next();
//                }
//
//                private void ensureInitialised() {
//                    if (iterator != null) {
//                        return;
//                    }
//
//                    final Object value = field.getValue();
//                    if (value == null) {
//                        logDebug("Skipping null field: " + field.getName(), pair.getSecond());
//                        iterator = Collections.emptyIterator();
//                    } else {
//                        iterator = getComponents(field.getGenericType(), value, stack).iterator();
//                    }
//                }
//            }
//            final LazyIterator childIterator = new LazyIterator(fieldInfo, pair.getSecond());
//            final Stream<GraphComponent> childComponents = StreamSupport.stream(
//                Spliterators.spliteratorUnknownSize(childIterator, Spliterator.NONNULL | Spliterator.ORDERED), false);

            final FieldInfo fieldInfo = pair.getFirst();
            final Stream<GraphComponent> childComponents = Stream.of(pair).flatMap(childFlatMapper);
            return Stream.concat(Stream.of(fieldInfo), childComponents);
        };

        final Iterable<RawField> fields = inspector.getFields(instance.getClass(), context.getInspectors());
        return StreamSupport.stream(fields.spliterator(), false)
            .map(converter)
            .filter(fieldFilter)
            .flatMap(flatMapper);

// Todo(ac): Not implemented with streams:
//            logInfo("Visiting field: " + fieldInfo, fieldStack);
//
//            try {
//                visitors.visitField(fieldInfo);
//            } catch (Exception e) {
//                throw new WalkerException("Visitor threw exception while visiting field.", fieldStack, e);
//            }
    }

    private Stream<GraphComponent> getElements(final Type containerType, final Object instance,
                                               final Inspector inspector, final WalkerStack stack) {

        final Function<Pair<RawElement, Integer>, Pair<ElementInfo, WalkerStack>> converter = pair -> {
            final RawElement rawElement = pair.getFirst();
            final Integer index = pair.getSecond();
            final WalkerStack elementStack = stack.push(rawElement, index);
            final ElementInfo elementInfo = new ElementInfo(rawElement, containerType,
                elementStack.getTypeResolver(), elementStack);
            return new Pair<>(elementInfo, elementStack);
        };

        final Predicate<Pair<ElementInfo, WalkerStack>> elementFilter = pair -> {
            if (context.isExcludedElement(pair.getFirst())) {
                logDebug("Skipping excluded element", pair.getSecond());
                return false;
            }
            return true;
        };

        final Function<Pair<ElementInfo, WalkerStack>, Stream<GraphComponent>> flatMapper = pair -> {
            final ElementInfo elementInfo = pair.getFirst();
            final Object value = elementInfo.getValue();
            if (value == null) {
                logDebug("Skipping null child", pair.getSecond());
                return Stream.of(elementInfo);
            }

            final Stream<GraphComponent> childComponents = getComponents(value.getClass(), value, pair.getSecond());
            return Stream.concat(Stream.of(elementInfo), childComponents);
        };

        // Todo(ac): Figure out best return type and standardise...
        final Iterator<RawElement> elements = inspector.getElements(instance, context.getInspectors());
        final Spliterator<RawElement> spliterator = Spliterators
            .spliteratorUnknownSize(elements, Spliterator.NONNULL | Spliterator.ORDERED);

        final Stream<RawElement> elementStream = StreamSupport.stream(spliterator, false);
        final Stream<Integer> indexStream = Stream.iterate(0, i -> ++i);    // Todo(ac): Convert to int stream
        return StreamUtils.innerZip(elementStream, indexStream, Spliterator.NONNULL | Spliterator.ORDERED)
            .map(converter)
            .filter(elementFilter)
            .flatMap(flatMapper);

        // Todo(ac): Bits not covered by stream version - especially need to work out how to do pre/post walk...
//            logInfo("Visiting element: " + elementInfo, elementStack);

//            element.preWalk();
//
//            try {
//                visitors.visitElement(elementInfo);
//            } catch (Exception e) {
//                throw new WalkerException("Visitor threw exception while visiting element.", elementStack, e);
//            }

        // Todo(ac): Ensure done even for null values.
        //element.postWalk();
    }

    // Todo(ac): Switch old way to use stream way and call visitors on result.
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

        int i = 0;
        while (elements.hasNext()) {
            final RawElement element = elements.next();
            final WalkerStack elementStack = stack.push(element, i++);
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

    private static class Visitors {
        private final FieldVisitor fieldVisitor;
        private final ElementVisitor elementVisitor;

        // Todo(ac): v2.x should have visitors being able to return a control enum {earlyOut, recurse, skip} to control the walk
        // This would make the hamcrest matcher much cleaner and allow more re-use of a walker.
        // This may or may not remove the need for Field/Element Filters...
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