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

package org.datalorax.populace.jaxb.field.filter;

import org.datalorax.populace.core.walk.field.FieldInfo;

import javax.xml.bind.annotation.XmlTransient;
import java.util.function.Predicate;

/**
 * @author Andrew Coates - 06/05/2015.
 */
public class FieldFilters extends org.datalorax.populace.core.walk.field.filter.FieldFilters {
    public static Predicate<FieldInfo> excludeXmlTransient() {
        return f -> fieldNotXmlTransient(f) && fieldTypeNotXmlTransient(f);
    }

    private static boolean fieldNotXmlTransient(final FieldInfo field) {
        return field.getAnnotation(XmlTransient.class) == null;
    }

    private static boolean fieldTypeNotXmlTransient(final FieldInfo field) {
        return field.getType().getAnnotation(XmlTransient.class) == null;
    }
}
