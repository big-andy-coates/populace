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

package org.datalorax.jaxb.field.filter;

import org.datalorax.populace.field.filter.FieldFilter;

import javax.xml.bind.annotation.XmlTransient;
import java.lang.reflect.Field;

/**
 * {@link org.datalorax.populace.field.filter.FieldFilter} to exclude fields marked with
 * {@link javax.xml.bind.annotation.XmlTransient @XmlTransient}
 *
 * @author Andrew Coates - 12/03/2015.
 */
public class ExcludeXmlTransientFields implements FieldFilter {
    public static ExcludeXmlTransientFields INSTANCE = new ExcludeXmlTransientFields();

    @Override
    public boolean evaluate(final Field field) {
        // Todo(ac): needs to work with getter / setter too. But need to switch to FieldInfo first.
        return field.getAnnotation(XmlTransient.class) == null;
    }
}
