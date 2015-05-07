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

package org.datalorax.populace.core.walk.inspector;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;
import org.datalorax.populace.core.util.ImmutableTypeMap;
import org.datalorax.populace.core.util.TypeUtils;
import org.datalorax.populace.core.walk.inspector.annotation.AnnotationInspector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

public class InspectorsTest {
    private Inspectors.Builder builder;
    private Inspectors inspectors;

    @BeforeMethod
    public void setUp() throws Exception {
        builder = Inspectors.newBuilder();
        inspectors = builder.build();
    }

    @Test
    public void shouldHaveDefaultInspector() throws Exception {
        // When:
        final Inspector factory = inspectors.getDefault();

        // Then:
        assertThat(factory, is(notNullValue()));
    }

    @Test
    public void shouldReturnDefaultInspectorForUnregisteredNonArrayType() throws Exception {
        // Given:
        final Type unregisteredType = getClass();

        // When:
        final Inspector factory = inspectors.get(unregisteredType);

        // Then:
        assertThat(factory, is(inspectors.getDefault()));
    }

    @Test
    public void shouldBeAbleToOverrideTheDefault() throws Exception {
        // Given:
        final Inspector newDefaultInspector = mock(Inspector.class, "default");

        // When:
        final Inspectors inspectors = builder
            .withDefaultInspector(newDefaultInspector)
            .build();

        // Then:
        assertThat(inspectors.getDefault(), is(newDefaultInspector));
    }

    @Test
    public void shouldHaveDefaultArrayInspector() throws Exception {
        // Given:
        final Type arrayType = TypeUtils.genericArrayType(int.class);

        // When:
        final Inspector factory = inspectors.get(arrayType);

        // Then:
        assertThat(factory, is(notNullValue()));
    }

    @Test
    public void shouldReturnDefaultArrayInspectorIfArrayTypeNotRegistered() throws Exception {
        // Given:
        final Type unregistered = TypeUtils.genericArrayType(int.class);

        // When:
        final Inspector factory = inspectors.get(unregistered);

        // Then:
        assertThat(factory, is(inspectors.getArrayDefault()));
    }

    @Test
    public void shouldBeAbleToOverrideDefaultArrayInspector() throws Exception {
        // Given:
        final Inspector newDefaultInspector = mock(Inspector.class, "array default");

        // When:
        final Inspectors inspectors = builder
            .withArrayDefaultInspector(newDefaultInspector)
            .build();

        // Then:
        assertThat(inspectors.getArrayDefault(), is(newDefaultInspector));
    }

    @Test
    public void shouldReturnNotPresentFromGetSpecificIfNoSpecificRegistered() throws Exception {
        // Given:
        final Type unregistered = TypeUtils.parameterise(Collection.class, Integer.class);

        // When:
        final Optional<Inspector> factory = inspectors.getSpecific(unregistered);

        // Then:
        assertThat(factory, is(Optional.<Inspector>empty()));
    }

    @Test
    public void shouldReturnSpecificInspectorFromGetSpecificIfRegistered() throws Exception {
        // Given:
        final Inspector expected = mock(Inspector.class);
        final Type registered = TypeUtils.parameterise(Collection.class, Integer.class);
        inspectors = builder.withSpecificInspector(registered, expected).build();

        // When:
        final Optional<Inspector> factory = inspectors.getSpecific(registered);

        // Then:
        assertThat(factory, is(Optional.of(expected)));
    }

    @Test
    public void shouldReturnNotPresentFromGetSuperIfNoSuperRegistered() throws Exception {
        // When:
        final Optional<Inspector> factory = inspectors.getSuper(String.class);

        // Then:
        assertThat(factory, is(Optional.<Inspector>empty()));
    }

    @Test
    public void shouldReturnSuperInspectorFromGetSuperIfRegistered() throws Exception {
        // Given:
        final Inspector expected = mock(Inspector.class);
        inspectors = builder.withSuperInspector(String.class, expected).build();

        // When:
        final Optional<Inspector> factory = inspectors.getSuper(String.class);

        // Then:
        assertThat(factory, is(Optional.of(expected)));
    }

    @Test
    public void shouldReturnNotPresentFromGetPackageIfNoPackageRegistered() throws Exception {
        // When:
        final Optional<Inspector> factory = inspectors.getPackage("a.package");

        // Then:
        assertThat(factory, is(Optional.<Inspector>empty()));
    }

    @Test
    public void shouldReturnPackageInspectorFromGetPackageIfRegistered() throws Exception {
        // Given:
        final Inspector expected = mock(Inspector.class);
        inspectors = builder.withPackageInspector("some.package", expected).build();

        // When:
        final Optional<Inspector> factory = inspectors.getPackage("some.package");

        // Then:
        assertThat(factory, is(Optional.of(expected)));
    }

    @Test
    public void shouldSupportSpecificInspectorForBigDecimal() throws Exception {
        // When:
        final Inspector factory = inspectors.get(BigDecimal.class);

        // Then:
        assertThat(factory, is(not(inspectors.getDefault())));
    }

    @Test
    public void shouldGetInspectorForNonClassOrParameterizedTypeIfSpecificRegistered() throws Exception {
        // Given:
        final Type wildcardType = TypeUtils.wildcardType();
        final Inspector factory = mock(Inspector.class, "specific");

        // When:
        final Inspectors inspectors = builder
            .withSpecificInspector(wildcardType, factory)
            .build();

        // Then:
        assertThat(inspectors.get(wildcardType), is(factory));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        final ImmutableTypeMap<Inspector> rawInspectors = mock(ImmutableTypeMap.class, "1");
        final AnnotationInspector annotationInspector = mock(AnnotationInspector.class, "1");

        new EqualsTester()
            .addEqualityGroup(
                new Inspectors(rawInspectors, annotationInspector),
                new Inspectors(rawInspectors, annotationInspector))
            .addEqualityGroup(
                new Inspectors(mock(ImmutableTypeMap.class, "2"), annotationInspector))
            .addEqualityGroup(
                new Inspectors(rawInspectors, mock(AnnotationInspector.class, "2")))
            .testEquals();
    }

    @Test
    public void shouldThrowNPEsOnConstructorParams() throws Exception {
        new NullPointerTester()
            .setDefault(ImmutableTypeMap.class, mock(ImmutableTypeMap.class))
            .setDefault(AnnotationInspector.class, mock(AnnotationInspector.class))
            .testAllPublicConstructors(Inspectors.class);
    }
}