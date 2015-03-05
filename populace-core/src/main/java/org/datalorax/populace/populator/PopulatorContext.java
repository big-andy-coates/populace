package org.datalorax.populace.populator;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.instance.InstanceFactories;
import org.datalorax.populace.populator.instance.InstanceFactory;
import org.datalorax.populace.populator.mutator.Mutators;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * Holds details of a populator's configuration
 *
 * @author datalorax - 26/02/2015.
 */
public class PopulatorContext {
    private final Mutators mutators;
    private final InstanceFactories instanceFactories;

    public PopulatorContext(final Mutators mutators,
                            final InstanceFactories instanceFactories) {
        Validate.notNull(mutators, "mutators null");
        Validate.notNull(instanceFactories, "instanceFactories null");
        this.mutators = mutators;
        this.instanceFactories = instanceFactories;
    }

    public Mutator getMutator(final Type type) {
        return mutators.get(type);
    }

    public Object createInstance(final Type type, final Object parent) {
        // Todo(ac): Temporary hack until we support TypeVariables properly
        final Type typeToLookUp = type instanceof TypeVariable ? Object.class : type;
        final InstanceFactory factory = instanceFactories.get(typeToLookUp);
        final Class<?> rawType = TypeUtils.getRawType(typeToLookUp, null);
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
