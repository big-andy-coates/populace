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

import org.datalorax.populace.populator.PopulatorException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DefaultConstructorInstanceFactoryTest {
    private InstanceFactory factory;

    @BeforeMethod
    public void setUp() throws Exception {
        factory = DefaultConstructorInstanceFactory.INSTANCE;
    }

    @Test
    public void shouldUseDefaultConstructorToCreateInstance() throws Exception {
        // When:
        final PublicTypeWithPublicConstructor instance = factory.createInstance(PublicTypeWithPublicConstructor.class, null);

        // Then:
        assertThat(instance, is(notNullValue()));
    }

    @Test
    public void shouldWorkWithPrivateConstructor() throws Exception {
        // When:
        final PublicTypeWithPrivateConstructor instance = factory.createInstance(PublicTypeWithPrivateConstructor.class, null);

        // Then:
        assertThat(instance, is(notNullValue()));
    }

    @Test
    public void shouldWorkWithPrivateClass() throws Exception {
        // When:
        final PrivateType instance = factory.createInstance(PrivateType.class, null);

        // Then:
        assertThat(instance, is(notNullValue()));
    }

    @Test
    public void shouldWorkWithInnerClasses() throws Exception {
        // Given:
        final TypeWithInner parent = new TypeWithInner();

        // When:
        final TypeWithInner.Inner instance = factory.createInstance(TypeWithInner.Inner.class, parent);

        // Then:
        assertThat(instance, is(notNullValue()));
    }

    @Test(expectedExceptions = PopulatorException.class)
    public void shouldThrowIfNoDefaultConstructor() throws Exception {
        // When:
        factory.createInstance(TypeWithNoDefaultConstructor.class, null);
    }

    @Test(expectedExceptions = PopulatorException.class)
    public void shouldThrowIfInterface() throws Exception {
        // When:
        factory.createInstance(InterfaceType.class, null);
    }

    @Test(expectedExceptions = PopulatorException.class)
    public void shouldThrowIfAbstract() throws Exception {
        // When:
        factory.createInstance(AbstractType.class, null);
    }

    public interface InterfaceType {
    }

    public static final class PublicTypeWithPublicConstructor {
    }

    public static final class PublicTypeWithPrivateConstructor {
        private PublicTypeWithPrivateConstructor() {
        }
    }

    private static final class PrivateType {
    }

    public static final class TypeWithInner {
        public class Inner {
        }
    }

    public static final class TypeWithNoDefaultConstructor {
        @SuppressWarnings("UnusedParameters")
        public TypeWithNoDefaultConstructor(String s) {
        }
    }

    public static abstract class AbstractType {
    }
}