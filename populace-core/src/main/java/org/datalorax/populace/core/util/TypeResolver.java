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

import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.walk.field.TypeTable;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * TypeResolver is capable of resolving {@link java.lang.reflect.TypeVariable TypeVariables} using the provided
 * {@code typeTable} for the different flavours of {@link java.lang.reflect.Type}. The constituent type arguments and
 * bounds are resolved in a recursive manner using the type information available in the type table.
 *
 * @author Andrew Coates - 02/04/2015.
 */
public class TypeResolver {
    private final TypeTable typeTable;

    /**
     * Creates a resolver that will use the information available in the supplied {@code typeTable} to resolve types
     *
     * @param typeTable the type table to use to resolve type variables
     */
    public TypeResolver(final TypeTable typeTable) {
        Validate.notNull(typeTable, "typeTable null");
        this.typeTable = typeTable;
    }

    /**
     * Resolve the provided {@code type} using the type information available
     * <ul>
     * <li>if {@code type} is a {@link java.lang.Class} with type parameters, then it will return a {@link ParameterizedType} where the
     * type parameters have been as resolved as far as is possible.</li>
     * <li>if {@code type} is a {@link java.lang.Class} without type parameters, then the class is returned unchanged.</li>
     * <li>if {@code type} is a {@link java.lang.reflect.ParameterizedType}, then it will return the type with its type parameters
     * resolved as much as is possible.</li>
     * <li>if {@code type} is a {@link java.lang.reflect.TypeVariable}, then it will recursively resolve the type using the type table,
     * returning the final state</li>
     * <li>if {@code type} is a {@link java.lang.reflect.WildcardType}, then it will return the type with any
     * bounds resolved as far as is possible</li>
     * <li>if {@code type} is a {@link java.lang.reflect.GenericArrayType}, then it will return the type with its component
     * type resolved as far as is possible</li>
     * </ul>
     * @param type the type to resolve
     * @return the resolved type.
     * @throws java.lang.UnsupportedOperationException on unsupported implementation of {@link java.lang.reflect.Type}
     */
    public Type resolve(final Type type) {
        return resolve(type, null);
    }

    private Type resolve(final Type type, final TypeToken<?> assigningType) {
        Validate.notNull(type, "type null");

        if (type instanceof Class) {
            return resolveClass((Class<?>) type);
        }
        if (type instanceof ParameterizedType) {
            return resolveParameterisedType((ParameterizedType) type);
        }
        if (type instanceof TypeVariable) {
            return resolveTypeVariable((TypeVariable<?>) type, assigningType);
        }
        if (type instanceof WildcardType) {
            return resolveWildcard((WildcardType) type, assigningType);
        }
        if (type instanceof GenericArrayType) {
            return resolveGenericArray((GenericArrayType) type, assigningType);
        }

        throw new UnsupportedOperationException("Unsupported type: " + type);
    }

    private Type[] resolve(final Type[] types, final TypeToken<?> assigningType) {
        return Arrays.stream(types)
            .map(type -> resolve(type, assigningType))
            .toArray(Type[]::new);
    }

    private Type resolveClass(final Class<?> type) {
        final TypeVariable<? extends Class<?>>[] typeParameters = type.getTypeParameters();
        if (typeParameters.length == 0) {
            return type;
        }

        final ParameterizedType parameterised = TypeUtils.parameterise(type, typeParameters);
        return resolveParameterisedType(parameterised);
    }

    private Type resolveParameterisedType(final ParameterizedType type) {
        final TypeToken<?> typeToken = TypeToken.of(type);

        final Type[] resolvedArgs = Arrays.stream(type.getActualTypeArguments())
            .map(t -> resolve(t, typeToken))
            .toArray(Type[]::new);

        return TypeUtils.parameterise(typeToken.getRawType(), resolvedArgs);
    }

    private Type resolveTypeVariable(final TypeVariable<?> type, final TypeToken<?> assigningType) {
        Type resolved = typeTable.resolveTypeVariable(type);

        if (type.equals(resolved) && assigningType != null) {
            resolved = resolveTypeVariableFromSupers(type, assigningType);
        }

        if (type.equals(resolved)) {
            return type;
        }

        return resolve(resolved);
    }

