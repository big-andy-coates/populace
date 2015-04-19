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

import org.apache.commons.lang3.Validate;

import java.lang.reflect.*;
import java.util.*;

/**
 * @author Andrew Coates - 04/03/2015.
 */
public final class TypeUtils {
    private static final List<Class<?>> PRIMITIVE_TYPES = Collections.unmodifiableList(Arrays.asList(
        boolean.class, byte.class, char.class, short.class, int.class, long.class, float.class, double.class));

    private static final List<Class<?>> BOXED_PRIMITIVE_TYPES = Collections.unmodifiableList(Arrays.asList(
        Boolean.class, Byte.class, Character.class, Short.class, Integer.class, Long.class, Float.class, Double.class));

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_BOXED_TYPES = new HashMap<Class<?>, Class<?>>() {{
        put(boolean.class, Boolean.class);
        put(byte.class, Byte.class);
        put(char.class, Character.class);
        put(short.class, Short.class);
        put(int.class, Integer.class);
        put(long.class, Long.class);
        put(float.class, Float.class);
        put(double.class, Double.class);
    }};

    public static List<Class<?>> getPrimitiveTypes() {
        return PRIMITIVE_TYPES;
    }

    public static List<Class<?>> getBoxedPrimitiveTypes() {
        return BOXED_PRIMITIVE_TYPES;
    }

    public static Class<?> getBoxedTypeForPrimitive(Class<?> primitiveType) {
        final Class<?> boxed = PRIMITIVE_TO_BOXED_TYPES.get(primitiveType);
        Validate.notNull(boxed, "Not a primitive type: %s", primitiveType);
        return boxed;
    }

    /**
     * Gets a single type argument, resolved to a class, from the set of type arguments of a class/interface based on a
     * the {@code toClass} subtype. For instance, given the parameterised type representing {@code Map&lt;String,Integer&gt;}
     * , the {@code toClass} value of {@code Map.class}, and the {@code typeVariable} of
     * {@code Map.class.getTypeParameters()[0]}, then this method will return String.class.  This method will work even
     * if the type represented by {@code type} is a subtype of the required type and does not itself have any template
     * arguments. For example, this method will determine that both of the parameters for the interface {@link Map} are
     * {@link Object} for the subtype {@link java.util.Properties Properties} even though the subtype does not directly
     * implement the {@code Map} interface. If the {@code type} is not a parameterised type, but a raw {@code Class} then
     * the method will return {@code Object.class}
     * <p>
     * This method throws {@link java.lang.IllegalArgumentException} if {@code type} is not assignable to {@code toClass}.
     * It returns an Object.class if the actual type parameter can not be determined.
     *
     * @param type         the type from which to determine the type parameters of {@code toClass}
     * @param toClass      the class whose type parameter is to be determined based on the subtype {@code type}
     * @param typeVariable the specific typeVariable of {@code toClass} to retrieve.
     * @param <T>          The type of {@code toClass}
     * @return the {@code Class} of the type argument, or null if {@code type} is not assignable to {@code toClass}
     * @throws java.lang.IllegalArgumentException if {@code type} is not assignable to {@code toClass}.
     */
    public static <T> Type getTypeArgument(final Type type, final Class<T> toClass, final TypeVariable<Class<T>> typeVariable) {
        final Map<TypeVariable<?>, Type> typeArguments = org.apache.commons.lang3.reflect.TypeUtils.getTypeArguments(type, toClass);
        if (typeArguments == null) {
            throw new IllegalArgumentException(type + " is not assignable to " + toClass);
        }

        final Type typeArg = typeArguments.get(typeVariable);
        if (typeArg instanceof ParameterizedType) {
            return typeArg;
        }
        if (typeArg instanceof Class) {
            return typeArg;
        }
        return Object.class;
    }

