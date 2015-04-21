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

package org.datalorax.populace.core.populate.instance;

import org.datalorax.populace.core.util.TypeUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class InstanceFactoriesTest {

    private InstanceFactories.Builder builder;
    private InstanceFactories factories;

    @BeforeMethod
    public void setUp() throws Exception {
        builder = InstanceFactories.newBuilder();
        factories = builder.build();
    }

    @Test
    public void shouldHaveDefaultFactory() throws Exception {
        // When:
        final InstanceFactory factory = factories.getDefault();

        // Then:
        assertThat(factory, is(notNullValue()));
    }

    @Test
    public void shouldReturnDefaultFactoryForUnregisteredNonArrayType() throws Exception {
        // Given:
        final Type unregisteredType = getClass();

        // When:
        final InstanceFactory factory = factories.get(unregisteredType);

        // Then:
        assertThat(factory, is(factories.getDefault()));
    }

    @Test
    public void shouldBeAbleToOverrideTheDefault() throws Exception {
        // Given:
        final InstanceFactory newDefaultFactory = mock(InstanceFactory.class, "default");

        // When:
        final InstanceFactories factories = builder
            .withDefaultFactory(newDefaultFactory)
            .build();

        // Then:
        assertThat(factories.getDefault(), is(newDefaultFactory));
    }

    @Test
    public void shouldHaveDefaultArrayFactory() throws Exception {
        // Given:
        final Type arrayType = TypeUtils.genericArrayType(int.class);

        // When:
        final InstanceFactory factory = factories.get(arrayType);

        // Then:
        assertThat(factory, is(notNullValue()));
    }

    @Test
    public void shouldReturnDefaultArrayFactoryIfArrayTypeNotRegistered() throws Exception {
        // Given:
        final Type unregistered = TypeUtils.genericArrayType(int.class);

        // When:
        final InstanceFactory factory = factories.get(unregistered);

        // Then:
        assertThat(factory, is(factories.getArrayDefault()));
    }

    @Test
    public void shouldBeAbleToOverrideDefaultArrayFactory() throws Exception {
        // Given:
        final InstanceFactory newDefaultFactory = mock(InstanceFactory.class, "array default");

        // When:
        final InstanceFactories factories = builder
            .withArrayDefaultFactory(newDefaultFactory)
            .build();

        // Then:
        assertThat(factories.getArrayDefault(), is(newDefaultFactory));
    }

    @Test
    public void shouldReturnNotPresentFromGetSpecificIfNoSpecificRegistered() throws Exception {
        // Given:
        final Type unregistered = TypeUtils.parameterise(Collection.class, Integer.class);

        // When:
        final Optional<InstanceFactory> factory = factories.getSpecific(unregistered);

        // Then:
        assertThat(factory, is(Optional.<InstanceFactory>empty()));
    }

    @Test
    public void shouldReturnSpecificInstanceFactoryFromGetSpecificIfRegistered() throws Exception {
        // Given:
        final InstanceFactory expected = mock(InstanceFactory.class);
        final Type registered = TypeUtils.parameterise(Collection.class, Integer.class);
        factories = builder.withSpecificFactory(registered, expected).build();

        // When:
        final Optional<InstanceFactory> factory = factories.getSpecific(registered);

        // Then:
        assertThat(factory, is(Optional.of(expected)));
    }

    @Test
    public void shouldReturnNotPresentFromGetSuperIfNoSuperRegistered() throws Exception {
        // When:
        final Optional<InstanceFactory> factory = factories.getSuper(String.class);

        // Then:
        assertThat(factory, is(Optional.<InstanceFactory>empty()));
    }

    @Test
    public void shouldReturnSuperInstanceFactoryFromGetSuperIfRegistered() throws Exception {
        // Given:
        final InstanceFactory expected = mock(InstanceFactory.class);
        factories = builder.withSuperFactory(String.class, expected).build();

        // When:
        final Optional<InstanceFactory> factory = factories.getSuper(String.class);

        // Then:
        assertThat(factory, is(Optional.of(expected)));
    }

    @Test
    public void shouldSupportSpecificFactoryForBigDecimal() throws Exception {
        // When:
        final InstanceFactory factory = factories.get(BigDecimal.class);

        // Then:
        assertThat(factory, is(not(factories.getDefault())));
    }

    @Test
    public void shouldGetInstanceFactoryForNonClassOrParameterizedTypeIfSpecificRegistered() throws Exception {
        // Given:
        final Type wildcardType = TypeUtils.wildcardType();
        final InstanceFactory factory = mock(InstanceFactory.class, "specific");

        // When:
        final InstanceFactories factories = builder
            .withSpecificFactory(wildcardType, factory)
            .build();

        // Then:
        assertThat(factories.get(wildcardType), is(factory));
    }

    @Test
    public void shouldReturnNullObjectStrategyForObjectIfOneRegistered() throws Exception {
        // Given:
        final NullObjectStrategy strategy = mock(NullObjectStrategy.class);
        factories = builder.withNullObjectStrategy(strategy).build();

        // When:
        final InstanceFactory factory = factories.get(Object.class);

        // Then:
        assertThat(factory, is(new InstanceFactories.NullObjectInstanceFactory(strategy)));
    }

    @Test
    public void shouldInvokeNullObjectStrategyOnCreateInstancePassingParentObject() throws Exception {
        // Given:
        final Object parent = new Object();
        final NullObjectStrategy strategy = mock(NullObjectStrategy.class);
        final InstanceFactories.NullObjectInstanceFactory factory = new InstanceFactories.NullObjectInstanceFactory(strategy);

        // When:
        factory.createInstance(null, parent, null);

        // Then:
        verify(strategy).onNullObject(parent);
    }

    @Test
    public void shouldReturnNullFromNullObjectStrategyFactory() throws Exception {
        // Given:
        final Object parent = new Object();
        final NullObjectStrategy strategy = mock(NullObjectStrategy.class);
        final InstanceFactories.NullObjectInstanceFactory factory = new InstanceFactories.NullObjectInstanceFactory(strategy);

        // When:
        final Object instance = factory.createInstance(null, parent, null);

        // Then:
        assertThat(instance, is(nullValue()));
    }

    @Test
    public void shouldUseStrategyInEquals() throws Exception {
        // Given:
        final NullObjectStrategy strategy1 = mock(NullObjectStrategy.class, "1");
        final NullObjectStrategy strategy2 = mock(NullObjectStrategy.class, "2");
        final InstanceFactories.NullObjectInstanceFactory factory = new InstanceFactories.NullObjectInstanceFactory(strategy1);

        // Then:
        assertThat(factory.equals(new InstanceFactories.NullObjectInstanceFactory(strategy1)), is(true));
        assertThat(factory.equals(new InstanceFactories.NullObjectInstanceFactory(strategy2)), is(false));
    }

    @Test
    public void shouldUseStrategyHashCode() throws Exception {
        // Given:
        final NullObjectStrategy strategy = mock(NullObjectStrategy.class);
        final InstanceFactories.NullObjectInstanceFactory factory = new InstanceFactories.NullObjectInstanceFactory(strategy);

        // When:
        final int hashCode = factory.hashCode();

        // Then:
        assertThat(hashCode, is(strategy.hashCode()));
    }

    @Test
    public void shouldIncludeStrategyToStringInToString() throws Exception {
        // Given:
        final NullObjectStrategy strategy = mock(NullObjectStrategy.class);
        final InstanceFactories.NullObjectInstanceFactory factory = new InstanceFactories.NullObjectInstanceFactory(strategy);
        when(strategy.toString()).thenReturn("Fork Handles");

        // When:
        final String string = factory.toString();

        // Then:
        assertThat(string, containsString("Fork Handles"));
    }
}