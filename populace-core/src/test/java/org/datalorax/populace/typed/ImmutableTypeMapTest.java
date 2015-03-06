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

package org.datalorax.populace.typed;


import org.apache.commons.lang3.reflect.TypeUtils;
import org.testng.annotations.Test;

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
    private static final Map<Package, String> NO_PACKAGE_VALUES = Collections.emptyMap();

    @Test
    public void shouldGetDefaultValueForNonArrayKeyIfNothingElseMatches() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.<String>newBuilder("default")
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
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.<String>newBuilder("default")
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
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.<String>newBuilder("default")
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
        final ParameterizedType specificType = TypeUtils.parameterize(HashSet.class, String.class);
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.<String>newBuilder("default")
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
        final ParameterizedType specificType = TypeUtils.parameterize(HashSet.class, String.class);
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.<String>newBuilder("default")
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
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.<String>newBuilder("default")
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
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.<String>newBuilder("default")
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
        final ImmutableTypeMap<String> natural = new ImmutableTypeMap<String>(NO_SPECIFIC_VALUES, naturalSuperValues, NO_PACKAGE_VALUES, "default", "array default");
        final ImmutableTypeMap<String> reversed = new ImmutableTypeMap<String>(NO_SPECIFIC_VALUES, reversedSuperValues, NO_PACKAGE_VALUES, "default", "array default");

        // When:
        final String nValue = natural.get(TypeUtils.parameterize(HashSet.class, String.class));
        final String rValue = reversed.get(TypeUtils.parameterize(HashSet.class, String.class));

        // Then:
        assertThat(nValue, is(rValue));
    }

    @Test
    public void shouldGetSpecificOverPackage() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.<String>newBuilder("default")
            .withSpecificType(String.class, "specific")
            .withPackageType(String.class.getPackage(), "package")
            .build();

        // When:
        final String value = collection.get(String.class);

        // Then:
        assertThat(value, is("specific"));
    }

    @Test
    public void shouldGetSuperOverPackage() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.<String>newBuilder("default")
            .withSuperType(Set.class, "super")
            .withPackageType(Set.class.getPackage(), "package")
            .build();

        // When:
        final String value = collection.get(HashSet.class);

        // Then:
        assertThat(value, is("super"));
    }

    @Test
    public void shouldGetPackageOverArrayDefault() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.<String>newBuilder("default")
            .withPackageType(Long.class.getPackage(), "package")
            .withArrayDefault("array default")
            .build();

        // When:
        final String value = collection.get(TypeUtils.genericArrayType(Long.class));

        // Then:
        assertThat(value, is("package"));
    }

    @Test
    public void shouldHandlePrimitiveTypesWithPackageLevelValues() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.<String>newBuilder("default")
            .withPackageType(Long.class.getPackage(), "package")
            .build();

        // When:
        final String value = collection.get(TypeUtils.genericArrayType(long.class));

        // Then:
        assertThat(value, is("package"));
    }

    @Test
    public void shouldPickMostSpecificPackageValue() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.<String>newBuilder("default")
            .withPackageType(Collection.class.getPackage(), "no cigar")
            .withPackageType(ConcurrentHashMap.class.getPackage(), "most specific")
            .build();

        // When:
        final String value = collection.get(HashSet.class);

        // Then:
        assertThat(value, is("most specific"));
    }

    @Test
    public void shouldPickSamePackageValueRegardlessOfOrder() throws Exception {
        // Given:
        final Map<Package, String> naturalSuperValues = new TreeMap<Package, String>(new PackageComparator()) {{
            put(Collection.class.getPackage(), "no cigar");
            put(ConcurrentHashMap.class.getPackage(), "most specific");
        }};
        final Map<Package, String> reversedSuperValues = new TreeMap<Package, String>(new PackageComparator().reversed()) {{
            put(Collection.class.getPackage(), "no cigar");
            put(ConcurrentHashMap.class.getPackage(), "most specific");
        }};
        final ImmutableTypeMap<String> natural = new ImmutableTypeMap<String>(NO_SPECIFIC_VALUES, NO_SUPER_VALUES, naturalSuperValues, "default", "array default");
        final ImmutableTypeMap<String> reversed = new ImmutableTypeMap<String>(NO_SPECIFIC_VALUES, NO_SUPER_VALUES, reversedSuperValues, "default", "array default");

        // When:
        final String nValue = natural.get(TypeUtils.parameterize(HashSet.class, String.class));
        final String rValue = reversed.get(TypeUtils.parameterize(HashSet.class, String.class));

        // Then:
        assertThat(nValue, is(rValue));
    }

    @Test
    public void shouldGetPackageOverDefault() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.<String>newBuilder("default")
            .withPackageType(Set.class.getPackage(), "package")
            .build();

        // When:
        final String value = collection.get(Set.class);

        // Then:
        assertThat(value, is("package"));
    }

    @Test
    public void shouldBeAbleToOverrideDefault() throws Exception {
        // Given:
        final ImmutableTypeMap<String> collection = ImmutableTypeMap.<String>newBuilder("default")
            .withDefault("new default")
            .build();

        // When:
        final String value = collection.get(String.class);

        // Then:
        assertThat(value, is("new default"));
    }

    private static class ClassComparator implements Comparator<Class<?>> {
        @Override
        public int compare(final Class<?> c1, final Class<?> c2) {
            return c1.getCanonicalName().compareTo(c2.getCanonicalName());
        }
    }

    private static class PackageComparator implements Comparator<Package> {
        @Override
        public int compare(final Package c1, final Package c2) {
            return c1.getName().compareTo(c2.getName());
        }
    }
}