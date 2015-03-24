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

package org.datalorax.populace.jaxb.walk.annotation;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("ALL")
public class JaxbAnnotationInspectorTest {
    private JaxbAnnotationInspector inspector;

    @BeforeMethod
    public void setUp() throws Exception {
        inspector = JaxbAnnotationInspector.INSTANCE;
    }

    @Test
    public void shouldReturnNullForNonXmlAnnotations() throws Exception {
        // Given:
        class SomeType {
            @Deprecated
            public String field;
        }
        final Field field = SomeType.class.getDeclaredField("field");

        // Then:
        assertThat(inspector.getAnnotation(field, Deprecated.class), is(nullValue()));
    }

    @Test
    public void shouldReturnNullIfAnnotationNotFound() throws Exception {
        // Given:
        class SomeTypeWithAccessors {
            public String field;
        }
        final Field field = SomeTypeWithAccessors.class.getDeclaredField("field");

        // Then:
        assertThat(inspector.getAnnotation(field, XmlTransient.class), is(nullValue()));
    }

    @Test
    public void shouldReturnNullIfAnnotationNotFoundWithAccessors() throws Exception {
        // Given:
        class SomeTypeWithAccessors {
            public String field;

            public String getField() {
                return "";
            }

            public void setField(final String value) {
            }
        }
        final Field field = SomeTypeWithAccessors.class.getDeclaredField("field");

        // Then:
        assertThat(inspector.getAnnotation(field, XmlTransient.class), is(nullValue()));
    }

    @Test
    public void shouldGetAnnotationFromFieldBeforeGetterOrSetter() throws Exception {
        // Given:
        class TypeWithAttributeOnField {
            @XmlJavaTypeAdapter(TypeAdapterOne.class)
            public String field;

            @XmlJavaTypeAdapter(TypeAdapterTwo.class)
            public String getField() {
                return "";
            }

            @XmlJavaTypeAdapter(TypeAdapterThree.class)
            public void setField(final String v) {
            }
        }
        final Field field = TypeWithAttributeOnField.class.getDeclaredField("field");

        // Then:
        assertThat(inspector.getAnnotation(field, XmlJavaTypeAdapter.class), is(instanceOf(XmlJavaTypeAdapter.class)));
        assertThat(inspector.getAnnotation(field, XmlJavaTypeAdapter.class).value(), is(equalTo(TypeAdapterOne.class)));
    }

    @Test
    public void shouldGetAnnotationFromGetterBeforeSetter() throws Exception {
        // Given:
        class TypeWithAttributeOnGetter {
            public String field;

            @XmlJavaTypeAdapter(TypeAdapterTwo.class)
            public String getField() {
                return "";
            }

            @XmlJavaTypeAdapter(TypeAdapterThree.class)
            public void setField(final String v) {
            }
        }
        final Field field = TypeWithAttributeOnGetter.class.getDeclaredField("field");

        // Then:
        assertThat(inspector.getAnnotation(field, XmlJavaTypeAdapter.class), is(instanceOf(XmlJavaTypeAdapter.class)));
        assertThat(inspector.getAnnotation(field, XmlJavaTypeAdapter.class).value(), is(equalTo(TypeAdapterTwo.class)));
    }

    @Test
    public void shouldGetAnnotationFromSetter() throws Exception {
        // Given:
        class TypeWithAttributeOnSetter {
            public String field;

            public String getField() {
                return "";
            }

            @XmlJavaTypeAdapter(TypeAdapterThree.class)
            public void setField(final String v) {
            }
        }
        final Field field = TypeWithAttributeOnSetter.class.getDeclaredField("field");

        // Then:
        assertThat(inspector.getAnnotation(field, XmlJavaTypeAdapter.class), is(instanceOf(XmlJavaTypeAdapter.class)));
        assertThat(inspector.getAnnotation(field, XmlJavaTypeAdapter.class).value(), is(equalTo(TypeAdapterThree.class)));
    }

    @Test
    public void shouldIgnoreAccessorsIfFieldIsMarkedAsXmlElement() throws Exception {
        // Given:
        class TypeWithAttributeOnGetter {
            @XmlElement
            public String field;

            @XmlJavaTypeAdapter(TypeAdapterTwo.class)
            public String getField() {
                return "";
            }

            @XmlJavaTypeAdapter(TypeAdapterThree.class)
            public void setField(final String v) {
            }
        }
        final Field field = TypeWithAttributeOnGetter.class.getDeclaredField("field");

        // Then:
        assertThat(inspector.getAnnotation(field, XmlJavaTypeAdapter.class), is(nullValue()));
    }

    @Test
    public void shouldIgnoreAccessorsThatDontMarchXmlElementType() throws Exception {
        // Given:
        class TypeWithAttributeOnGetter {
            public String field;

            @XmlElement
            public String getField() {
                return "";
            }

            @XmlJavaTypeAdapter(TypeAdapterThree.class)
            public void setField(final long v) {
            }

            @XmlJavaTypeAdapter(TypeAdapterThree.class)
            public void setField(final String v, final String v2) {
            }
        }
        final Field field = TypeWithAttributeOnGetter.class.getDeclaredField("field");

        // Then:
        assertThat(inspector.getAnnotation(field, XmlJavaTypeAdapter.class), is(nullValue()));
    }

    @Test
    public void shouldSupportIsGetterNaming() throws Exception {
        // Given:
        class TypeWithBooleanIsGetters {
            public Boolean bigBoolean;
            public boolean smallBoolean;

            @XmlElement
            @XmlJavaTypeAdapter(TypeAdapterOne.class)
            public Boolean isBigBoolean() {
                return bigBoolean;
            }

            @XmlElement
            @XmlJavaTypeAdapter(TypeAdapterTwo.class)
            public boolean isSmallBoolean() {
                return smallBoolean;
            }
        }
        final Field bigField = TypeWithBooleanIsGetters.class.getDeclaredField("bigBoolean");
        final Field smallField = TypeWithBooleanIsGetters.class.getDeclaredField("smallBoolean");

        // Then:
        assertThat(inspector.getAnnotation(bigField, XmlJavaTypeAdapter.class), is(notNullValue()));
        assertThat(inspector.getAnnotation(bigField, XmlJavaTypeAdapter.class).value(), is(equalTo(TypeAdapterOne.class)));
        assertThat(inspector.getAnnotation(smallField, XmlJavaTypeAdapter.class), is(notNullValue()));
        assertThat(inspector.getAnnotation(smallField, XmlJavaTypeAdapter.class).value(), is(equalTo(TypeAdapterTwo.class)));
    }

    private class TypeAdapterOne extends XmlAdapter {
        @Override
        public Object unmarshal(final Object v) throws Exception {
            return null;
        }

        @Override
        public Object marshal(final Object v) throws Exception {
            return null;
        }
    }

    private class TypeAdapterTwo extends XmlAdapter {
        @Override
        public Object unmarshal(final Object v) throws Exception {
            return null;
        }

        @Override
        public Object marshal(final Object v) throws Exception {
            return null;
        }
    }

    private class TypeAdapterThree extends XmlAdapter {
        @Override
        public Object unmarshal(final Object v) throws Exception {
            return null;
        }

        @Override
        public Object marshal(final Object v) throws Exception {
            return null;
        }
    }
}