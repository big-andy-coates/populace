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

package org.datalorax.populace.instance;

import org.datalorax.populace.populator.instance.ChainableInstanceFactory;
import org.datalorax.populace.populator.instance.InstanceCreationException;
import org.datalorax.populace.populator.instance.InstanceFactories;
import org.datalorax.populace.populator.instance.InstanceFactory;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.reflect.Method;

/**
 * Instance factory to can be used to create new instances for interface types that are marked with
 * {@link javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter}.
 *
 * @author Andrew Coates - 09/03/2015.
 */
public class JaxBInstanceFactory implements ChainableInstanceFactory {
    public static final JaxBInstanceFactory INSTANCE = new JaxBInstanceFactory();

    private static <T> Class<? extends T> getValueType(final Class<? extends T> rawType, final XmlJavaTypeAdapter annotation) {
        try {
            final Class<? extends XmlAdapter> adapterType = annotation.value();
            final Method marshalMethod = adapterType.getMethod("marshal", rawType);
            //noinspection unchecked
            return (Class<? extends T>) marshalMethod.getReturnType();
        } catch (NoSuchMethodException e) {
            throw new InstanceCreationException("Failed to determine value to for type marked with @XmlJavaTypeAdapter", rawType, e);
        }
    }

    @Override
    public boolean supportsType(Class<?> rawType) {
        return rawType.getAnnotation(XmlJavaTypeAdapter.class) != null;
    }

    @Override
    public <T> T createInstance(Class<? extends T> rawType, Object parent, final InstanceFactories instanceFactories) {
        final XmlJavaTypeAdapter annotation = rawType.getAnnotation(XmlJavaTypeAdapter.class);
        if (annotation == null) {
            return null;
        }

        final Class<? extends T> returnType = getValueType(rawType, annotation);

        final InstanceFactory factory = instanceFactories.get(returnType);
        return factory.createInstance(returnType, parent, instanceFactories);
    }
}