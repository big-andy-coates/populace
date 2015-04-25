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

package org.datalorax.populace.jaxb.instance;

import com.google.common.testing.EqualsTester;
import org.datalorax.populace.core.populate.instance.InstanceCreationException;
import org.datalorax.populace.core.populate.instance.InstanceFactories;
import org.datalorax.populace.core.populate.instance.InstanceFactory;
import org.datalorax.populace.core.util.TypeUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class JaxbInstanceFactoryTest {
    private JaxbInstanceFactory factory;
    private InstanceFactory valueTypeFactory;
    private InstanceFactories instanceFactories;

    @BeforeMethod
    public void setUp() throws Exception {
        instanceFactories = mock(InstanceFactories.class);
        valueTypeFactory = mock(InstanceFactory.class);

        factory = JaxbInstanceFactory.INSTANCE;
    }

    @Test
    public void shouldReturnNullIfNoAnnotationPresent() throws Exception {
        // Given:
        class SimpleType {
        }

        // When:
        final SimpleType instance = factory.createInstance(SimpleType.class, this, instanceFactories);

        // Then:
        assertThat(instance, is(nullValue()));
    }

    @Test
    public void shouldReturnInstanceCreatedByValueTypeInstanceFactory() throws Exception {
        // Given:
        final InterfaceWithTypeAdapterImpl expected = new InterfaceWithTypeAdapterImpl();
        givenValueTypeInstanceFactoryWillReturn(InterfaceWithTypeAdapterImpl.class, expected);
        givenValueTypeInstanceFactoryWillReturn(TypeAdapter.class, new TypeAdapter());

        // When:
        final InterfaceWithTypeAdapter instance = factory.createInstance(InterfaceWithTypeAdapter.class, this, instanceFactories);


        // Then:
        assertThat(instance, is(sameInstance(expected)));
    }

    @Test
    public void shouldWorkIfMarshalMethodsAreOnBaseTypeOfAdapter() throws Exception {
        // Given:
        final InterfaceWithDerivedTypeAdapterImpl expected = new InterfaceWithDerivedTypeAdapterImpl();
        givenValueTypeInstanceFactoryWillReturn(InterfaceWithDerivedTypeAdapterImpl.class, expected);
        givenValueTypeInstanceFactoryWillReturn(DerivedTypeAdapter.class, new DerivedTypeAdapter());

        // When:
        final InterfaceWithDerivedTypeAdapter instance = factory.createInstance(InterfaceWithDerivedTypeAdapter.class, this, instanceFactories);

        // Then:
        assertThat(instance, is(sameInstance(expected)));
    }

    @Test(expectedExceptions = InstanceCreationException.class)
    public void shouldThrowIfAdapterDoesImplementMarshalMethods() throws Exception {
        // Given:
        abstract class AbstractTypeAdapter extends XmlAdapter {
        }

        @XmlJavaTypeAdapter(value = AbstractTypeAdapter.class)
        class TypeWithAbstractTypeAdapter {
        }

        // When:
        final TypeWithAbstractTypeAdapter instance = factory.createInstance(TypeWithAbstractTypeAdapter.class, this, instanceFactories);


        // Then:
        assertThat(instance, instanceOf(InterfaceWithTypeAdapterImpl.class));
    }

    @Test(expectedExceptions = InstanceCreationException.class)
    public void shouldThrowIfUnableToCreateInstanceOfAdapter() throws Exception {
        // Given:
        final InterfaceWithTypeAdapterImpl expected = new InterfaceWithTypeAdapterImpl();
        givenValueTypeInstanceFactoryWillReturn(InterfaceWithTypeAdapterImpl.class, expected);
        when(valueTypeFactory.createInstance(eq(TypeAdapter.class), anyObject(), any(InstanceFactories.class))).thenThrow(new RuntimeException("boom!"));

        // When:
        factory.createInstance(InterfaceWithTypeAdapter.class, this, instanceFactories);
    }

    @Test(expectedExceptions = InstanceCreationException.class)
    public void shouldThrowIfMarshalCallThrows() throws Exception {
        // Given:
        final TypeAdapter adapter = mock(TypeAdapter.class);
        givenValueTypeInstanceFactoryWillReturn(InterfaceWithTypeAdapterImpl.class, new InterfaceWithTypeAdapterImpl());
        givenValueTypeInstanceFactoryWillReturn(TypeAdapter.class, adapter);
        when(adapter.unmarshal(any(InterfaceWithTypeAdapterImpl.class))).thenThrow(new RuntimeException("Boom!"));

        // When:
        factory.createInstance(InterfaceWithTypeAdapter.class, this, instanceFactories);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldUseParameterisedTypeToGetInstanceFactoryForValueType() throws Exception {
        // Given:
        final ParameterizedType valueType = TypeUtils.parameterise(InterfaceWithGenericTypeAdapterImpl.class, String.class);
        when(valueTypeFactory.createInstance(any(Class.class), anyObject(), any(InstanceFactories.class))).thenReturn(new InterfaceWithGenericTypeAdapterImpl());
        givenValueTypeInstanceFactoryWillReturn(GenericTypeAdapter.class, new GenericTypeAdapter());

        // When:
        factory.createInstance(InterfaceWithGenericTypeAdapter.class, this, instanceFactories);

        // Then:
        verify(instanceFactories).get(valueType);
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                JaxbInstanceFactory.INSTANCE,
                new JaxbInstanceFactory())
            .addEqualityGroup(
                mock(InstanceFactory.class))
            .testEquals();
    }

    @SuppressWarnings("unchecked")
    private <T> void givenValueTypeInstanceFactoryWillReturn(final Class type, final T expected) {
        when(instanceFactories.get(any(Type.class))).thenReturn(valueTypeFactory);
        when(valueTypeFactory.createInstance(eq(type), anyObject(), any(InstanceFactories.class))).thenReturn(expected);
    }

    ////////////////////////////////

    @XmlJavaTypeAdapter(value = TypeAdapter.class)
    interface InterfaceWithTypeAdapter {
    }

    @XmlJavaTypeAdapter(value = DerivedTypeAdapter.class)
    interface InterfaceWithDerivedTypeAdapter {
    }

    @XmlJavaTypeAdapter(value = GenericTypeAdapter.class)
    interface InterfaceWithGenericTypeAdapter {
    }

    ////////////////////////////////

    public static class TypeAdapter extends XmlAdapter<InterfaceWithTypeAdapterImpl, InterfaceWithTypeAdapter> {

        @Override
        public InterfaceWithTypeAdapter unmarshal(final InterfaceWithTypeAdapterImpl v) throws Exception {
            return v;
        }

        @Override
        public InterfaceWithTypeAdapterImpl marshal(final InterfaceWithTypeAdapter v) throws Exception {
            return null;
        }

    }

    public static class BaseTypeAdapter extends XmlAdapter<InterfaceWithDerivedTypeAdapterImpl, InterfaceWithDerivedTypeAdapter> {

        @Override
        public InterfaceWithDerivedTypeAdapter unmarshal(final InterfaceWithDerivedTypeAdapterImpl v) throws Exception {
            return v;
        }

        @Override
        public InterfaceWithDerivedTypeAdapterImpl marshal(final InterfaceWithDerivedTypeAdapter v) throws Exception {
            return null;
        }

    }

    public static class DerivedTypeAdapter extends BaseTypeAdapter {
    }

    public static class GenericTypeAdapter extends XmlAdapter<InterfaceWithGenericTypeAdapterImpl<String>, InterfaceWithGenericTypeAdapter> {

        @Override
        public InterfaceWithGenericTypeAdapter unmarshal(final InterfaceWithGenericTypeAdapterImpl v) throws Exception {
            return v;
        }

        @Override
        public InterfaceWithGenericTypeAdapterImpl<String> marshal(final InterfaceWithGenericTypeAdapter v) throws Exception {
            return null;
        }
    }

    ////////////////////////////////

    class InterfaceWithTypeAdapterImpl implements InterfaceWithTypeAdapter {
    }

    class InterfaceWithDerivedTypeAdapterImpl implements InterfaceWithDerivedTypeAdapter {
    }

    @SuppressWarnings("UnusedDeclaration")
    class InterfaceWithGenericTypeAdapterImpl<T> implements InterfaceWithGenericTypeAdapter {
    }
}