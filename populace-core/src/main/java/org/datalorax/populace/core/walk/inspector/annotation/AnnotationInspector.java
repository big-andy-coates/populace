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

/**
 * A 'pluggable' annotation inspector that allows the logic used to find an annotation, given a field, to be customised.
 *
 * @author Andrew Coates - 24/03/2015.
 */
public interface AnnotationInspector {
    <T extends Annotation> T getAnnotation(Field field, Class<T> type);
}