    /**
     * Get the array component type of {@code type}.
     * @param type the type to be checked
     * @return component type or null if type is not an array type
     */
    public static Type getArrayComponentType(final Type type) {
        return org.apache.commons.lang3.reflect.TypeUtils.getArrayComponentType(type);
    }

    /**
     * Learn whether the specified type denotes an array type.
     *
     * @param type the type to be checked
     * @return {@code true} if {@code type} is an array class or a {@link java.lang.reflect.GenericArrayType}.
     * @see org.apache.commons.lang3.reflect.TypeUtils#isArrayType(java.lang.reflect.Type)
     */
    public static boolean isArrayType(final Type type) {
        return org.apache.commons.lang3.reflect.TypeUtils.isArrayType(type);
    }

    /**
     * Checks if the subject type may be implicitly cast to the target type following the Java generics rules.
     *
     * @param type   the subject type to be assigned to the target type
     * @param toType the target type
     * @return {@code true} if {@code type} is assignable to {@code toType}.
     * @see org.apache.commons.lang3.reflect.TypeUtils#isAssignable(Type, Type)
     */
    public static boolean isAssignable(final Type type, final Type toType) {
        return org.apache.commons.lang3.reflect.TypeUtils.isAssignable(type, toType);
    }

    /**
     * Create a wild card type instance with no bounds
     *
     * @return {@link java.lang.reflect.WildcardType}
     */
    public static WildcardType wildcardType() {
        return org.apache.commons.lang3.reflect.TypeUtils.wildcardType()
            .withUpperBounds(Object.class)  // implicit
            .build();
    }

    /**
     * Create a wild card type instance with the specified {@code lowerBounds}
     *
     * @param lowerBounds the lower bounds to add.
     * @return {@link java.lang.reflect.WildcardType}
     */
    public static WildcardType wildcardTypeWithLowerBounds(final Type... lowerBounds) {
        return org.apache.commons.lang3.reflect.TypeUtils.wildcardType()
            .withUpperBounds(Object.class)  // implicit
            .withLowerBounds(lowerBounds)
            .build();
    }

    /**
     * Create a wild card type instance with the specified {@code upperBounds}
     *
     * @param upperBounds the upper bounds to add.
     * @return {@link java.lang.reflect.WildcardType}
     */
    public static WildcardType wildcardTypeWithUpperBounds(final Type... upperBounds) {
        return org.apache.commons.lang3.reflect.TypeUtils.wildcardType()
            .withUpperBounds(upperBounds.length == 0 ? new Type[]{Object.class} : upperBounds)
            .build();
    }

    /**
     * Create a generic array type instance.
     *
     * @param componentType the type of the elements of the array. For example the component type of {@code boolean[]}
     *                      is {@code boolean}
     * @return {@link GenericArrayType}
     * @see org.apache.commons.lang3.reflect.TypeUtils#genericArrayType(java.lang.reflect.Type)
     */
    public static GenericArrayType genericArrayType(final Type componentType) {
        return org.apache.commons.lang3.reflect.TypeUtils.genericArrayType(componentType);
    }

    /**
     * Create a parameterised type instance.
     *
     * @param raw the raw class to create a parameterized type instance for
     * @param typeArguments the types used for parameterisation
     * @return {@link ParameterizedType}
     *
     * @see org.apache.commons.lang3.reflect.TypeUtils#parameterize(java.lang.Class, Type...)
     */
    public static ParameterizedType parameterise(final Class<?> raw, final Type... typeArguments) {
        return org.apache.commons.lang3.reflect.TypeUtils.parameterize(raw, typeArguments);
    }

    /**
     * Get the raw {@link Class} from the {@code type} provided
     *
     * @param type to resolve
     * @param assigningType type to be resolved against
     * @return the resolved {@link Class} object or {@code null} if the type could not be resolved
     *
     * @see org.apache.commons.lang3.reflect.TypeUtils#getRawType(java.lang.reflect.Type, java.lang.reflect.Type)
     */
    public static Class<?> getRawType(final Type type, final Type assigningType) {
        return org.apache.commons.lang3.reflect.TypeUtils.getRawType(type, assigningType);
    }

