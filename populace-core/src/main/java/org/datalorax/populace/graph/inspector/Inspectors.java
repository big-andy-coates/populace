package org.datalorax.populace.graph.inspector;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.typed.ImmutableTypeMap;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Helper functions for working with {@link org.datalorax.populace.graph.inspector.Inspector inspectors}
 *
 * @author datalorax - 01/03/2015.
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
