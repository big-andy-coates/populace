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

package org.datalorax.populace.populator.mutator.change;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;

import java.lang.reflect.Type;

/**
 * Mutator that changes to value of {@link java.lang.String strings}
 *
 * @author Andrew Coates - 27/02/2015.
 */
public class ChangeStringMutator implements Mutator {
    public static final ChangeStringMutator INSTANCE = new ChangeStringMutator();

    @Override
    public Object mutate(Type type, Object currentValue, final Object parent, PopulatorContext config) {
        if (!type.equals(String.class)) {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }

        if (currentValue == null) {
            return null;
        }

        return ((String) currentValue).isEmpty() ? "populated" : currentValue + " - populated";
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
}
