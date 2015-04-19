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


import org.testng.annotations.Test;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Andrew Coates - 27/02/2015.
 */
public class ImmutableTypeMapTest {
    private static final Map<Type, String> NO_SPECIFIC_VALUES = Collections.emptyMap();
    private static final Map<Class<?>, String> NO_SUPER_VALUES = Collections.emptyMap();
    private static final Map<String, String> NO_PACKAGE_VALUES = Collections.emptyMap();

    @Test
    public void shouldGetDefaultValueForNonArrayKeyIfNothingElseMatches() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withSpecificType(String.class, "String")
            .withSuperType(List.class, "List")
            .build();

        // When:
        final String value = collection.get(int.class);

        // Then:
        assertThat(value, is("default"));
    }

    @Test
    public void shouldGetDefaultArrayValueIfInstalledForArrayKeyIfNothingElseMatches() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withSpecificType(String.class, "String")
            .withSuperType(List.class, "List")
            .withArrayDefault("array default")
            .build();

        // When:
        final String value = collection.get(TypeUtils.genericArrayType(long.class));

        // Then:
        assertThat(value, is("array default"));
    }

    @Test
    public void shouldGetDefaultValueForArrayKeyIfNothingElseMatchesAndNoArrayDefaultInstalled() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withSpecificType(String.class, "String")
            .withSuperType(List.class, "List")
            .build();

        // When:
        final String value = collection.get(TypeUtils.genericArrayType(long.class));

        // Then:
        assertThat(value, is("default"));
    }

    @Test
    public void shouldGetSpecificOverDefaultValue() throws Exception {
        // Given:
        final ParameterizedType specificType = TypeUtils.parameterise(HashSet.class, String.class);
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withSpecificType(specificType, "specific")
            .build();

        // When:
        final String value = collection.get(specificType);

        // Then:
        assertThat(value, is("specific"));
    }

    @Test
    public void shouldGetSpecificOverSuperValue() throws Exception {
        // Given:
        final ParameterizedType specificType = TypeUtils.parameterise(HashSet.class, String.class);
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withSpecificType(specificType, "specific")
            .withSuperType(Set.class, "Set")
            .build();

        // When:
        final String value = collection.get(specificType);

        // Then:
        assertThat(value, is("specific"));
    }

    @Test
    public void shouldGetSuperOverDefaultValue() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withSuperType(Set.class, "super")
            .build();

        // When:
        final String value = collection.get(HashSet.class);

        // Then:
        assertThat(value, is("super"));
    }

    @Test
    public void shouldPickMostSpecificSuperValue() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withSuperType(Collection.class, "no cigar")
            .withSuperType(Set.class, "most specific")
            .build();

        // When:
        final String value = collection.get(HashSet.class);

        // Then:
        assertThat(value, is("most specific"));
    }

    @Test
    public void shouldPickSameSuperValueRegardlessOfOrder() throws Exception {
        // Given:
        final Map<Class<?>, String> naturalSuperValues = new TreeMap<Class<?>, String>(new ClassComparator()) {{
            put(Collection.class, "no cigar");
            put(Set.class, "most specific");
        }};
        final Map<Class<?>, String> reversedSuperValues = new TreeMap<Class<?>, String>(new ClassComparator().reversed()) {{
            put(Collection.class, "no cigar");
            put(Set.class, "most specific");
        }};
        final ImmutableTypeMap<String> natural = new ImmutableTypeMap<>(NO_SPECIFIC_VALUES, naturalSuperValues, NO_PACKAGE_VALUES, "default", "array default");
        final ImmutableTypeMap<String> reversed = new ImmutableTypeMap<>(NO_SPECIFIC_VALUES, reversedSuperValues, NO_PACKAGE_VALUES, "default", "array default");

        // When:
        final String nValue = natural.get(TypeUtils.parameterise(HashSet.class, String.class));
        final String rValue = reversed.get(TypeUtils.parameterise(HashSet.class, String.class));

        // Then:
        assertThat(nValue, is(rValue));
    }

    @Test
    public void shouldGetSpecificOverPackage() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withSpecificType(String.class, "specific")
            .withPackageType(String.class.getPackage().getName(), "package")
            .build();

        // When:
        final String value = collection.get(String.class);

        // Then:
        assertThat(value, is("specific"));
    }

    @Test
    public void shouldGetSuperOverPackage() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withSuperType(Set.class, "super")
            .withPackageType(Set.class.getPackage().getName(), "package")
            .build();

        // When:
        final String value = collection.get(HashSet.class);

        // Then:
        assertThat(value, is("super"));
    }

    @Test
    public void shouldNotGetPackageOverArrayDefault() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withPackageType(Long.class.getPackage().getName(), "package")
            .withArrayDefault("array default")
            .build();

        // When:
        final String value = collection.get(TypeUtils.genericArrayType(Long.class));

        // Then:
        assertThat(value, is("array default"));
    }

    @Test
    public void shouldHandlePrimitiveTypesWithPackageLevelValues() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withPackageType(Long.class.getPackage().getName(), "package")
            .build();

        // When:
        final String value = collection.get(long.class);

        // Then:
        assertThat(value, is("package"));
    }

    @Test
    public void shouldNotMatchChildPackages() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withPackageType(Collection.class.getPackage().getName(), "match")
            .withPackageType(ConcurrentHashMap.class.getPackage().getName(), "child")
            .build();

        // When:
        final String value = collection.get(HashSet.class);

        // Then:
        assertThat(value, is("match"));
    }

    @Test
    public void shouldPickMostSpecificPackageValue() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withPackageType("org", "no cigar")
            .withPackageType("org.datalorax", "most specific")
            .build();

        // When:
        final String value = collection.get(getClass());

        // Then:
        assertThat(value, is("most specific"));
    }

    @Test
    public void shouldPickSamePackageValueRegardlessOfOrder() throws Exception {
        // Given:
        final Map<String, String> naturalSuperValues = new TreeMap<String, String>(Comparator.<String>naturalOrder()) {{
            put("org", "no cigar");
            put("org.datalorax", "most specific");
        }};
        final Map<String, String> reversedSuperValues = new TreeMap<String, String>(Comparator.<String>reverseOrder()) {{
            put("org", "no cigar");
            put("org.datalorax", "most specific");
        }};
        final ImmutableTypeMap<String> natural = new ImmutableTypeMap<>(NO_SPECIFIC_VALUES, NO_SUPER_VALUES, naturalSuperValues, "default", "array default");
        final ImmutableTypeMap<String> reversed = new ImmutableTypeMap<>(NO_SPECIFIC_VALUES, NO_SUPER_VALUES, reversedSuperValues, "default", "array default");

        // When:
        final String nValue = natural.get(getClass());
        final String rValue = reversed.get(getClass());

        // Then:
        assertThat(nValue, is(rValue));
    }

    @Test
    public void shouldGetPackageOverDefault() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withPackageType(Set.class.getPackage().getName(), "package")
            .build();

        // When:
        final String value = collection.get(Set.class);

        // Then:
        assertThat(value, is("package"));
    }

    @Test
    public void shouldBeAbleToOverrideDefault() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withDefault("new default")
            .build();

        // When:
        final String value = collection.get(String.class);

        // Then:
        assertThat(value, is("new default"));
    }

    @Test
    public void shouldHandlePrimitivesForPackageValues() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withPackageType(Integer.class.getPackage().getName(), "package")
            .build();

        // When:
        final String value = collection.get(int.class);

        // Then:
        assertThat(value, is("package"));
    }

    @Test
    public void shouldReturnArrayDefaultForArrayOfObjects() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withPackageType(Integer.class.getPackage().getName(), "package")
            .withArrayDefault("array default")
            .build();
        final GenericArrayType type = TypeUtils.genericArrayType(Object.class);

        // When:
        final String value = collection.get(type);

        // Then:
        assertThat(value, is("array default"));
    }

    @Test
    public void shouldWorkWithMixtureOfTypeImplementations() throws Exception {
        // Given:
        class SomeType {
            Collection<?> field;
        }
        final Type oracleParameterisedType = SomeType.class.getDeclaredField("field").getGenericType();
        final Type otherParameterisedType = TypeUtils.parameterise(Collection.class, TypeUtils.wildcardType());

        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withSpecificType(oracleParameterisedType, "specific")
            .withArrayDefault("array default")
            .build();

        // When:
        final String value = collection.get(otherParameterisedType);

        // Then:
        assertThat(value, is("specific"));
    }

    @Test
    public void shouldWorkWithMixtureOfTypeImplementations_OtherWay() throws Exception {
        // Given:
        class SomeType {
            Collection<?> field;
        }
        final Type oracleParameterisedType = SomeType.class.getDeclaredField("field").getGenericType();
        final Type otherParameterisedType = TypeUtils.parameterise(Collection.class, TypeUtils.wildcardType());

        final ImmutableTypeMap<String> collection = ImmutableTypeMap.newBuilder("default")
            .withSpecificType(otherParameterisedType, "specific")
            .withArrayDefault("array default")
            .build();

        // When:
        final String value = collection.get(oracleParameterisedType);

        // Then:
        assertThat(value, is("specific"));
    }

    private static class ClassComparator implements Comparator<Class<?>> {
        @Override
        public int compare(final Class<?> c1, final Class<?> c2) {
            return c1.getCanonicalName().compareTo(c2.getCanonicalName());
        }
    }
}