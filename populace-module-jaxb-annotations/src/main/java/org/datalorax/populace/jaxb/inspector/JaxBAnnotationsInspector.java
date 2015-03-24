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

package org.datalorax.populace.jaxb.inspector;

import org.datalorax.populace.core.walk.field.RawField;
import org.datalorax.populace.core.walk.inspector.Inspector;
import org.datalorax.populace.core.walk.inspector.Inspectors;
import org.datalorax.populace.jaxb.field.JaxbFieldElement;
import org.datalorax.populace.jaxb.field.JaxbPropertyElement;
import org.datalorax.populace.jaxb.util.JaxbUtils;

import javax.xml.bind.annotation.XmlElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An inspector replacement for the default {@link org.datalorax.populace.core.walk.inspector.FieldInspector} that exposes
 * bean properties, rather than raw fields, and which understands and uses JaxB annotations to include/exclude properties.
 *
 * @author Andrew Coates - 09/03/2015.
 */
public class JaxBAnnotationsInspector implements Inspector {
    public static final JaxBAnnotationsInspector INSTANCE = new JaxBAnnotationsInspector();

    @Override
    public Iterable<RawField> getFields(final Class<?> type, final Inspectors inspectors) {
        final Map<String, RawField> found = new HashMap<>();

        handleFields(type, found);
        handleMethods(type, found);

        return found.values();
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

    private void handleFields(final Class<?> type, final Map<String, RawField> found) {
        for (Field field : type.getDeclaredFields()) {
            final XmlElement annotation = field.getAnnotation(XmlElement.class);
            if (annotation == null) {
                continue;
            }

            final JaxbFieldElement element = new JaxbFieldElement(field);
            if (found.put(element.getName(), element) != null) {
                throw new DuplicateJaxbElementException(field.getName(), field.getDeclaringClass(), found.get(element.getName()));
            }
        }

        final Class<?> superClass = type.getSuperclass();
        if (!Object.class.equals(superClass)) {
            handleFields(superClass, found);
        }
    }

    private void handleMethods(final Class<?> type, final Map<String, RawField> found) {
        final Map<String, Method> getters = new HashMap<>();
        final Map<String, Method> setters = new HashMap<>();

        for (Method method : type.getDeclaredMethods()) {
            final JaxbUtils.AccessorType accessorType = JaxbUtils.classifyAccessor(method);
            switch (accessorType) {
                case is:
                case get:
                    getters.put(accessorType.getPropertyName(method), method);
                    break;
                case set:
                    setters.put(accessorType.getPropertyName(method), method);
                    break;
            }
        }

        final Set<String> names = new HashSet<>();
        names.addAll(getters.keySet());
        names.addAll(setters.keySet());

        for (String name : names) {
            final Method getter = getters.get(name);
            final Method setter = setters.get(name);
            if (getter == null || setter == null) {
                continue;
            }

            final XmlElement getterAnnotation = getter.getAnnotation(XmlElement.class);
            final XmlElement setterAnnotation = setter.getAnnotation(XmlElement.class);
            if (getterAnnotation == null && setterAnnotation == null) {
                continue;
            }

            final JaxbPropertyElement element = new JaxbPropertyElement(getter, setter);
            if (found.put(element.getName(), element) != null) {
                throw new DuplicateJaxbElementException(getter.getName(), getter.getDeclaringClass(), found.get(element.getName()));
            }
        }

        final Class<?> superClass = type.getSuperclass();
        if (!Object.class.equals(superClass)) {
            handleMethods(superClass, found);
        }

        // Todo(ac): refactor
    }
}
