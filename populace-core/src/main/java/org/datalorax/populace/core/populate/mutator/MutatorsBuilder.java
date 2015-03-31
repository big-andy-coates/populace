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

package org.datalorax.populace.core.populate.mutator;

import org.datalorax.populace.core.populate.Mutator;
import org.datalorax.populace.core.populate.mutator.change.ChangeBigDecimalMutator;
import org.datalorax.populace.core.populate.mutator.change.ChangeEnumMutator;
import org.datalorax.populace.core.populate.mutator.change.ChangePrimitiveMutator;
import org.datalorax.populace.core.populate.mutator.change.ChangeStringMutator;
import org.datalorax.populace.core.populate.mutator.ensure.EnsureArrayElementsNotNullMutator;
import org.datalorax.populace.core.populate.mutator.ensure.EnsureCollectionNotEmptyMutator;
import org.datalorax.populace.core.populate.mutator.ensure.EnsureMapNotEmptyMutator;
import org.datalorax.populace.core.populate.mutator.ensure.EnsureMutator;
import org.datalorax.populace.core.util.ImmutableTypeMap;
import org.datalorax.populace.core.util.TypeUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

import static org.datalorax.populace.core.populate.mutator.Mutators.chain;

/**
 * Builder for {@link Mutators} collection
 *
 * @author Andrew Coates - 01/03/2015.
 */
final class MutatorsBuilder implements  Mutators.Builder {
    private static final Mutators DEFAULT;

    private final ImmutableTypeMap.Builder<Mutator> mutatorsBuilder;

    MutatorsBuilder(final ImmutableTypeMap<Mutator> mutators) {
        this.mutatorsBuilder = ImmutableTypeMap.asBuilder(mutators);
    }

    private MutatorsBuilder() {
        this.mutatorsBuilder = ImmutableTypeMap.newBuilder(EnsureMutator.INSTANCE);
    }

    static {
        final Mutators.Builder builder = new MutatorsBuilder();

        TypeUtils.getPrimitiveTypes().forEach(type -> builder.withSpecificMutator(type, ChangePrimitiveMutator.INSTANCE));
        TypeUtils.getBoxedPrimitiveTypes().forEach(type -> builder.withSpecificMutator(type, chain(EnsureMutator.INSTANCE, ChangePrimitiveMutator.INSTANCE)));

        builder.withSpecificMutator(String.class, chain(EnsureMutator.INSTANCE, ChangeStringMutator.INSTANCE));
        builder.withSpecificMutator(BigDecimal.class, chain(EnsureMutator.INSTANCE, ChangeBigDecimalMutator.INSTANCE));
        builder.withSpecificMutator(Date.class, DateMutator.INSTANCE);

        // Todo(ac): Just a throught... but can't Collection work similar to set.  Copy contents into array, return RawElements using this. Have #set() call clear collection & add all again
        builder.withSuperMutator(Collection.class, chain(EnsureMutator.INSTANCE, EnsureCollectionNotEmptyMutator.INSTANCE)); // Todo(ac):, ChangeCollectionElementsMutator.INSTANCE));
        builder.withSuperMutator(Set.class, chain(EnsureMutator.INSTANCE, EnsureCollectionNotEmptyMutator.INSTANCE)); // todo(ac):, EnsureSetElementsNotNullMutator.INSTANCE));
        builder.withSuperMutator(List.class, chain(EnsureMutator.INSTANCE, EnsureCollectionNotEmptyMutator.INSTANCE)); /// , EnsureListElementsNotNullMutator.INSTANCE));
        builder.withSuperMutator(Map.class, chain(EnsureMutator.INSTANCE, EnsureMapNotEmptyMutator.INSTANCE)); //, EnsureMapValuesNotNullMutator.INSTANCE));
        builder.withSuperMutator(Enum.class, chain(EnsureMutator.INSTANCE, ChangeEnumMutator.INSTANCE));

        builder.withArrayDefaultMutator(chain(EnsureMutator.INSTANCE, EnsureArrayElementsNotNullMutator.INSTANCE));

        DEFAULT = builder.build();
    }

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
}
