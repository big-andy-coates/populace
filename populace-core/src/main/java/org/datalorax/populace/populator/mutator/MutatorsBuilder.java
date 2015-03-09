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

package org.datalorax.populace.populator.mutator;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.mutator.change.ChangeCollectionElementsMutator;
import org.datalorax.populace.populator.mutator.change.ChangeEnumMutator;
import org.datalorax.populace.populator.mutator.change.ChangeListElementsMutator;
import org.datalorax.populace.populator.mutator.change.ChangeMapValuesMutator;
import org.datalorax.populace.populator.mutator.ensure.EnsureCollectionNotEmptyMutator;
import org.datalorax.populace.populator.mutator.ensure.EnsureMapNotEmptyMutator;
import org.datalorax.populace.populator.mutator.ensure.EnsureMutator;
import org.datalorax.populace.type.TypeUtils;
import org.datalorax.populace.typed.ImmutableTypeMap;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.datalorax.populace.populator.mutator.Mutators.chain;

/**
 * Builder for {@link org.datalorax.populace.populator.mutator.Mutators} collection
 *
 * @author Andrew Coates - 01/03/2015.
 */
final class MutatorsBuilder implements  Mutators.Builder {
    private static final Mutators DEFAULT;

    private final ImmutableTypeMap.Builder<Mutator> mutatorsBuilder;

    public static Mutators defaults() {
        return DEFAULT;
    }

    @Override
    public Mutators.Builder withSpecificMutator(final Type type, final Mutator mutator) {
        mutatorsBuilder.withSpecificType(type, mutator);
        return this;
    }

    @Override
    public Mutators.Builder withSuperMutator(final Class<?> baseClass, final Mutator mutator) {
        mutatorsBuilder.withSuperType(baseClass, mutator);
        return this;
    }

    @Override
    public Mutators.Builder withArrayDefaultMutator(final Mutator mutator) {
        mutatorsBuilder.withArrayDefault(mutator);
        return this;
    }

    @Override
    public Mutators.Builder withDefaultMutator(final Mutator mutator) {
        mutatorsBuilder.withDefault(mutator);
        return this;
    }

    @Override
    public Mutators build() {
        return new Mutators(mutatorsBuilder.build());
    }

    MutatorsBuilder(final ImmutableTypeMap<Mutator> mutators) {
        this.mutatorsBuilder = ImmutableTypeMap.asBuilder(mutators);
    }

    private MutatorsBuilder() {
        this.mutatorsBuilder = ImmutableTypeMap.newBuilder(EnsureMutator.INSTANCE);
    }

    static {
        final Mutators.Builder builder = new MutatorsBuilder();

        TypeUtils.getPrimitiveTypes().forEach(type -> builder.withSpecificMutator(type, PrimitiveMutator.INSTANCE));
        TypeUtils.getBoxedPrimitiveTypes().forEach(type -> builder.withSpecificMutator(type, PrimitiveMutator.INSTANCE));

        // Todo(ac): what about other java lang types.. BigDecimal, etc.
        builder.withSpecificMutator(String.class, StringMutator.INSTANCE);
        builder.withSpecificMutator(Date.class, DateMutator.INSTANCE);

        builder.withSuperMutator(Collection.class, chain(EnsureMutator.INSTANCE, ChangeCollectionElementsMutator.INSTANCE));
        builder.withSuperMutator(List.class, chain(EnsureMutator.INSTANCE, EnsureCollectionNotEmptyMutator.INSTANCE, ChangeListElementsMutator.INSTANCE));
        builder.withSuperMutator(Map.class, chain(EnsureMutator.INSTANCE, EnsureMapNotEmptyMutator.INSTANCE, ChangeMapValuesMutator.INSTANCE));
        builder.withSuperMutator(Enum.class, chain(EnsureMutator.INSTANCE, ChangeEnumMutator.INSTANCE));

        DEFAULT = builder
            .withArrayDefaultMutator(ArrayMutator.INSTANCE)
            .build();
    }
}
