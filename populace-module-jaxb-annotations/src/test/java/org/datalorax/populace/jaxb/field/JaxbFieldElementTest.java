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

package org.datalorax.populace.jaxb.field;

import org.datalorax.populace.core.util.TypeUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class JaxbFieldElementTest {
    private JaxbFieldElement field;

    @BeforeMethod
    public void setUp() throws Exception {
        field = new JaxbFieldElement(Bean.class.getDeclaredField("someField"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowOnNullField() throws Exception {
        new JaxbFieldElement(null);
    }

    @Test
    public void shouldThrowIfInvalidJaxBField() throws Exception {
        new JaxbFieldElement(Bean.class.getDeclaredField("invalidField"));
    }

    @Test
    public void shouldReturnFieldName() throws Exception {
        assertThat(field.getName(), is("someField"));
    }

    @Test
    public void shouldReturnDeclaringClass() throws Exception {
        assertThat(field.getDeclaringClass(), is(equalTo(Bean.class)));
    }

    @Test
    public void shouldReturnGenericType() throws Exception {
        assertThat(field.getGenericType(), is(TypeUtils.parameterise(Map.class, String.class, Double.class)));
    }

    @Test
    public void shouldEnsureAccessible() throws Exception {
        // Given:
        field = new JaxbFieldElement(Bean.class.getDeclaredField("privateField"));
        assertThat("pre-condition", Bean.class.getDeclaredField("privateField").isAccessible(), is(false));

        // When:
        field.ensureAccessible();

        // Then:
        assertAccessible();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetValue() throws Exception {
        // Given:
        final Bean bean = new Bean("something", 1.2);

        // When:
        final Object value = field.getValue(bean);

        // Then:
        assertThat(value, is(instanceOf(Map.class)));
        assertThat(((Map<String, ?>) value).keySet(), contains("something"));
        assertThat(((Map<?, Double>) value).values(), contains(1.2));
    }

    @Test
    public void shouldSetField() throws Exception {
        // Given:
        final Bean bean = new Bean();
        final Map<String, Double> map = new HashMap<>();
        map.put("value", 22.5);

        // When:
        field.setValue(bean, map);

        // Then:
        assertThat(bean.someField, is(sameInstance(map)));
    }

    @Test
    public void shouldReturnAnnotation() throws Exception {
        assertThat(field.getAnnotation(XmlElement.class), is(notNullValue()));
    }

    @Test
    public void shouldReturnTransientIfMarkedXmlTransient() throws Exception {
        // Given:
        field = new JaxbFieldElement(Bean.class.getDeclaredField("someField"));

        // Then:
        assertThat(field.isTransient(), is(true));
    }

    @Test
    public void shouldReturnNotTransientIfNoXmlTransient() throws Exception {
        // Given:
        field = new JaxbFieldElement(Bean.class.getDeclaredField("privateField"));

        // Then:
        assertThat(field.isTransient(), is(false));
    }

    @Test
    public void shouldReturnNotStatic() throws Exception {
        assertThat(field.isStatic(), is(false));
    }

    @Test
    public void shouldReturnFinalIfFinal() throws Exception {
        // Given:
        field = new JaxbFieldElement(Bean.class.getDeclaredField("finalField"));

        // Then:
        assertThat(field.isFinal(), is(true));
    }

    @Test
    public void shouldReturnNotFinalIfNotFinal() throws Exception {
        // Given:
        field = new JaxbFieldElement(Bean.class.getDeclaredField("someField"));
        ;

        // Then:
        assertThat(field.isFinal(), is(false));
    }

    private void assertAccessible() throws ReflectiveOperationException {
        // Would throw is not:
        field.getValue(new Bean());
    }

    public static class Bean {
        @XmlElement
        private final String finalField = "";
        @XmlElement
        @XmlTransient
        @SuppressWarnings("UnusedDeclaration")
        public Map<String, Double> someField;
        @XmlElement
        private transient String privateField;
        private int invalidField;

        public Bean() {

        }

        public Bean(final String key, final Double value) {
            someField = new HashMap<>();
            someField.put(key, value);
        }
    }
}