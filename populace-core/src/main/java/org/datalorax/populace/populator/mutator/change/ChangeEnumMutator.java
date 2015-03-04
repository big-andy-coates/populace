package org.datalorax.populace.populator.mutator.change;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.datalorax.populace.populator.Mutator;
import org.datalorax.populace.populator.PopulatorContext;

import java.lang.reflect.Type;

/**
 * Change mutator for enums. The mutator will change the value of the enum to another valid value in the enumeration,
 * assuming there is one. For enumerations with only a single value the value will not be changed. If the current value
 * is null the value will also not be changed.
 * @author datalorax - 02/03/2015.
 */
public class ChangeEnumMutator implements Mutator {
    public static final Mutator INSTANCE = new ChangeEnumMutator();

    @Override
    public Object mutate(final Type type, final Object currentValue, final Object parent, final PopulatorContext config) {
        Validate.isTrue(TypeUtils.getRawType(type, null).isEnum(), "Enum type expected");
        return currentValue == null ? currentValue : changeEnum((Enum)currentValue);
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

    private Object changeEnum(final Enum currentValue) {
        final Object[] allValues = currentValue.getDeclaringClass().getEnumConstants();
        if (allValues.length <= 1) {
            return currentValue;
        }

        final int newOrdinal = (currentValue.ordinal() + 1) % allValues.length;
        return allValues[newOrdinal];
    }
}
