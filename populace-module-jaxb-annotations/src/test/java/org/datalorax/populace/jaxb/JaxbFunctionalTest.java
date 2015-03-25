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

import org.datalorax.populace.core.populate.GraphPopulator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author Andrew Coates - 09/03/2015.
 */
public class JaxbFunctionalTest {
    private GraphPopulator populator;

    @BeforeMethod
    public void setUp() throws Exception {
        populator = PopulaceJaxb.install(GraphPopulator.newBuilder()).build();
    }

    @Test
    public void shouldHandleInterfaceToConcreteTypeAdapters() throws Exception {
        // Given:
        class TypeWithFieldUsingRelatedTypeAdapter {
            public RelatedBoundInterface type;
        }

        // When:
        final TypeWithFieldUsingRelatedTypeAdapter populated = populator.populate(new TypeWithFieldUsingRelatedTypeAdapter());

        // Then:
        assertThat(populated.type, is(instanceOf(RelatedValueType.class)));
    }

    @Test
    public void shouldHandleUnrelatedTypeAdapters() throws Exception {
        // Given:
        class TypeWithFieldUsingUnrelatedTypeAdapter {
            public UnrelatedBoundType type;
        }

        // When:
        final TypeWithFieldUsingUnrelatedTypeAdapter populated = populator.populate(new TypeWithFieldUsingUnrelatedTypeAdapter());

        // Then:
        assertThat(populated.type, is(instanceOf(UnrelatedBoundType.class)));
    }

    @Test
    public void shouldIgnoreXmlTransientFields() throws Exception {
        // Given:
        class TypeWithXmlTransientField {
            @XmlTransient
            public String _transient;
            public String _nonTransient;
        }

        // When:
        final TypeWithXmlTransientField populated = populator.populate(new TypeWithXmlTransientField());

        // Then:
        assertThat(populated._transient, is(nullValue()));
        assertThat(populated._nonTransient, is(notNullValue()));
    }

    @Test
    public void shouldIgnoreXmlTransientAccessors() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class TypeWithXmlTransientAccessors {
            private String transientGetter;
            private String transientSetter;

            @XmlTransient
            public String getTransientGetter() {
                return "";
            }

            public void setTransientGetter(final String v) {
            }

            public String getTransientSetter() {
                return "";
            }

            @XmlTransient
            public void setTransientSetter(final String v) {
            }
        }

        // When:
        final TypeWithXmlTransientAccessors populated = populator.populate(new TypeWithXmlTransientAccessors());

        // Then:
        assertThat(populated.transientGetter, is(nullValue()));
        assertThat(populated.transientSetter, is(nullValue()));
    }

    @Test
    public void shouldIgnoreXmlTransientTypes() throws Exception {
        // Given:
        @XmlTransient
        class TransientType {
        }

        @SuppressWarnings("UnusedDeclaration")
        class TypeWithXmlTransientTypeField {
            private TransientType field;
        }

        // When:
        final TypeWithXmlTransientTypeField populated = populator.populate(new TypeWithXmlTransientTypeField());

        // Then:
        assertThat(populated.field, is(nullValue()));
    }

    @XmlJavaTypeAdapter(AdapterForInterfaceWithTypeAdapter.class)
    public static interface RelatedBoundInterface {

    }

    @SuppressWarnings("UnusedDeclaration")
    public static class RelatedValueType implements RelatedBoundInterface {
        public String value;
    }

    private static class AdapterForInterfaceWithTypeAdapter extends XmlAdapter<RelatedValueType, RelatedBoundInterface> {
        @Override
        public RelatedBoundInterface unmarshal(final RelatedValueType value) throws Exception {
            return value;
        }

        @Override
        public RelatedValueType marshal(final RelatedBoundInterface bound) throws Exception {
            return (RelatedValueType) bound;
        }
    }

    @XmlJavaTypeAdapter(AdapterBetweenUnrelated.class)
    public static class UnrelatedBoundType {
        public String value;
    }

    public static class UnrelatedValueType {
        public String value;
    }

    private static class AdapterBetweenUnrelated extends XmlAdapter<UnrelatedValueType, UnrelatedBoundType> {
        @Override
        public UnrelatedBoundType unmarshal(final UnrelatedValueType v) throws Exception {
            final UnrelatedBoundType boundType = new UnrelatedBoundType();
            boundType.value = v.value;
            return boundType;
        }

        @Override
        public UnrelatedValueType marshal(final UnrelatedBoundType b) throws Exception {
            final UnrelatedValueType valueType = new UnrelatedValueType();
            valueType.value = b.value;
            return valueType;
        }
    }
}
