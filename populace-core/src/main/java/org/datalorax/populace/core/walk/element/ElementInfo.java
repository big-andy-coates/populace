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

package org.datalorax.populace.core.walk.element;

import org.apache.commons.lang3.Validate;
import org.datalorax.populace.core.util.TypeResolver;
import org.datalorax.populace.core.walk.GraphComponent;
import org.datalorax.populace.core.walk.field.PathProvider;

import java.lang.reflect.Type;

/**
 * @author Andrew Coates - 04/03/2015.
 */
public class ElementInfo implements GraphComponent {
    private final RawElement element;
    private final TypeResolver typeResolver;
    private final PathProvider path;
    private final Type containerType;

    public ElementInfo(final RawElement element, final Type containerType, final TypeResolver typeResolver, final PathProvider path) {
        Validate.notNull(element, "field null");
        Validate.notNull(containerType, "field containerType");
        Validate.notNull(typeResolver, "typeResolver null");
        Validate.notNull(path, "path null");
        this.element = element;
        this.containerType = containerType;
        this.typeResolver = typeResolver;
        this.path = path;
    }

    /**
     * Get the depth of this object into the walk
     *
     * @return the number of parent objects between this element and the root of the walk
     */
    public int getDepth() {
        return path.getDepth();
    }

    /**
     * @return the generic type of the element
     * @see org.datalorax.populace.core.walk.element.RawElement#getGenericType(java.lang.reflect.Type)
     */
    public Type getGenericType() {
        // Todo(ac): get from value elements.
        return typeResolver.resolve(element.getGenericType(containerType));
    }

    /**
     * @return the current value of the {@code RawElement}.
     */
    public Object getValue() {
        return element.getValue();
    }

    /**
     * Sets the current value of the {@code RawElement}
     *
     * @param value the new value for the element being modified
     */
    public void setValue(Object value) {
        element.setValue(value);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ElementInfo that = (ElementInfo) o;
        return element.equals(that.element) && path.equals(that.path) && typeResolver.equals(that.typeResolver);
    }

    @Override
    public int hashCode() {
        int result = element.hashCode();
        result = 31 * result + typeResolver.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ElementInfo{" +
            "path=" + path.getPath() +
            ", type=" + getGenericType() +
            '}';
    }
}
