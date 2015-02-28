package org.datalorax.populace.populator;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
*  Builder implementation for the GraphPopulator
 * @author datalorax - 28/02/2015.
*/
class GraphPopulatorBuilder implements GraphPopulator.Builder {
    private final MutatorConfigBuilder mutatorConfig = new MutatorConfigBuilder();
    private final Set<String> fieldExclusions = new HashSet<String>();

    public GraphPopulator.Builder withFieldExclusions(Set<String> exclusions) {
        fieldExclusions.addAll(exclusions);
        return this;
    }

    public GraphPopulator.Builder withSpecificMutator(Type type, Mutator mutator) {
        mutatorConfig.withSpecificMutator(type, mutator);
        return this;
    }

    public GraphPopulator.Builder withBaseMutator(Class<?> baseClass, Mutator mutator) {
        mutatorConfig.withBaseMutator(baseClass, mutator);
        return this;
    }

    public GraphPopulatorBuilder withDefaultArrayMutator(Mutator mutator) {
        mutatorConfig.withDefaultArrayMutator(mutator);
        return this;
    }

    public GraphPopulator.Builder withDefaultMutator(Mutator mutator) {
        mutatorConfig.withDefaultMutator(mutator);
        return this;
    }

    PopulatorConfig buildPopulatorConfig() {
        return new PopulatorConfig(fieldExclusions, mutatorConfig.build());
    }

    public GraphPopulator build() {
        return new GraphPopulator(buildPopulatorConfig());
    }
}
