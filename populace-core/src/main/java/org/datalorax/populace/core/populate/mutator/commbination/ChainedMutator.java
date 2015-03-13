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

package org.datalorax.populace.core.populate.mutator.commbination;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.populate.Mutator;
import org.datalorax.populace.core.populate.PopulatorContext;

import java.lang.reflect.Type;

/**
 * A mutator that chains together two other mutators. In output from the first mutator becomes the input to the next.
 *
 * @author Andrew Coates - 02/03/2015.
 */
public class ChainedMutator implements Mutator {
    private final Mutator first;
    private final Mutator second;

    public ChainedMutator(final Mutator first, final Mutator second) {
        Validate.notNull(first, "first null");
        Validate.notNull(second, "second null");
        this.first = first;
        this.second = second;
    }

    public static Mutator chain(final Mutator first, final Mutator second, final Mutator... additional) {
        ChainedMutator chain = new ChainedMutator(first, second);
        for (Mutator mutator : additional) {
            chain = new ChainedMutator(chain, mutator);
        }
        return chain;
    }

    @Override
    public Object mutate(final Type type, final Object currentValue, final Object parent, final PopulatorContext config) {
        final Object mutated = first.mutate(type, currentValue, parent, config);
        return second.mutate(type, mutated, parent, config);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ChainedMutator that = (ChainedMutator) o;
        return first.equals(that.first) && second.equals(that.second);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ChainedMutator{" +
            "first=" + first +
            ", second=" + second +
            '}';
    }
}
