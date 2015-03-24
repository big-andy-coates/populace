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

package org.datalorax.populace.jaxb.walk.annotation;

import com.google.common.collect.ImmutableSet;
import org.datalorax.populace.core.walk.inspector.annotation.AnnotationInspector;

import javax.xml.bind.annotation.XmlElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * An {@link org.datalorax.populace.core.walk.inspector.annotation.AnnotationInspector} that will search for annotations not just
 * from the field, but also on any valid available getter or setter method. Only annotations under the
 * {@link javax.xml.bind.annotation} package are handled.
 *
 * @author Andrew Coates - 24/03/2015.
 */
public class JaxbAnnotationInspector implements AnnotationInspector {
    public static final JaxbAnnotationInspector INSTANCE = new JaxbAnnotationInspector();

    @Override
    public <T extends Annotation> T getAnnotation(final Field field, final Class<T> type) {
        if (!type.getCanonicalName().startsWith("javax.xml.bind.annotation")) {
            return null;
        }

        T annotation = getFromField(field, type);
        annotation = annotation != null ? annotation : getFromGetter(field, type);
        annotation = annotation != null ? annotation : getFromSetter(field, type);
        return annotation;
    }

    private <T extends Annotation> T getFromField(final Field field, final Class<T> type) {
        return field.getAnnotation(type);
    }

    private <T extends Annotation> T getFromGetter(final Field field, final Class<T> type) {
        if (field.getAnnotation(XmlElement.class) != null) {
            return null;
        }

        final Method method = findGetter(field);
        return method == null ? null : method.getAnnotation(type);
    }

    private <T extends Annotation> T getFromSetter(final Field field, final Class<T> type) {
        if (field.getAnnotation(XmlElement.class) != null) {
            return null;
        }

        final Method method = findSetter(field);
        return method == null ? null : method.getAnnotation(type);
    }

    private Method findGetter(final Field field) {
        final Set<String> validGetters = getValidGetterNames(field);
        for (Method method : field.getDeclaringClass().getDeclaredMethods()) {
            if (!validGetters.contains(method.getName())) {
                continue;
            }

            if (!method.getGenericReturnType().equals(field.getGenericType())) {
                return null;
            }

            return method;
        }
        return null;
    }

    private Method findSetter(final Field field) {
        final Set<String> validSetters = getValidSetterNames(field);
        for (Method method : field.getDeclaringClass().getDeclaredMethods()) {
            if (!validSetters.contains(method.getName())) {
                continue;
            }

            final Type[] genericParameterTypes = method.getGenericParameterTypes();
            if (genericParameterTypes.length != 1) {
                continue;
            }

            if (!genericParameterTypes[0].equals(field.getGenericType())) {
                return null;
            }

            return method;
        }
        return null;
    }

    private Set<String> getValidGetterNames(final Field field) {
        final String postFix = getMethodNamePostFix(field);
        if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
            return ImmutableSet.of("is" + postFix, "get" + postFix);
        }

        return ImmutableSet.of("get" + postFix);
    }

    private Set<String> getValidSetterNames(final Field field) {
        final String postFix = getMethodNamePostFix(field);
        return ImmutableSet.of("set" + postFix);
    }

    private String getMethodNamePostFix(final Field field) {
        final String name = field.getName();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
