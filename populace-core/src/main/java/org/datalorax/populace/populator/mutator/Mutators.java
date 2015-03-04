package org.datalorax.populace.populator.mutator;

import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.mutator.change.ChangeEnumMutator;
import org.datalorax.populace.populator.mutator.change.ChangeListElementsMutator;
import org.datalorax.populace.populator.mutator.change.ChangeMapValuesMutator;
import org.datalorax.populace.populator.mutator.change.ChangeSetElementsMutator;
import org.datalorax.populace.populator.mutator.commbination.ChainMutator;
import org.datalorax.populace.populator.mutator.ensure.EnsureCollectionNotEmptyMutator;
import org.datalorax.populace.populator.mutator.ensure.EnsureMapNotEmptyMutator;
import org.datalorax.populace.populator.mutator.ensure.EnsureMutator;
import org.datalorax.populace.type.TypeUtils;
import org.datalorax.populace.typed.ImmutableTypeMap;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper functions for working with {@link org.datalorax.populace.populator.Mutator mutators}
 *
 * @author datalorax - 01/03/2015.
 */
public final class Mutators {
    private static final ImmutableTypeMap<Mutator> DEFAULT;

    public static ImmutableTypeMap.Builder<Mutator> defaultMutators() {
        return ImmutableTypeMap.asBuilder(DEFAULT);
    }

    public static Mutator chain(final Mutator first, final Mutator second, final Mutator... additional) {
        return ChainMutator.chain(first, second, additional);
    }

    static {
        final ImmutableTypeMap.Builder<Mutator> builder = ImmutableTypeMap.newBuilder();

        TypeUtils.getPrimitiveTypes().forEach(type -> builder.withSpecificType(type, PrimitiveMutator.INSTANCE));
        TypeUtils.getBoxedPrimitiveTypes().forEach(type -> builder.withSpecificType(type, PrimitiveMutator.INSTANCE));

        // Todo(ac): what about other java lang types..
        builder.withSpecificType(String.class, StringMutator.INSTANCE);
        builder.withSpecificType(Date.class, DateMutator.INSTANCE);

        // Todo(ac): what about base Collection.class? what instantiates that and populates...
        builder.withSuperType(Set.class, chain(EnsureMutator.INSTANCE, ChangeSetElementsMutator.INSTANCE));
        builder.withSuperType(List.class, chain(EnsureMutator.INSTANCE, EnsureCollectionNotEmptyMutator.INSTANCE, ChangeListElementsMutator.INSTANCE));
        builder.withSuperType(Map.class, chain(EnsureMutator.INSTANCE, EnsureMapNotEmptyMutator.INSTANCE, ChangeMapValuesMutator.INSTANCE));
        builder.withSuperType(Enum.class, chain(EnsureMutator.INSTANCE, ChangeEnumMutator.INSTANCE));

        DEFAULT = builder
            .withDefaultArray(ArrayMutator.INSTANCE)
            .withDefault(EnsureMutator.INSTANCE)
            .build();
    }

    private Mutators() {
    }
}
