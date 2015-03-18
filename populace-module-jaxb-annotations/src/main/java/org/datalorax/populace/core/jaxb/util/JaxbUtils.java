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

package org.datalorax.populace.core.jaxb.util;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.walk.inspector.InspectionException;

import javax.xml.bind.annotation.XmlElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author Andrew Coates - 13/03/2015.
 */
public final class JaxbUtils {

    /**
     * Classifies the accessor method.
     *
     * @param method the method to classify
     * @return the type of accessor the method represents
     * @throws java.lang.IllegalArgumentException if the method is not a valid accessor.
     */
    public static AccessorType classifyAccessor(final Method method) {
        final AccessorType type = classifyMethod(method);
        Validate.notNull(type, "Method is not an accessor: " + method);
        return type;
    }

    /**
     * Classifies the method to an {@link AccessorType}
     *
     * @param method the method to classify
     * @return the accessor type, or null if not an accessor
     */
    public static AccessorType classifyMethod(final Method method) {
        for (AccessorType type : AccessorType.values()) {
            if (type.matches(method)) {
                return type;
            }
        }

        return null;
    }

    /**
     * @param method the method to check
     * @return null if not a setter, the property name if it is
     */
    public static String getPropertyName(final Method method) {
        final AccessorType type = classifyAccessor(method);
        return type.getPropertyName(method);
    }

    /**
     * Validates the supplied {@code getter} / {@code setter} are a valid JaxB getter & setter pair.
     *
     * @param getter the getter method
     * @param setter the setter method
     * @throws InspectionException if they are not valid.
     */
    public static void validateGetterAndSetter(final Method getter, final Method setter) throws InspectionException {
        try {
            validateGetterName(getter);
            validateSetterName(setter);
            validateRelated(getter.getDeclaringClass(), setter.getDeclaringClass(), "getter and setter have unrelated types");
            validateProperty(getter, setter);
            validateXmlElementAnnotations(getter, setter);
        } catch (InspectionException e) {
            throw new InspectionException("Invalid getter & setter. getter:" + getter + ", setter:" + setter, e);
        }
    }

    public static void validateField(final Field field) {

        // Todo(ac):
    }

    private static void validateRelated(final Class<?> first, final Class<?> second, final String msg) throws InspectionException {
        if (!first.isAssignableFrom(second) && !second.isAssignableFrom(first)) {
            throw new InspectionException(msg + ", first: " + first + ", second: " + second);
        }
    }

    private static void validateProperty(final Method getter, final Method setter) {
        final Type getterType = getter.getGenericReturnType();
        final Type[] setterParams = setter.getGenericParameterTypes();
        if (setterParams.length != 1) {
            throw new InspectionException("setter has incorrect number of arguments. expected: 1, found: " + setterParams.length);
        }

        final Type setterType = setterParams[0];
        if (!getterType.equals(setterType)) {
            throw new InspectionException("Getter and setter have different property types. getter: " + getterType + ", setter: " + setterType);
        }
    }

    private static void validateGetterName(final Method method) {
        if (!AccessorType.is.matches(method) && !AccessorType.get.matches(method)) {
            throw new InspectionException("Invalid getter: " + method);
        }
    }

    private static void validateSetterName(final Method method) {
        if (!AccessorType.set.matches(method)) {
            throw new InspectionException("Invalid setter: " + method);
        }
    }

    private static void validateXmlElementAnnotations(final Method getter, final Method setter) {
        final XmlElement getterAnnotation = getter.getAnnotation(XmlElement.class);
        final XmlElement setterAnnotation = setter.getAnnotation(XmlElement.class);
        if (getterAnnotation == null && setterAnnotation == null) {
            throw new InspectionException("Neither getter nor setter have an XmlElement annotation");
        }

        if (getterAnnotation != null && setterAnnotation != null) {
            throw new InspectionException("Both getter and setter have XmlElement annotations");
        }
    }

    public enum AccessorType {
        is,
        get,
        set;

        public boolean matches(final Method method) {
            // Todo(ac): validate args / return type?
            return method.getName().startsWith(name()) && method.getName().length() > name().length();
        }

        public String getPropertyName(final Method method) {
            Validate.isTrue(method.getName().startsWith(name()));
            return method.getName().substring(name().length());
        }
    }
}
