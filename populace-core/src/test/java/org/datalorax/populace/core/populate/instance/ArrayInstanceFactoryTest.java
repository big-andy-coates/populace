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

import com.google.common.testing.EqualsTester;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

public class ArrayInstanceFactoryTest {
    private ArrayInstanceFactory factory;
    private InstanceFactories factories;

    @BeforeMethod
    public void setUp() throws Exception {
        factories = mock(InstanceFactories.class);

        factory = ArrayInstanceFactory.INSTANCE;
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnUnsupportedType() throws Exception {
        factory.createInstance(String.class, null, factories);
    }

    @Test
    public void shouldCreateArrayWithOneElement() throws Exception {
        // Given:
        final double[] array = new double[]{};

        // When:
        final double[] instance = factory.createInstance(array.getClass(), null, factories);

        // Then:
        assertThat(instance, is(notNullValue()));
        assertThat(instance.length, is(1));
        assertThat(instance[0], is(0.0));
    }

    @Test
    public void shouldHandleMultiDimensionalArrays() throws Exception {
        // Given:
        final double[][] array = new double[][]{};

        // When:
        final double[][] instance = factory.createInstance(array.getClass(), null, factories);

        // Then:
        assertThat(instance, is(notNullValue()));
        assertThat(instance.length, is(1));
        assertThat(instance[0], is(nullValue()));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                ArrayInstanceFactory.INSTANCE,
                new ArrayInstanceFactory())
            .addEqualityGroup(
                mock(InstanceFactory.class))
            .testEquals();
    }
}