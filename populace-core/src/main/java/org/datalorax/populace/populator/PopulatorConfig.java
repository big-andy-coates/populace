package org.datalorax.populace.populator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Holds details of the populator's configuration
 * @author datalorax - 26/02/2015.
 */
public class PopulatorConfig {
    private final Set<String> fieldExclusions;
    private final MutatorConfig mutatorConfig;

    public PopulatorConfig(Set<String> fieldExclusions, MutatorConfig mutatorConfig) {
        this.fieldExclusions = Collections.unmodifiableSet(new HashSet<String>(fieldExclusions));
        this.mutatorConfig = mutatorConfig;
    }

    public boolean isExcludedField(String fieldName) {
        return fieldExclusions.contains(fieldName);
    }
    public MutatorConfig getMutatorConfig() {
        return mutatorConfig;
    }
}
