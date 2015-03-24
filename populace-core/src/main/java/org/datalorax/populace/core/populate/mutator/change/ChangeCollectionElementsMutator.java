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

package org.datalorax.populace.core.populate.mutator.change;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.populate.Mutator;
import org.datalorax.populace.core.populate.PopulatorContext;
import org.datalorax.populace.core.util.TypeUtils;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;

/**
 * Change mutator for {@link java.util.Collection collections}.
 *
 * If the currentValue is null then this mutator does nothing. Consider using
 * {@link org.datalorax.populace.core.populate.mutator.ensure.EnsureMutator} to first ensure the current value is not null,
 * if the required behaviour is to always ensure a non-null, populated collection instance.
 *
 * Non null values are modified by adding an additional instance to the collection of the required type.
 * This is done as mutating entries of some collection implementations, e.g. {@link java.util.Set}, can invalidate the
 * collection and lead to undefined behaviour.
 *
 * @author Andrew Coates - 27/02/2015.
 */
public class ChangeCollectionElementsMutator implements Mutator {
    public static final ChangeCollectionElementsMutator INSTANCE = new ChangeCollectionElementsMutator();
    private static final TypeVariable<Class<Collection>> COLLECTION_TYPE_VARIABLE = Collection.class.getTypeParameters()[0];

    @Override
    public Collection<?> mutate(Type type, Object currentValue, final Object parent, PopulatorContext config) {
        final Collection<Object> collection = ensureCollection(type, currentValue);
        if (collection == null) {
            return null;
        }

        final Type defaultComponentType = getComponentType(type);
        final Object element = findNonNullElement(collection);
        final Type componentType = element == null ? defaultComponentType : element.getClass();
        final Mutator componentMutator = element == null ? config.getMutator(defaultComponentType) : config.getMutator(element.getClass());

        Object newItem = config.createInstance(componentType, null);
        boolean added;
        do {
            newItem = componentMutator.mutate(componentType, newItem, null, config);

            added = collection.add(newItem);
        } while (!added);

        return collection;
    }

    @SuppressWarnings("unchecked")
    private Collection<Object> ensureCollection(final Type type, final Object currentValue) {
        Validate.isAssignableFrom(Collection.class, TypeUtils.getRawType(type, null), "Unsupported type %s", type);
        return (Collection<Object>) currentValue;
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

    private Type getComponentType(Type type) {
        return TypeUtils.getTypeArgument(type, Collection.class, COLLECTION_TYPE_VARIABLE);
    }

    private Object findNonNullElement(final Collection<Object> collection) {
        for (Object o : collection) {
            if (o != null) {
                return o;
            }
        }

        return null;
    }
}
