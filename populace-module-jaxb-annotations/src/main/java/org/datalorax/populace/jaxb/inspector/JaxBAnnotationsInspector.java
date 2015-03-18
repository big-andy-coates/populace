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

package org.datalorax.populace.jaxb.inspector;

import com.google.common.collect.ImmutableSet;
import org.datalorax.populace.core.walk.field.RawField;
import org.datalorax.populace.core.walk.inspector.Inspector;

/**
 * An inspector replacement for the default {@link org.datalorax.populace.core.walk.inspector.FieldInspector} that exposes
 * bean properties, rather than raw fields, and which understands and uses JaxB annotations to include/include properties
 * and/or map interfaces to concrete types.
 *
 * @author Andrew Coates - 09/03/2015.
 */
public class JaxBAnnotationsInspector implements Inspector {
    public static final JaxBAnnotationsInspector INSTANCE = new JaxBAnnotationsInspector();

    @Override
    public Iterable<RawField> getFields(final Object instance) {
        return ImmutableSet.of();   // Todo(ac)
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

// Todo(ac): use or lose
