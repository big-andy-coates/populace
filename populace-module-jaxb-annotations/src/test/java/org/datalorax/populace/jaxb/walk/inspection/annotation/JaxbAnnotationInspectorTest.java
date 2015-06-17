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

package org.datalorax.populace.jaxb.walk.inspection.annotation;

import com.google.common.testing.EqualsTester;
import org.datalorax.populace.core.walk.inspector.annotation.AnnotationInspector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

@SuppressWarnings("ALL")
public class JaxbAnnotationInspectorTest {
    private JaxbAnnotationInspector inspector;

    @BeforeMethod
    public void setUp() throws Exception {
        inspector = JaxbAnnotationInspector.INSTANCE;
    }

    @Test
    public void shouldReturnNullForFieldsIfNonXmlAnnotations() throws Exception {
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
    public void shouldReturnNullForMethodsIfNonXmlAnnotations() throws Exception {
        // Given:
        final Method method = getClass().getDeclaredMethod("shouldReturnNullForMethodsIfNonXmlAnnotations");

        // Then:
        assertThat(inspector.getAnnotation(Test.class), is(nullValue()));
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
        assertThat(inspector.getAnnotation(field, XmlJavaTypeAdapter.class).value(), is(equalTo(TypeAdapterThree.class)));
    }

    @Test
    public void shouldPickRightOverloadedSetter() throws Exception {
        // Given:
        class Super {
        }

        class Sub extends Super {
        }

        class SubSub extends Sub {
        }

        class TypeWithOverloadedAccessors {
            public Sub field;

            public Sub getField() {
                return null;
            }

            @XmlJavaTypeAdapter(TypeAdapterOne.class)
            public void setField(final Super v) {
            }

            @XmlJavaTypeAdapter(TypeAdapterTwo.class)
            public void setField(final Sub v) {
            }

            @XmlJavaTypeAdapter(TypeAdapterThree.class)
            public void setField(final SubSub v) {
            }

            @XmlJavaTypeAdapter(TypeAdapterThree.class)
            public void setField(final long unrelated) {
            }
        }
        final Field field = TypeWithOverloadedAccessors.class.getDeclaredField("field");

        // Then:
        assertThat(inspector.getAnnotation(field, XmlJavaTypeAdapter.class).value(), is(equalTo(TypeAdapterTwo.class)));
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

    @XmlElement
    @Test
    public void shouldGetAnnotationOfFirstMethodIfPresent() throws Exception {
        // Given:
        final Method method1 = getClass().getDeclaredMethod("shouldGetAnnotationOfFirstMethodIfPresent");
        final Method method2 = getClass().getDeclaredMethod("setUp");
        final XmlElement expected = method1.getAnnotation(XmlElement.class);

        // Then:
        assertThat(inspector.getAnnotation(XmlElement.class, method1, method2), is(expected));
    }

    @XmlElement
    @Test
    public void shouldGetAnnotationOfSecondMethodIfPresentButNotOnFirst() throws Exception {
        // Given:
        final Method method1 = getClass().getDeclaredMethod("setUp");
        final Method method2 = getClass().getDeclaredMethod("shouldGetAnnotationOfSecondMethodIfPresentButNotOnFirst");
        final XmlElement expected = method2.getAnnotation(XmlElement.class);

        // Then:
        assertThat(inspector.getAnnotation(XmlElement.class, method1, method2), is(expected));
    }

    @Test
    public void shouldReturnNullIfNoMethodsHaveAnnotation() throws Exception {
        // Given:
        final Method method1 = getClass().getDeclaredMethod("setUp");
        final Method method2 = getClass().getDeclaredMethod("setUp");

        // Then:
        assertThat(inspector.getAnnotation(XmlElement.class, method1, method2), is(nullValue()));
    }

    @Test
    public void shouldReturnNullOnEmptyArrayOfAccessors() throws Exception {
        assertThat(inspector.getAnnotation(XmlElement.class), is(nullValue()));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                JaxbAnnotationInspector.INSTANCE,
                new JaxbAnnotationInspector())
            .addEqualityGroup(
                mock(AnnotationInspector.class))
            .testEquals();
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