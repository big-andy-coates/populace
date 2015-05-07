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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.bind.annotation.XmlTransient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FieldFiltersTest {
    private FieldInfo field;

    @BeforeMethod
    public void setUp() throws Exception {
        field = mock(FieldInfo.class);
    }

    @Test
    public void shouldExcludeIfFieldIsMarkedXmlTransient() throws Exception {
        // Given:
        givenFieldMarkedTransient();
        givenClassNotMarkedTransient();

        // Then:
        assertThat(FieldFilters.excludeXmlTransient().test(field), is(false));
    }

    @Test
    public void shouldExcludeIfFieldTypeIsMarkedXmlTransient() throws Exception {
        // Given:
        givenFieldNotMarkedTransient();
        givenClassMarkedTransient();

        // Then:
        assertThat(FieldFilters.excludeXmlTransient().test(field), is(false));
    }

    @Test
    public void shouldExcludeIfBothFieldClassAndTypeAreMarkedXmlTransient() throws Exception {
        // Given:
        givenFieldMarkedTransient();
        givenClassMarkedTransient();

        // Then:
        assertThat(FieldFilters.excludeXmlTransient().test(field), is(false));
    }

    @Test
    public void shouldIncludeIfFieldAndClassNotMarkedWithXmlTransient() throws Exception {
        // Given:
        givenFieldNotMarkedTransient();
        givenClassNotMarkedTransient();

        // Then:
        assertThat(FieldFilters.excludeXmlTransient().test(field), is(true));
    }

    private void givenFieldNotMarkedTransient() {
        when(field.getAnnotation(XmlTransient.class)).thenReturn(null);
    }

    private void givenFieldMarkedTransient() {
        when(field.getAnnotation(XmlTransient.class)).thenReturn(mock(XmlTransient.class));
    }

    @SuppressWarnings("unchecked")
    private void givenClassNotMarkedTransient() {
        when(field.getType()).thenReturn((Class) String.class);
    }

    @SuppressWarnings("unchecked")
    private void givenClassMarkedTransient() {
        @XmlTransient
        class TransientType {
        }
        when(field.getType()).thenReturn((Class) TransientType.class);
    }
}