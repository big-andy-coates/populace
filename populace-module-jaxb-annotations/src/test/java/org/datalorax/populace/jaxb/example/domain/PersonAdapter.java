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

package org.datalorax.populace.jaxb.example.domain;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Andrew Coates - 20/03/2015.
 */
public class PersonAdapter extends XmlAdapter<PersonImpl, Person> {
    @Override
    public Person unmarshal(final PersonImpl v) throws Exception {
        return v;
    }

    @Override
    public PersonImpl marshal(final Person v) throws Exception {
        return (PersonImpl) v;
    }
}
