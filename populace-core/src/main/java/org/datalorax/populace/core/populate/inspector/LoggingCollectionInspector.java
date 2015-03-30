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

package org.datalorax.populace.core.populate.inspector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datalorax.populace.core.walk.element.RawElement;
import org.datalorax.populace.core.walk.inspector.CollectionInspector;
import org.datalorax.populace.core.walk.inspector.Inspector;

import java.lang.reflect.Type;
import java.util.stream.Stream;

/**
 * An inspector of collections that logs should any mutator attempt to mutate the collection by setting an element.
 * <p>
 * The {@link java.util.Collection} API is not sufficient to support replacing an element in a reliable way. It is
 * therefore not supported by Populace by default. This limitation only causes problems if the collection contains
 * immutable types, as these can only be mutated by replacing the element in-place.
 *
 * @author Andrew Coates - 30/03/2015.
 */
public class LoggingCollectionInspector implements Inspector {
    public static final Inspector INSTANCE = new LoggingCollectionInspector();
    private static final Log LOG = LogFactory.getLog(LoggingCollectionInspector.class);

    @SuppressWarnings("unchecked")
    @Override
    public Stream<RawElement> getElements(final Object instance) {
        return CollectionInspector.INSTANCE.getElements(instance).map(LoggingCollectionElement::new);
    }

    @Override
    public boolean equals(final Object that) {
        return this == that || (that != null && getClass() == that.getClass());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    private static class LoggingCollectionElement implements RawElement {
        private final RawElement element;

        public LoggingCollectionElement(final RawElement element) {
            this.element = element;
        }

        @Override
        public Type getGenericType(final Type containerType) {
            return element.getGenericType(containerType);
        }

        @Override
        public Object getValue() {
            return element.getValue();
        }

        @Override
        public void setValue(final Object value) {
            final String possibleType = value != null ? value.getClass().getCanonicalName() : getValueType();
            LOG.warn("Immutable element of Collection could not be mutated. type: " + possibleType);
        }

        private String getValueType() {
            final Object value = getValue();
            return value == null ? "Unknown" : value.getClass().getCanonicalName();
        }
    }
}
