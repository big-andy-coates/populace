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

package org.datalorax.populace.core.populate.mutator.ensure;

import org.datalorax.populace.core.populate.Mutator;
import org.datalorax.populace.core.populate.PopulatorContext;

import java.lang.reflect.Type;

/**
 * A {@link org.datalorax.populace.core.populate.Mutator} that ensures the value is set to null.
 *
 * @author Andrew Coates - 09/03/2015.
 */
public class EnsureNullMutator implements Mutator {
    public static final EnsureNullMutator INSTANCE = new EnsureNullMutator();

    @Override
    public Object mutate(final Type type, final Object currentValue, final Object parent, final PopulatorContext config) {
        return null;
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
