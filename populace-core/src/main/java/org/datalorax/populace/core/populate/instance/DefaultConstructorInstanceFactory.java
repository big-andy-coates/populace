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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.populate.PopulatorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * Instance factory that uses a classes default constructor to create a new instance of the type, or throws if it can't
 *
 * @author Andrew Coates - 02/03/2015.
 */
public class DefaultConstructorInstanceFactory implements InstanceFactory {
    public static final InstanceFactory INSTANCE = new DefaultConstructorInstanceFactory();

    private static boolean isInnerClass(final Class<?> rawType) {
        return rawType.getEnclosingClass() != null && !Modifier.isStatic(rawType.getModifiers());
    }

    @Override
    public <T> T createInstance(Class<? extends T> rawType, final Object parent, final InstanceFactories instanceFactories) {
        try {
            if (isInnerClass(rawType)) {
                return createNewInnerClass(rawType, parent);
            } else {
                return createNewTopLevel(rawType);
            }
        } catch (ReflectiveOperationException e) {
            throw new PopulatorException("Failed to create new instance of type via default constructor. Type: " + rawType, e);
        }
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

    private <T> T createNewTopLevel(final Class<? extends T> rawType) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        final Constructor<?> defaultConstructor = getConstructor(rawType);

        //noinspection unchecked
        return (T) defaultConstructor.newInstance();
    }

    private <T> T createNewInnerClass(final Class<? extends T> rawType, final Object parent) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Validate.notNull(parent, "Parent of inner class was null");
        final Constructor<?> defaultConstructor = getConstructor(rawType, parent.getClass());

        //noinspection unchecked
        return (T) defaultConstructor.newInstance(parent);
    }

    private <T> Constructor<? extends T> getConstructor(final Class<? extends T> rawType, Class<?>... parameterTypes) {
        try {
            final Constructor<? extends T> defaultConstructor = rawType.getDeclaredConstructor(parameterTypes);
            defaultConstructor.setAccessible(true);
            return defaultConstructor;
        } catch (NoSuchMethodException e) {
            throw new PopulatorException("Failed to instantiate type as no viable constructor could be found for type: " + rawType +
                ", parameters: " + StringUtils.join(parameterTypes, ',') +
                ", availableConstructors: " + StringUtils.join(rawType.getDeclaredConstructors(), ',') +
                ", either add a default constructor, or consider adding a custom InstanceFactory to handle this type", e);
        }
    }
}
