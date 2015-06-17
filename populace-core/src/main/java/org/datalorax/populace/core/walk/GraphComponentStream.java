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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datalorax.populace.core.util.Pair;
import org.datalorax.populace.core.util.StreamUtils;
import org.datalorax.populace.core.walk.element.ElementInfo;
import org.datalorax.populace.core.walk.element.RawElement;
import org.datalorax.populace.core.walk.field.FieldInfo;
import org.datalorax.populace.core.walk.field.PathProvider;
import org.datalorax.populace.core.walk.field.RawField;
import org.datalorax.populace.core.walk.inspector.Inspector;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.datalorax.populace.core.util.TypeUtils.abbreviatedName;

/**
 * Stream of graph components.
 *
 * @author Andrew Coates - 28/02/2015.
 */
class GraphComponentStream {    // Todo(aC): break out the tests from GraphWalker.
    private static final Log LOG = LogFactory.getLog(GraphComponentStream.class);

    private final WalkerContext context;

    /**
     * Construct an instance of the stream that will use the provided {@code context}
     *
     * @param context the stream's configuration
     */
    GraphComponentStream(final WalkerContext context) {
        this.context = context;
    }

    /**
     * Obtain a stream of the fields and elements of the supplied {@code instance} and any object it links too. The
     * object graph is walked depth first.
     * <p>
     * Note: Due to the ordering of calls when using Streams, this method can not rely on users ensuring fields are
     * accessible. Instead it will internally call {@link FieldInfo#ensureAccessible()} on all encountered fields.
     *
     * @param instance the instance to walk
     * @return a stream of {@link GraphComponent GraphComponents} that represent the fields and elements of the object
     * graph.
     */
    public Stream<GraphComponent> stream(final Object instance) {
        final WalkerStack walkerStack = WalkerStack.newStack(instance);
        return getComponents(instance.getClass(), instance, walkerStack);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final GraphComponentStream that = (GraphComponentStream) o;
        return context.equals(that.context);
    }

    @Override
    public int hashCode() {
        return context.hashCode();
    }

    @Override
    public String toString() {
        return "GraphComponentStream{" +
            "context=" + context +
            '}';
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

    private static void logDebug(String message, PathProvider path) {
        if (LOG.isDebugEnabled()) { // Todo(ac): Add wrapper around logging that takes supplier e.g. log.info(() -> "" + instanceWithExpensiveToString);
            LOG.debug(path.getPath() + " - " + message);
        }
    }

    private static void logInfo(String message, PathProvider path) {
        if (LOG.isInfoEnabled()) {
            LOG.info(path.getPath() + " - " + message);
        }
    }
}