    /**
     * Returns an abbreviated generic name for logging purposes.  Package names are abbreviated to a single character.
     * Generic info is included.
     *
     * @param type the type whose name should be abbreviated.
     * @return the abbreviated class name
     */
    public static String abbreviatedName(final Type type) {
        if (type instanceof Class) {
            return abbreviatedName(((Class<?>) type).getName());
        }

        if (type instanceof ParameterizedType) {
            final ParameterizedType pt = (ParameterizedType) type;
            return abbreviatedName(pt.getTypeName());
        }

        throw new UnsupportedOperationException("Type not supported: " + type);
    }

    /**
     * Ensures a consistent implementation of the different sub types i.e. {@link java.lang.reflect.ParameterizedType},
     * {@link java.lang.Class}, {@link java.lang.reflect.TypeVariable} and {@link java.lang.reflect.GenericArrayType}.
     * <p>
     * This is useful if you need to compare them, as implementations from different vendors do not generally compare as
     * equal, even if the type information they convey is equivalent.
     * <p>
     * Calling this method with two equivalent types, from two different implementations, will result in two {@code type}s
     * that will be equal i.e. {@code ensureTypeImpl(t1).equals(ensureTypeImpl(t2))} will return true.
     *
     * @param type the type, from any implementation, for which a consistent implementation is required.
     * @return the same type, but from a consistent implementation.
     */
    public static Type ensureConsistentType(final Type type) {
        if (type instanceof Class) {
            return type;    // Final class, so only one impl.
        }

        if (type instanceof ParameterizedType) {
            return ensureConsistentParameterisedType((ParameterizedType) type);
        }

        if (type instanceof TypeVariable) {
            return type;
        }

        if (type instanceof WildcardType) {
            return ensureConsistentWildcard((WildcardType) type);
        }

        if (type instanceof GenericArrayType) {
            return ensureConsistentGenericArrayType((GenericArrayType) type);
        }

        throw new UnsupportedOperationException("Unsupported type: " + type.getClass());
    }

    private static Type ensureConsistentParameterisedType(final ParameterizedType type) {
        final Type[] consistentTypeArgs = Arrays.stream(type.getActualTypeArguments())
            .map(TypeUtils::ensureConsistentType)
            .toArray(Type[]::new);

        return parameterise(getRawType(type, null), consistentTypeArgs);
    }

    private static Type ensureConsistentWildcard(final WildcardType type) {
        final Type[] lowerBounds = Arrays.stream(type.getLowerBounds()).map(TypeUtils::ensureConsistentType).toArray(Type[]::new);
        final Type[] upperBounds = Arrays.stream(type.getUpperBounds()).map(TypeUtils::ensureConsistentType).toArray(Type[]::new);
        return org.apache.commons.lang3.reflect.TypeUtils.wildcardType()
            .withLowerBounds(lowerBounds)
            .withUpperBounds(upperBounds)
            .build();
    }

    private static Type ensureConsistentGenericArrayType(final GenericArrayType type) {
        final Type componentType = ensureConsistentType(type.getGenericComponentType());
        return org.apache.commons.lang3.reflect.TypeUtils.genericArrayType(componentType);
    }

    private static String abbreviatedName(final String typeName) {
        final StringBuilder builder = new StringBuilder();

        int startOfLastWord = 0;
        for (int i = 0; i != typeName.length(); ++i) {
            final char c = typeName.charAt(i);
            if (c == '.') {
                builder.append(typeName.charAt(startOfLastWord)).append('.');
                startOfLastWord = i + 1;
            } else if (c == '<' || '>' == c) {
                builder.append(typeName.substring(startOfLastWord, i + 1));
                startOfLastWord = i + 1;
            }
        }

        builder.append(typeName.substring(startOfLastWord));
        return builder.toString();
    }
}