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
import java.util.Arrays;

/**
 * Default annotation inspector for Populace. The inspector obtains annotations of the supplied {@code field}
 *
 * @author Andrew Coates - 24/03/2015.
 */
public class SimpleAnnotationInspector implements AnnotationInspector {
    public static final SimpleAnnotationInspector INSTANCE = new SimpleAnnotationInspector();

    @Override
    public <T extends Annotation> T getAnnotation(final Field field, final Class<T> type) {
        return field.getAnnotation(type);
    }

    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> type, final Method... accessorMethods) {
        return Arrays.stream(accessorMethods)
            .map(accessor -> accessor.getAnnotation(type))
            .filter(annotation -> annotation != null)
            .findFirst().orElse(null);
    }
}
