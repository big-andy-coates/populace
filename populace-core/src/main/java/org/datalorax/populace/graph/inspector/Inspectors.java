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

package org.datalorax.populace.graph.inspector;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.typed.ImmutableTypeMap;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Helper functions for working with {@link org.datalorax.populace.graph.inspector.Inspector inspectors}
 *
 * @author Andrew Coates - 01/03/2015.
 */
public class Inspectors {
    private final ImmutableTypeMap<Inspector> inspectors;

    public static Builder newBuilder() {
        return InspectorsBuilder.defaults();
    }

    public static Builder asBuilder(final Inspectors source) {
        return new InspectorsBuilder(source.inspectors);
    }

    public interface Builder {

        Builder withSpecificInspectors(final Map<Type, Inspector> inspector);

        Builder withSpecificInspector(final Type type, final Inspector inspector);

        Builder withSuperInspectors(final Map<Class<?>, Inspector> inspector);

        Builder withSuperInspector(final Class<?> baseClass, final Inspector inspector);

        Builder withArrayDefaultInspector(final Inspector inspector);

        Builder withDefaultInspector(final Inspector inspector);

        Inspectors build();

    }

    public Inspector get(final Type type) {
        return inspectors.get(type);
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

    /**
     * Construct via {@link Inspectors#newBuilder()} and copy via {@link Inspectors#asBuilder(Inspectors)}
     */
    Inspectors(ImmutableTypeMap<Inspector> inspectors) {
        Validate.notNull(inspectors);
        this.inspectors = inspectors;
    }
}
