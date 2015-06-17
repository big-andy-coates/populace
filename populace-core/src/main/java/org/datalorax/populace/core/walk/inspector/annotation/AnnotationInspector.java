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

package org.datalorax.populace.core.walk.inspector.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A 'pluggable' annotation inspector that allows the logic used to find an annotation, given a field or method, to be
 * customised.
 *
 * @author Andrew Coates - 24/03/2015.
 */
public interface AnnotationInspector {
    /**
     * Search for the requested annotation {@code type} on the supplied {@code field}
     *
     * @param field the field to look for the annotation on
     * @param type  the type of the annotation to find
     * @param <T>   the type of the annotation to find
     * @return the annotation, if found, else null
     */
    <T extends Annotation> T getAnnotation(Field field, Class<T> type);

    // Todo(v2.x): Switch to Optional<T> return value in v2.x

    /**
     * Search for the requested annotation {@code type} on the supplied {@code accessorMethods}
     *
     * @param accessorMethods the methods to look for the annotation on. There will either be 1 or 2 methods.
     * @param type            the type of the annotation to find
     * @param <T>             the type of the annotation to find
     * @return the annotation, if found, else null
     */
    default <T extends Annotation> T getAnnotation(Class<T> type, Method... accessorMethods) {
        // Todo(v2.x): remove default in v2.x - its just here to maintain version compatibility
        return null;
    }
}
