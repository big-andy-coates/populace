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
import org.datalorax.populace.jaxb.field.JaxbPropertyElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.bind.annotation.XmlTransient;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class JaxbPropertyElementTest {
    private Method getter;
    private Method setter;
    private JaxbPropertyElement property;

    private static Method getMethod(final String methodName) throws NoSuchMethodException {
        return getMethod(Bean.class, methodName);
    }

    private static Method getMethod(final Class<?> type, final String methodName) throws NoSuchMethodException {
        return type.getDeclaredMethod(methodName);
    }

    private static Method setMethod(final String methodName) throws NoSuchMethodException {
        return setMethod(Bean.class, methodName);
    }

    private static Method setMethod(final Class<?> type, final String methodName) throws NoSuchMethodException {
        return type.getDeclaredMethod(methodName, Map.class);
    }

    @BeforeMethod
    public void setUp() throws Exception {
        getter = getMethod("getValue");
        setter = setMethod("setValue");

        property = new JaxbPropertyElement(getter, setter);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowOnNullGetter() throws Exception {
        new JaxbPropertyElement(null, setter);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowOnNullSetter() throws Exception {
        new JaxbPropertyElement(getter, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnInvalidGetter() throws Exception {
        new JaxbPropertyElement(getMethod("invalidGetter"), setter);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnInvalidSetter() throws Exception {
        new JaxbPropertyElement(getter, setMethod("invalidSetter"));
    }

    @Test
    public void shouldReturnName() throws Exception {
        assertThat(property.getName(), is("value"));
    }

    @Test
    public void shouldReturnDeclaringClassFromSetterIfItIsSubType() throws Exception {
        // Given:
        property = new JaxbPropertyElement(getMethod(Base.class, "getSubTypeSetter"), setMethod("setSubTypeSetter"));

        // Then:
        assertThat(property.getDeclaringClass(), is(equalTo(Bean.class)));
    }

    @Test
    public void shouldReturnDeclaringClassFromGetterIfItIsSubType() throws Exception {
        // Given:
        property = new JaxbPropertyElement(getMethod("getSubTypeGetter"), setMethod(Base.class, "setSubTypeGetter"));

        // Then:
        assertThat(property.getDeclaringClass(), is(equalTo(Bean.class)));
    }

    @Test
    public void shouldReturnGenericType() throws Exception {
        assertThat(property.getGenericType(), is(TypeUtils.parameterise(Map.class, String.class, Integer.class)));
    }

    @Test
    public void shouldNotBlowUpOnEnsureAccessible() throws Exception {
        property.ensureAccessible();
    }

    @Test
    public void shouldGetCurrentValue() throws Exception {
        // Given:
        final Bean instance = new Bean();
        instance.setValue(new HashMap<>());

        // When:
        final Object value = property.getValue(instance);

        // Then:
        assertThat(value, is(notNullValue()));
        assertThat(value, is(sameInstance(instance.getValue())));
    }

    @Test
    public void shouldSetCurrentValue() throws Exception {
        // Given:
        final Bean instance = new Bean();
        final Map<String, Integer> newValue = new HashMap<String, Integer>() {{
            put("this", 1);
        }};

        // When:
        property.setValue(instance, newValue);

        // Then:
        assertThat(instance.getValue(), is(newValue));
    }

    @Test
    public void shouldNotGetAnnotationIfItIsNotPresent() throws Exception {
        assertThat(property.getAnnotation(Deprecated.class), is(nullValue()));
    }

    @Test
    public void shouldGetAnnotationOfSetterIfPresent() throws Exception {
        // Given:
        property = new JaxbPropertyElement(getMethod("getWithAnnotationOnSetter"), setMethod("setWithAnnotationOnSetter"));

        // Then:
        assertThat(property.getAnnotation(Deprecated.class), is(notNullValue()));
    }

    @Test
    public void shouldGetAnnotationOfGetterIfPresent() throws Exception {
        // Given:
        property = new JaxbPropertyElement(getMethod("getWithAnnotationOnGetter"), setMethod("setWithAnnotationOnGetter"));

        // Then:
        assertThat(property.getAnnotation(Deprecated.class), is(notNullValue()));
    }

    @Test
    public void shouldDetectXmlTransient() throws Exception {
        // Given:
        property = new JaxbPropertyElement(getMethod("getTransient"), setMethod("setTransient"));

        // Then:
        assertThat(property.isTransient(), is(true));
    }

    @Test
    public void shouldNotBeTransientIfNoAnnotation() throws Exception {
        assertThat(property.isTransient(), is(false));
    }

    @Test
    public void shouldNotBeStatic() throws Exception {
        assertThat(property.isStatic(), is(false));
    }

    @Test
    public void shouldNotBeFinal() throws Exception {
        assertThat(property.isFinal(), is(false));
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class Base {
        public Map<String, Integer> getSubTypeSetter() {
            return null;
        }

        public void setSubTypeGetter(Map<String, Integer> value) {
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class Bean extends Base {
        private Map<String, Integer> value;

        public Map<String, Integer> getValue() {
            return value;
        }

        public void setValue(Map<String, Integer> value) {
            this.value = value;
        }

        public boolean invalidGetter() {
            return true;
        }

        public void invalidSetter(final Map<String, Integer> value) {
        }

        @XmlTransient
        public Map<String, Integer> getTransient() {
            return null;
        }

        public void setTransient(final Map<String, Integer> value) {
        }

        @Deprecated
        public Map<String, Integer> getWithAnnotationOnGetter() {
            return null;
        }

        public void setWithAnnotationOnGetter(Map<String, Integer> value) {
        }

        public Map<String, Integer> getWithAnnotationOnSetter() {
            return null;
        }

        @Deprecated
        public void setWithAnnotationOnSetter(Map<String, Integer> value) {
        }

        public Map<String, Integer> getSubTypeGetter() {
            return null;
        }

        public void setSubTypeSetter(Map<String, Integer> value) {
        }
    }
}