    private Type resolveWildcard(final WildcardType type, final TypeToken<?> assigningType) {
        final Type[] lowerBounds = type.getLowerBounds();
        if (lowerBounds.length != 0) {
            final Type[] resolvedLowerBounds = resolve(lowerBounds, assigningType);
            if (Arrays.equals(resolvedLowerBounds, lowerBounds)) {
                return type;
            }

            return TypeUtils.wildcardTypeWithLowerBounds(resolvedLowerBounds);
        }

        final Type[] resolvedUpperBounds = resolve(type.getUpperBounds(), assigningType);
        if (Arrays.equals(resolvedUpperBounds, type.getUpperBounds())) {
            return type;
        }

        return TypeUtils.wildcardTypeWithUpperBounds(resolvedUpperBounds);
    }

    private Type resolveGenericArray(final GenericArrayType type, final TypeToken<?> assigningType) {
        final Type resolvedComponentType = resolve(type.getGenericComponentType(), assigningType);
        if (resolvedComponentType.equals(type.getGenericComponentType())) {
            return type;
        }
        return TypeUtils.genericArrayType(resolvedComponentType);
    }

    /**
     * Resolved to provided {@code typeVar}, for the provided {@code type}, using the provided {@code typeTable}
     *
     * @param typeVar the type variable to resolve
     * @param assigningType    the type the variable belongs to.
     * @return the resolved type, or Optional.empty() if the type couldn't be resolved.
     */
    private Type resolveTypeVariableFromSupers(final TypeVariable<?> typeVar, final TypeToken<?> assigningType) {
        final Stream<TypeVariable<?>> typeArgAliases = findSuperAndInterfaceTypeArgumentAliases(typeVar, assigningType);
        final Optional<Type> first = typeArgAliases
            .map(t -> this.resolve(t, assigningType))
            .filter(t -> !(t instanceof TypeVariable))
            .findFirst();
        return first.isPresent() ? first.get() : typeVar;
    }

    /**
     * Get a stream of all available aliases for the provided type variable found in any super types or implemented
     * generic interfaces of the provided {@code type}
     *
     * @param typeVar the type variable to find aliases for
     * @param type    the type whose super classes and interfaces to search for matching type variables
     * @return the type argument of any matching type variables found.
     */
    private static Stream<TypeVariable<?>> findSuperAndInterfaceTypeArgumentAliases(final TypeVariable<?> typeVar,
                                                                                    final TypeToken<?> type) {
        return type.getTypes().stream()
            .filter(t -> t.getType() instanceof ParameterizedType)      // Ignore non-parameterized types
            .flatMap(t -> getTypeArgumentAliases(typeVar, t));    // For each parameterised get type argument aliases
    }

    /**
     * Get a stream of available alias for the provided type variable e.g. given a type:
     * <pre>
     * {@code
     *    interface SomeInterface&lt;T2&gt; {}
     *    class SomeType&lt;T2&gt; implements SomeInterface&lt;T2&gt; {}
     *
     *    TypeVariable&lt;?&gt; typeVariable = SomeType.class.getTypeParameters()[0];
     *    TypeToken interfaceToken = findTypeToken(TypeToken.of(SomeType.class).getTypes(), SomeInterface.class);
     *    getTypeArgumentAliases(typeVariable, interfaceToken);
     * }
     * </pre>
     * <p>
     * will return a stream containing T2.
     *
     * @param typeVar the type variable to find an alias for
     * @param type    the type token to search for matching type variables
     * @return the type argument of any matching type variables found.
     */
    private static Stream<TypeVariable<?>> getTypeArgumentAliases(final TypeVariable<?> typeVar,
                                                                  final TypeToken<?> type) {
        Validate.isInstanceOf(ParameterizedType.class, type.getType());
        final Type[] sourceTypeArgs = ((ParameterizedType) type.getType()).getActualTypeArguments();
        final TypeVariable<? extends Class<?>>[] sourceAliases = type.getRawType().getTypeParameters();

        return IntStream.range(0, sourceTypeArgs.length)
            .filter(i -> sourceTypeArgs[i].equals(typeVar))  // Filter out indexes with different type argument
            .filter(i -> !sourceAliases[i].equals(typeVar))  // Filter out aliases that match existing
            .mapToObj(i -> sourceAliases[i]);                        // Return aliases
    }
}
