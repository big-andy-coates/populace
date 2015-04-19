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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

public class InstanceFactoriesBuilderTest {

    private InstanceFactories.Builder builder;

    @BeforeMethod
    public void setUp() throws Exception {
        builder = InstanceFactories.newBuilder();
    }

    @Test
    public void shouldHaveDefaultFactory() throws Exception {
        // Given:
        final Type unregisteredType = getClass();

        // When:
        final InstanceFactories factories = builder.build();

        // Then:
        assertThat(factories.get(unregisteredType), is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToOverrideTheDefault() throws Exception {
        // Given:
        final Type unregisteredType = getClass();
        final InstanceFactory newDefaultFactory = mock(InstanceFactory.class, "default");

        // When:
        final InstanceFactories factories = builder
            .withDefaultFactory(newDefaultFactory)
            .build();

        // Then:
        assertThat(factories.get(unregisteredType), is(newDefaultFactory));
    }

    @Test
    public void shouldHaveDefaultArrayFactory() throws Exception {
        // Given:
        final Type arrayType = TypeUtils.genericArrayType(int.class);

        // When:
        final InstanceFactories factories = builder.build();

        // Then:
        assertThat(factories.get(arrayType), is(notNullValue()));
    }

    @Test
    public void shouldBeAbleToOverrideDefaultArrayFactory() throws Exception {
        // Given:
        final Type arrayType = TypeUtils.genericArrayType(int.class);
        final InstanceFactory newDefaultFactory = mock(InstanceFactory.class, "array default");

        // When:
        final InstanceFactories factories = builder
            .withArrayDefaultFactory(newDefaultFactory)
            .build();

        // Then:
        assertThat(factories.get(arrayType), is(newDefaultFactory));
    }

    @Test
    public void shouldSupportSpecificFactoryForBigDecimal() throws Exception {
        // Given:
        final InstanceFactory newDefaultFactory = mock(InstanceFactory.class, "default");

        // When:
        final InstanceFactories factories = builder
            .withDefaultFactory(newDefaultFactory)
            .build();

        // Then:
        assertThat(factories.get(BigDecimal.class), is(not(newDefaultFactory)));
    }

    // Todo(ac):
}