package org.datalorax.populace.populator;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.instance.InstanceFactory;
import org.datalorax.populace.typed.TypeMap;

import java.lang.reflect.Type;

/**
 * Holds details of a populator's configuration
 *
 * @author datalorax - 26/02/2015.
 */
public class PopulatorContext {
    private final TypeMap<Mutator> mutators;
    private final TypeMap<InstanceFactory> instanceFactories;

    public PopulatorContext(final TypeMap<Mutator> mutators,
                            final TypeMap<InstanceFactory> instanceFactories) {
        Validate.notNull(mutators, "mutators null");
        Validate.notNull(mutators.getDefault(), "No default mutator provided");
        Validate.notNull(mutators.getArrayDefault(), "No default mutator provided for array types");
        Validate.notNull(instanceFactories, "instanceFactories null");
        Validate.notNull(instanceFactories.getDefault(), "No default instance factory provided");
        Validate.notNull(instanceFactories.getArrayDefault(), "No default instance factory provided for array types");
        this.mutators = mutators;
        this.instanceFactories = instanceFactories;
    }

    public Mutator getMutator(final Type type) {
        return mutators.get(type);
    }

    public Object createInstance(final Type type, final Object parent) {
        final InstanceFactory factory = instanceFactories.get(type);
        final Class<?> rawType = TypeUtils.getRawType(type, null);
        return factory.createInstance(rawType, parent);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PopulatorContext that = (PopulatorContext) o;
        return instanceFactories.equals(that.instanceFactories) && mutators.equals(that.mutators);
    }

    @Override
    public int hashCode() {
        int result = mutators.hashCode();
        result = 31 * result + instanceFactories.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PopulatorContext{" +
            "mutators=" + mutators +
            ", instanceFactories=" + instanceFactories +
            '}';
    }
}
