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

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.populate.Mutator;
import org.datalorax.populace.core.populate.PopulatorContext;
import org.datalorax.populace.core.util.TypeUtils;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;

/**
 * A mutator that ensures a collection has at least valueIf it is, then the mutator adds a non-null single entry.
 * <p>
 * If the currentValue is null then this mutator does nothing. Consider using
 * {@link EnsureMutator} to first ensure the current value is not null
 * the required behaviour is to always ensure a non-null, populated collection instance.
 *
 * @author Andrew Coates - 01/03/2015.
 */
public class EnsureCollectionNotEmptyMutator implements Mutator {
    public static final EnsureCollectionNotEmptyMutator INSTANCE = new EnsureCollectionNotEmptyMutator();
    private static final TypeVariable<Class<Collection>> COLLECTION_TYPE_VARIABLE = Collection.class.getTypeParameters()[0];

    @SuppressWarnings("unchecked")
    private static Collection<Object> ensureCollection(final Type type, final Object currentValue) {
        Validate.isAssignableFrom(Collection.class, TypeUtils.getRawType(type, Collection.class), "Mutator only supports Collection types");
        return (Collection<Object>) currentValue;
    }

    private static Object createEntry(Type collectionType, final Object parent, PopulatorContext config) {
        final Type componentType = getComponentType(collectionType);
        final Object value = config.createInstance(componentType, parent);

        final Mutator mutator = config.getMutator(componentType);
        return mutator.mutate(componentType, value, parent, config);
    }

    private static Type getComponentType(Type type) {
        return TypeUtils.getTypeArgument(type, Collection.class, COLLECTION_TYPE_VARIABLE);
    }

    @Override
    public Collection<?> mutate(final Type type, final Object currentValue, final Object parent, final PopulatorContext config) {
        final Collection<Object> collection = ensureCollection(type, currentValue);
        if (currentValue == null || !collection.isEmpty()) {
            return collection;
        }

        final Object value = createEntry(type, parent, config);
        collection.add(value);
        return collection;
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

