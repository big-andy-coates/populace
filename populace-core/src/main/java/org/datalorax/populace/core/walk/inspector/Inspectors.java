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
import java.util.Optional;

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
     * Get the inspector most specific to the provided {@code type}.
     *
     * @param type the specific type to find
     * @return the inspector
     * @see org.datalorax.populace.core.util.ImmutableTypeMap#get(java.lang.reflect.Type) for details
     */
    public Inspector get(final Type type) {
        return inspectors.get(type);
    }

    /**
     * Get the inspector registered against the specific {@code type} provided, is present.
     *
     * @param type the specific type to find.
     * @return the inspector if found, else Optional.empty()
     * @see org.datalorax.populace.core.util.ImmutableTypeMap#getSpecific(java.lang.reflect.Type)
     */
    public Optional<Inspector> getSpecific(final Type type) {
        return Optional.ofNullable(inspectors.getSpecific(type));
    }

    /**
     * Get the specific super inspector registered for the {@code type} provided, is present.
     *
     * @param type the super type to find.
     * @return the inspector if found, else Optional.empty()
     * @see org.datalorax.populace.core.util.ImmutableTypeMap#getSuper(Class)
     */
    public Optional<Inspector> getSuper(final Class<?> type) {
        return Optional.ofNullable(inspectors.getSuper(type));
    }

    /**
     * Get the most specific inspector registered for the {@code packageName} provided, is present.
     *
     * @param packageName the name of the package.
     * @return the inspector if found, else Optional.empty()
     * @see org.datalorax.populace.core.util.ImmutableTypeMap#getPackage(String)
     */
    public Optional<Inspector> getPackage(final String packageName) {
        return Optional.ofNullable(inspectors.getPackage(packageName));
    }

    /**
     * Get the default inspector for array types.
     *
     * @return the inspector
     * @see org.datalorax.populace.core.util.ImmutableTypeMap#getArrayDefault()
     */
    public Inspector getArrayDefault() {
        return inspectors.getArrayDefault();
    }

    /**
     * Get the default inspector for none-array types.
     *
     * @return the inspector
     * @see org.datalorax.populace.core.util.ImmutableTypeMap#getDefault()
     */
    public Inspector getDefault() {
        return inspectors.getDefault();
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
        return annotationInspector.equals(that.annotationInspector) && inspectors.equals(that.inspectors);
    }

    @Override
    public int hashCode() {
        int result = inspectors.hashCode();
        result = 31 * result + annotationInspector.hashCode();
        return result;
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
