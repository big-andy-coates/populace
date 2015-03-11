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

package org.datalorax.populace.jaxb;

/**
 * @author Andrew Coates - 09/03/2015.
 */
public class FunctionalTest {
    // Todo(ac): XmlTransient
    // Todo(ac): XmlFieldAccess, + XmlElement on field or get/setter, XmlValue, XmlAnyElement
    // Todo(ac): XmlJavaTypeAdaptor


    // Annotation inspector needs to:
    // Resolve null field from interface to concrete, using XmlJavaTypeAdaptor (and maybe XmlJavaTypeAdaptors)
    // Resolve null field from @XmlAnyElement to concrete type
    // Customise what fields are exposed.
}
