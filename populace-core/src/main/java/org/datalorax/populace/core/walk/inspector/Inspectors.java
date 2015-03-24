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

package org.datalorax.populace.core.walk.inspector;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.util.ImmutableTypeMap;
import org.datalorax.populace.core.walk.inspector.annotation.AnnotationInspector;
import org.datalorax.populace.core.walk.inspector.annotation.ChainedAnnotationInspector;

import java.lang.reflect.Type;

/**
 * Helper functions for working with {@link Inspector inspectors}
 *
 * @author Andrew Coates - 01/03/2015.
 */
public class Inspectors {
    private final ImmutableTypeMap<Inspector> inspectors;
    private final AnnotationInspector annotationInspector;

    /**
     * Construct via {@link Inspectors#newBuilder()} and copy via {@link Inspectors#asBuilder(Inspectors)}
     */
    Inspectors(final ImmutableTypeMap<Inspector> inspectors, final AnnotationInspector annotationInspector) {
        Validate.notNull(inspectors, "inspectors null");
        Validate.notNull(annotationInspector, "annotationInspector null");
        this.inspectors = inspectors;
        this.annotationInspector = annotationInspector;
    }

    /**
     * @return the default set of {@link Inspector inspectors} defined by the system
     */
    public static Inspectors defaults() {
        return InspectorsBuilder.defaults();
    }

    /**
     * @return a new Inspectors builder, initialised with the defaults in the system.
     */
    public static Builder newBuilder() {
        return asBuilder(defaults());
    }

    /**
     * Convert an existing immutable set of inspectors into a new builder instance
     *
     * @param source the source set of inspectors. The builder will be pre configured with all the inspectors in this set
     * @return a new Inspectors builder, initialised with the inspectors in {@code source}
     */
    public static Builder asBuilder(final Inspectors source) {
        return new InspectorsBuilder(source.inspectors, source.annotationInspector);
    }

    /**
     * Chains two annotation inspectors. The {@code second} inspector is only called should the {@code first} not find
     * the annotation.
     *
     * @param first  the first annotation inspector to call.
     * @param second the second annotation inspector to call, should the first return {@code null}.
     * @return the annotation, as found by either the {@code first} or the {@code second} inspectors.
     */
    public static AnnotationInspector chain(final AnnotationInspector first, final AnnotationInspector second) {
        return new ChainedAnnotationInspector(first, second);
    }

    /**
     * Get the inspector with the collection that handles the supplied {@code type}.
     *
     * @param type the tye an inspector is required for
     * @return the inspector that handles the supplied {@code type}.
     */
    public Inspector get(final Type type) {
        return inspectors.get(type);
    }

    /**
     * Get the {@link org.datalorax.populace.core.walk.inspector.annotation.AnnotationInspector}.
     *
     * @return the annotation inspector
     */
    public AnnotationInspector getAnnotationInspector() {
        return annotationInspector;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Inspectors that = (Inspectors) o;
        return inspectors.equals(that.inspectors);
    }

    @Override
    public int hashCode() {
        return inspectors.hashCode();
    }

    @Override
    public String toString() {
        return "Inspectors{" +
            "inspectors=" + inspectors +
            '}';
    }

    public interface Builder {

        Builder withSpecificInspector(final Type type, final Inspector inspector);

        Builder withSuperInspector(final Class<?> baseClass, final Inspector inspector);

        Builder withPackageInspector(final String packageName, final Inspector inspector);

        Builder withArrayDefaultInspector(final Inspector inspector);

        Builder withDefaultInspector(final Inspector inspector);

        Builder withAnnotationInspector(final AnnotationInspector annotationInspector);

        AnnotationInspector getAnnotationInspector();

        Inspectors build();
    }
}
