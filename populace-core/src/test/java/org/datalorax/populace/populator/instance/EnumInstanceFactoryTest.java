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

package org.datalorax.populace.populator.instance;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class EnumInstanceFactoryTest {
    private InstanceFactory factory;

    @BeforeMethod
    public void setUp() throws Exception {
        factory = EnumInstanceFactory.INSTANCE;
    }

    @Test
    public void shouldReturnNullOnUnsupportedType() throws Exception {
        // When:
        final String instance = factory.createInstance(String.class, null, null);

        // Then:
        assertThat(instance, is(nullValue()));
    }

    @Test
    public void shouldReturnFirstEnumForEnumTypes() throws Exception {
        // When:
        final NonEmptyEnum instance = factory.createInstance(NonEmptyEnum.class, null, null);

        // Then:
        assertThat(instance, is(NonEmptyEnum.first));
    }

    @Test
    public void shouldReturnNullForZeroLengthEnums() throws Exception {
        // When:
        final EmptyEnum instance = factory.createInstance(EmptyEnum.class, null, null);

        // Then:
        assertThat(instance, is(nullValue()));
    }

    private static enum NonEmptyEnum {
        first, second, third
    }

    private static enum EmptyEnum {}
}