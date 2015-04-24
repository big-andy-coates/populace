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
import com.google.common.testing.NullPointerTester;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

public class BigDecimalInstanceFactoryTest {
    @Test
    public void shouldReturnDefaultProvided() throws Exception {
        // Given:
        final BigDecimal defaultValue = mock(BigDecimal.class);
        final InstanceFactory factory = new BigDecimalInstanceFactory(defaultValue);

        // When:
        final BigDecimal instance = factory.createInstance(BigDecimal.class, null, null);

        // Then:
        assertThat(instance, is(sameInstance(defaultValue)));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        final BigDecimal defaultValue = new BigDecimal(1.234);

        new EqualsTester()
            .addEqualityGroup(
                new BigDecimalInstanceFactory(defaultValue),
                new BigDecimalInstanceFactory(defaultValue),
                new BigDecimalInstanceFactory(new BigDecimal(1.234)))
            .addEqualityGroup(
                new BigDecimalInstanceFactory(new BigDecimal(1.233)))
            .testEquals();
    }

    @Test
    public void shouldThrowNPEsOnConstructorParams() throws Exception {
        new NullPointerTester()
            .testAllPublicConstructors(BigDecimalInstanceFactory.class);
    }
}