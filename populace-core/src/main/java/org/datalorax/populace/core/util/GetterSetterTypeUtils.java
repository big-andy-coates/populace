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

package org.datalorax.populace.core.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;

/**
 * Utils class for validating getters and setters. Required validation is kept to the bare minimum.
 *
 * @author Andrew Coates - 15/06/2015.
 */
public final class GetterSetterTypeUtils {
    private GetterSetterTypeUtils() {
    }

    /**
     * Validate that the getter and setter are both valid, (See {@link #validateGetter} and {@link #validateSetter},
     * respectively for more info), that they belong to the same or relate classes, and that they access the same type
     * of property i.e. that they are a valid pair.
     *
     * @param getter the getter to validate
     * @param setter the setter to validate
     * @throws java.lang.IllegalArgumentException if either are invalid
     */
    public static void validateGetterSetterPair(final Method getter, final Method setter) {
        final StringBuilder builder = new StringBuilder();

        if (!TypeUtils.areRelatedTypes(getter.getDeclaringClass(), setter.getDeclaringClass())) {
            builder.append("getter & setter must be on related classes\n");
        }

        _validateGetter(getter, Optional.of(builder));
        _validateSetter(setter, Optional.of(builder));

        if (setter.getParameterCount() > 0 && !getter.getGenericReturnType().equals(setter.getGenericParameterTypes()[0])) {
            builder.append("getter return type must match setter param type\n");
        }
        if (builder.length() != 0) {
            throw new IllegalArgumentException("Not a valid getter / setter pair. " +
                "getter: " + getter + ", setter: " + setter + ", reason: " + builder);
        }
    }

    /**
     * Validate that the getter is valid i.e. is not static, takes not parameters and has a return value.
     *
     * @param getter the getter to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidGetter(final Method getter) {
        return _validateGetter(getter, Optional.<StringBuilder>empty());
    }

    /**
     * Validate that the setter is valid i.e. is not static and takes a single parameters.
     *
     * @param setter the setter to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidSetter(final Method setter) {
        return _validateSetter(setter, Optional.<StringBuilder>empty());
    }

    /**
     * Validate that the getter is valid i.e. is not static, takes not parameters and has a return value.
     *
     * @param getter the getter to validate
     * @throws java.lang.IllegalArgumentException if either are invalid
     */
    public static void validateGetter(final Method getter) {
        final StringBuilder message = new StringBuilder();
        if (!_validateGetter(getter, Optional.of(message))) {
            throw new IllegalArgumentException(message + ", getter: " + getter);
        }
    }

    /**
     * Validate that the setter is valid i.e. is not static and takes a single parameters.
     *
     * @param setter the setter to validate
     * @throws java.lang.IllegalArgumentException if either are invalid
     */
    public static void validateSetter(final Method setter) {
        final StringBuilder message = new StringBuilder();
        if (!_validateSetter(setter, Optional.of(message))) {
            throw new IllegalArgumentException(message + ", setter: " + setter);
        }
    }

    private static boolean _validateGetter(final Method getter, final Optional<StringBuilder> message) {
        boolean valid = true;
        if (Modifier.isStatic(getter.getModifiers())) {
            valid = false;
            message.ifPresent(builder -> builder.append("Getter can not be static"));
        }

        if (getter.getParameterCount() != 0) {
            valid = false;
            message.ifPresent(builder -> builder.append("Getter must not take parameters"));
        }

        if (getter.getReturnType() == void.class) {
            valid = false;
            message.ifPresent(builder -> builder.append("Getter must return a value"));
        }

        return valid;
    }

    private static boolean _validateSetter(final Method setter, final Optional<StringBuilder> message) {
        boolean valid = true;
        if (Modifier.isStatic(setter.getModifiers())) {
            valid = false;
            message.ifPresent(builder -> builder.append("Setter can not be static"));
        }

        if (setter.getParameterCount() != 1) {
            valid = false;
            message.ifPresent(builder -> builder.append("Setter must take only a single parameter"));
        }

        return valid;
    }
}
