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

import org.datalorax.populace.core.populate.instance.InstanceCreationException;
import org.datalorax.populace.core.populate.instance.InstanceFactories;
import org.datalorax.populace.core.populate.instance.InstanceFactory;
import org.datalorax.populace.core.util.TypeUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Instance factory to can be used to create new instances for interface types that are marked with
 * {@link javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter}.
 *
 * @author Andrew Coates - 09/03/2015.
 */
public class JaxbInstanceFactory implements InstanceFactory {
    public static final JaxbInstanceFactory INSTANCE = new JaxbInstanceFactory();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T createInstance(Class<? extends T> type, Object parent, final InstanceFactories instanceFactories) {
        final Optional<XmlJavaTypeAdapter> annotation = findXmlJavaTypeAnnotation(type);
        if (!annotation.isPresent()) {
            return null;
        }

        final Object value = createValueInstance(type, parent, instanceFactories, annotation.get());
        return (T) convert(type, annotation.get().value(), value, instanceFactories);
    }

    @Override
    public boolean equals(final Object that) {
        return this == that || (that != null && getClass() == that.getClass());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @SuppressWarnings("unchecked")
    private Object convert(final Class<?> type, final Class<? extends XmlAdapter> adapterType,
                           final Object value, final InstanceFactories instanceFactories) {

        final XmlAdapter adapter = createTypeAdapter(type, adapterType, instanceFactories);

        try {
            return adapter.unmarshal(value);
        } catch (Exception e) {
            final Type valueType = TypeUtils.getTypeArgument(adapterType, XmlAdapter.class.getTypeParameters()[0]);
            final Type boundType = TypeUtils.getTypeArgument(adapterType, XmlAdapter.class.getTypeParameters()[1]);
            throw new InstanceCreationException("Failed to marshal between XmlTypeAdapters value and bound types. " +
                "bound: " + boundType + ", value: " + valueType, type, e);
        }
    }

    private static <T> Optional<XmlJavaTypeAdapter> findXmlJavaTypeAnnotation(final Class<? extends T> type) {
        final XmlJavaTypeAdapter annotation = type.getAnnotation(XmlJavaTypeAdapter.class);
        return Optional.ofNullable(annotation);
    }

    private static Type getValueType(final Class<?> rawType, final XmlJavaTypeAdapter annotation) {
        try {
            final Class<? extends XmlAdapter> adapterType = annotation.value();
            final Method marshalMethod = adapterType.getMethod("marshal", rawType);
            return marshalMethod.getGenericReturnType();
        } catch (NoSuchMethodException e) {
            throw new InstanceCreationException("Failed to determine ValueType of the Adapter specified in the " +
                "@XmlJavaTypeAdapter annotation. Abstract adapter maybe?", rawType, e);
        }
    }

    private static <T> Object createValueInstance(final Class<? extends T> rawType, final Object parent,
                                                  final InstanceFactories instanceFactories, final XmlJavaTypeAdapter annotation) {
        final Type valueType = getValueType(rawType, annotation);
        final InstanceFactory factory = instanceFactories.get(valueType);
        final Class<?> rawValueType = TypeUtils.getRawType(valueType, null);
        return factory.createInstance(rawValueType, parent, instanceFactories);
    }

    private static XmlAdapter createTypeAdapter(final Class<?> type, final Class<? extends XmlAdapter> adapterType,
                                                final InstanceFactories instanceFactories) {
        try {
            return instanceFactories.get(adapterType).createInstance(adapterType, null, instanceFactories);
        } catch (Exception e) {
            throw new InstanceCreationException("Failed to create the type adapter needed to marshal the bound type. " +
                "adapterType: " + adapterType, type, e);
        }
    }

    // Todo(ac): @XmlTypeAdapter can also be present on field, or getter or setter, or in package-info.java.
}
