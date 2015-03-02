package org.datalorax.populace.typed;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class TypeMapBuilderTest {
    private TypedMapBuilder<String> builder;

    @BeforeMethod
    public void setUp() throws Exception {
        builder = new TypedMapBuilder<String>();
    }

    @Test
    public void shouldInstallSpecificType() throws Exception {
        // Given:
        final String specific = "int";

        // When:
        builder.withSpecificType(int.class, specific);

        // Then:
        final TypeMap<String> collection = builder.build();
        assertThat(collection.get(int.class), is(specific));
    }

    @Test
    public void shouldOverrideExistingSpecificType() throws Exception {
        // Given:
        final String specific = "int";
        final String override = "int override";
        builder.withSpecificType(String.class, specific);

        // When:
        builder.withSpecificType(int.class, override);

        // Then:
        final TypeMap<String> collection = builder.build();
        assertThat(collection.get(int.class), is(override));
    }

    @Test
    public void shouldInstallSuperType() throws Exception {
        // Given:
        final String superType = "super type";

        // When:
        builder.withSuperType(Map.class, superType);

        // Then:
        final TypeMap<String> collection = builder.build();
        assertThat(collection.get(HashMap.class), is(superType));
    }

    @Test
    public void shouldOverrideExistingSuperType() throws Exception {
        // Given:
        final String superType = "super type";
        final String override = "super type override";
        builder.withSuperType(Set.class, superType);

        // When:
        builder.withSuperType(Set.class, override);

        // Then:
        final TypeMap<String> collection = builder.build();
        assertThat(collection.get(HashSet.class), is(override));
    }

    @Test
    public void shouldInstallDefault() throws Exception {
        // Given:
        final Type unregisteredType = TypedMapBuilder.class;
        final String defaultV = "default";

        // When:
        builder.withDefault(defaultV);

        // Then:
        final TypeMap<String> collection = builder.build();
        assertThat(collection.get(unregisteredType), is(defaultV));
    }

    @Test
    public void shouldReturnNullIfNoMatchFoundAndNoDefault() throws Exception {
        // Given:
        final Type unregisteredType = TypedMapBuilder.class;

        // Then:
        final TypeMap<String> collection = builder.build();
        assertThat(collection.get(unregisteredType), is(nullValue()));
    }

    @Test
    public void shouldInstallDefaultArray() throws Exception {
        // Given:
        final Type arrayType = TypeUtils.genericArrayType(int.class);
        final String defaultA = "default array";

        // When:
        builder.withDefaultArray(defaultA);

        // Then:
        final TypeMap<String> collection = builder.build();
        assertThat(collection.get(arrayType), is(defaultA));
    }

    @Test
    public void shouldReturnNullIfNoMatchFoundAndNoDefaultArray() throws Exception {
        // Given:
        final Type arrayType = TypeUtils.genericArrayType(int.class);

        // Then:
        final TypeMap<String> collection = builder.build();
        assertThat(collection.get(arrayType), is(nullValue()));
    }
}