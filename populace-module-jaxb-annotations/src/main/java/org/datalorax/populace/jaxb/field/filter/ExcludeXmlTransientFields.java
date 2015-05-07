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
import org.datalorax.populace.core.walk.field.filter.FieldFilter;

import javax.xml.bind.annotation.XmlTransient;

/**
 * {@link org.datalorax.populace.core.walk.field.filter.FieldFilter} to include fields marked with
 * {@link javax.xml.bind.annotation.XmlTransient @XmlTransient}
 *
 * @author Andrew Coates - 12/03/2015.
 * @deprecated Use {@link FieldFilters#excludeXmlTransient()}
 */
@SuppressWarnings("deprecation")
@Deprecated
public class ExcludeXmlTransientFields implements FieldFilter {
    @SuppressWarnings("deprecation")
    public static final ExcludeXmlTransientFields INSTANCE = new ExcludeXmlTransientFields();

    @Override
    public boolean include(final FieldInfo field) {
        return test(field);
    }

    @Override
    public boolean test(final FieldInfo field) {
        return fieldNotXmlTransient(field) && fieldTypeNotXmlTransient(field);
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

    private boolean fieldNotXmlTransient(final FieldInfo field) {
        return field.getAnnotation(XmlTransient.class) == null;
    }

    private boolean fieldTypeNotXmlTransient(final FieldInfo field) {
        return field.getType().getAnnotation(XmlTransient.class) == null;
    }
}