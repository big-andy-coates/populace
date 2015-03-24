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

import org.apache.commons.lang3.Validate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Chains two annotation inspectors. The {@code second} inspector is only called should the {@code first} not find
 * the annotation.
 *
 * @author Andrew Coates - 24/03/2015.
 */
public class ChainedAnnotationInspector implements AnnotationInspector {
    private final AnnotationInspector first;
    private final AnnotationInspector second;

    public ChainedAnnotationInspector(final AnnotationInspector first, final AnnotationInspector second) {
        Validate.notNull(first, "first null");
        Validate.notNull(second, "second null");
        this.first = first;
        this.second = second;
    }

    @Override
    public <T extends Annotation> T getAnnotation(final Field field, final Class<T> type) {
        final T annotation = first.getAnnotation(field, type);
        return annotation != null ? annotation : second.getAnnotation(field, type);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ChainedAnnotationInspector that = (ChainedAnnotationInspector) o;
        return first.equals(that.first) && second.equals(that.second);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ChainedAnnotationInspector{" +
            "first=" + first +
            ", second=" + second +
            '}';
    }
}
