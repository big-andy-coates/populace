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

package org.datalorax.populace.core.jaxb.schema;

import org.testng.annotations.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

/**
 * @author Andrew Coates - 13/03/2015.
 */
public class JaxbAssumptionsTest {
    private static <T> T serialiseAndDeserialise(final T value) throws JAXBException {
        //noinspection unchecked
        return new TestMarshaller<T>((Class<T>) value.getClass()).marshallAndUnmarshall(value);
    }

    @Test
    public void shouldNotUseAccessorsForFieldMarkedWithXmlElement() throws Exception {
        // Given:
        final TypeWithAccessorsAndXmlElementOnField serialised = new TypeWithAccessorsAndXmlElementOnField();

        // When:
        final TypeWithAccessorsAndXmlElementOnField deserialised = serialiseAndDeserialise(serialised);

        // Then:
        assertThat(serialised.getterCalled, is(false));
        assertThat(deserialised.setterCalled, is(false));
    }

    @Test
    public void shouldIgnoreSetterWithUnrelatedTypes() throws Exception {
        final TypeWithUnrelatedGetterAndSetter serialised = new TypeWithUnrelatedGetterAndSetter(1);

        // When:
        final TypeWithUnrelatedGetterAndSetter deserialised = serialiseAndDeserialise(serialised);

        // Then:
        assertThat(deserialised.setterCalled, is(false));
    }

    @Test
    public void shouldIgnoresSetterWithWrongGenerics() throws Exception {
        // Given:
        final TypeWithMismatchedGenerics serialised = new TypeWithMismatchedGenerics("value");

        // When:
        final TypeWithMismatchedGenerics deserialised = serialiseAndDeserialise(serialised);

        // Then:
        assertThat(serialised.getterCalled, is(true));
        assertThat(deserialised.setterCalled, is(false));
    }

    @Test
    public void shouldIgnoreSettersOnRawMismatch() throws Exception {
        // Given:
        final TypeWithMixedRawGenericType serialised = new TypeWithMixedRawGenericType(1);

        // When:
        final TypeWithMixedRawGenericType deserialised = serialiseAndDeserialise(serialised);

        // Then:
        assertThat(serialised.getterCalled, is(true));
        assertThat(deserialised.setterCalled, is(false));
    }

    @Test
    public void shouldIgnoresSetterWithDerivedOrSuperTypes() throws Exception {
        // Given:
        final TypeWithSubTypeParameters serialised = new TypeWithSubTypeParameters(1);
        final TestMarshaller<TypeWithSubTypeParameters> marshaller = new TestMarshaller<>(TypeWithSubTypeParameters.class);

        // When:
        final String xml = marshaller.marshall(serialised);
        final TypeWithSubTypeParameters deserialised = marshaller.unmarshall(xml);

        // Then:
        assertThat("Property with derived setter should be exposed", xml, containsString("oneWay"));
        assertThat("Property with derived getter should be exposed", xml, containsString("theOther"));
        assertThat("Neither setter should not of been called", deserialised.setterCalled, is(false));
    }

    @Test(expectedExceptions = JAXBException.class)
    public void shouldThrowIfXmlElementOnBothFieldAndAccessors() throws Exception {
        serialiseAndDeserialise(new TypeWithXmlElementOnFieldAndAccessors());
    }

    @Test(expectedExceptions = JAXBException.class)
    public void shouldThrowIfXmlElementOnBothAccessors() throws Exception {
        serialiseAndDeserialise(new TypeWithXmlElementOnBothAccessors());
    }

    @Test
    public void shouldCallPrivateSetter() throws Exception {
        // Given:
        final TypeWithNonPublicSetter serialised = new TypeWithNonPublicSetter();

        // When:
        final TypeWithNonPublicSetter deserialised = serialiseAndDeserialise(serialised);

        // Then:
        assertThat(deserialised.setterCalled, is(true));
    }

    @Test
    public void shouldCallPrivateGetter() throws Exception {
        // Given:
        final TypeWithNonPublicGetter serialised = new TypeWithNonPublicGetter();

        // When:
        final TypeWithNonPublicGetter deserialised = serialiseAndDeserialise(serialised);

        // Then:
        assertThat(serialised.getterCalled, is(true));
        assertThat(deserialised.setterCalled, is(true));
    }

    @Test
    public void shouldIgnoreSetterWithNoArgs() throws Exception {
        // Given:
        final TypeWithNoArgSetter serialised = new TypeWithNoArgSetter();

        // When:
        final TypeWithNoArgSetter deserialised = serialiseAndDeserialise(serialised);

        // Then:
        assertThat(deserialised.setterCalled, is(false));
    }

    @Test
    public void shouldIgnoreSetterWithTooManyArgs() throws Exception {
        // Given:
        final TypeWithTooManyArgSetter serialised = new TypeWithTooManyArgSetter();

        // When:
        final TypeWithTooManyArgSetter deserialised = serialiseAndDeserialise(serialised);

        // Then:
        assertThat(deserialised.setterCalled, is(false));
    }

    private static class TestMarshaller<T> {
        private Marshaller marshaller;
        private Unmarshaller unmarshaller;

        public TestMarshaller(final Class<? extends T> type) throws JAXBException {
            final JAXBContext jaxbContext = JAXBContext.newInstance(type);
            this.marshaller = jaxbContext.createMarshaller();
            this.unmarshaller = jaxbContext.createUnmarshaller();
            this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        }

        public String marshall(final T value) throws JAXBException {
            return _marshall(value).toString();
        }

        public T unmarshall(final String value) throws JAXBException {
            return _unmarshall(value.getBytes());
        }

        public T marshallAndUnmarshall(final T value) throws JAXBException {
            final ByteArrayOutputStream baos = _marshall(value);
            return _unmarshall(baos.toByteArray());
        }

        private ByteArrayOutputStream _marshall(final T value) throws JAXBException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshaller.marshal(value, baos);
            return baos;
        }

        public T _unmarshall(final byte[] bytes) throws JAXBException {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            final Object result = unmarshaller.unmarshal(bais);
            //noinspection unchecked
            return (T) result;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.NONE)
    public static class TypeWithAccessorsAndXmlElementOnField {
        public boolean getterCalled;
        public boolean setterCalled;
        @XmlElement
        private String fieldWithXmlElement = "some value";

        public String getFieldWithXmlElement() {
            getterCalled = true;
            return fieldWithXmlElement;
        }

        public void setFieldWithXmlElement(String value) {
            setterCalled = true;
            fieldWithXmlElement = value;
        }
    }

    @SuppressWarnings({"UnusedDeclaration", "unchecked"})
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.NONE)
    public static class TypeWithMismatchedGenerics {
        private Map value = new HashMap<String, Integer>();
        private boolean getterCalled;
        private boolean setterCalled;

        public TypeWithMismatchedGenerics() {
        }

        public TypeWithMismatchedGenerics(final String value) {
            this.value.put(value, 1);
        }

        @XmlElement
        public Map<String, Integer> getField() {
            getterCalled = true;
            return value;
        }

        public void setField(Map<Integer, String> value) {
            setterCalled = true;
            this.value = value;
        }
    }

    @SuppressWarnings({"UnusedDeclaration", "unchecked"})
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.NONE)
    public static class TypeWithMixedRawGenericType {
        public Map<String, Integer> field;
        public boolean getterCalled;
        public boolean setterCalled;

        public TypeWithMixedRawGenericType() {
        }

        public TypeWithMixedRawGenericType(int populate) {
            field = new HashMap<>();
            field.put("value", 1);
        }

        @XmlElement
        public Map getGetterWithRaw() {
            getterCalled = true;
            return field;
        }

        public void setGetterWithRaw(Map<String, Integer> value) {
            setterCalled = true;
        }

        public Map<String, Integer> getSetterWithRaw() {
            getterCalled = true;
            return field;
        }

        public void setSetterWithRaw(Map value) {
            setterCalled = true;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.NONE)
    public static class TypeWithSubTypeParameters {
        public boolean setterCalled;
        private Base value;

        public TypeWithSubTypeParameters() {
        }

        public TypeWithSubTypeParameters(int x) {
            value = new Derived();
        }

        @XmlElement
        public Base getOneWay() {
            return value;
        }

        public void setOneWay(Derived value) {
            setterCalled = true;
            this.value = value;
        }

        @XmlElement
        public Derived getTheOther() {
            return (Derived) value;
        }

        public void setTheOther(Base value) {
            setterCalled = true;
            this.value = value;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class Base {
        public String value = "bob";
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class Derived extends Base {
        public String value2 = "peter";
    }

    @SuppressWarnings("UnusedDeclaration")
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.NONE)
    public static class TypeWithXmlElementOnFieldAndAccessors {
        @XmlElement
        private String field;

        @XmlElement
        public String getField() {
            return field;
        }

        public void setField(final String field) {
            this.field = field;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.NONE)
    public static class TypeWithXmlElementOnBothAccessors {
        @XmlElement
        public String getField() {
            return "";
        }

        @XmlElement
        public void setField(final String field) {
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.NONE)
    public static class TypeWithUnrelatedGetterAndSetter {
        public boolean setterCalled;
        private String field;

        public TypeWithUnrelatedGetterAndSetter() {
        }

        public TypeWithUnrelatedGetterAndSetter(int i) {
            field = "hello";
        }

        @XmlElement
        public String getField() {
            return field;
        }

        public void setField(final Long field) {
            this.setterCalled = true;
            this.field = "" + field;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.NONE)
    public static class TypeWithNonPublicSetter {
        public boolean setterCalled;
        private String field = "hello";

        @XmlElement
        public String getField() {
            return field;
        }

        private void setField(final String field) {
            this.setterCalled = true;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.NONE)
    public static class TypeWithNonPublicGetter {
        public boolean getterCalled;
        public boolean setterCalled;
        private String field = "hello";

        @XmlElement
        private String getField() {
            this.getterCalled = true;
            return field;
        }

        public void setField(final String field) {
            this.setterCalled = true;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.NONE)
    public static class TypeWithNoArgSetter {
        public boolean setterCalled;
        private String field = "hello";

        @XmlElement
        private String getField() {
            return field;
        }

        public void setField() {
            this.setterCalled = true;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.NONE)
    public static class TypeWithTooManyArgSetter {
        public boolean setterCalled;
        private String field = "hello";

        @XmlElement
        private String getField() {
            return field;
        }

        public void setField(String one, String two) {
            this.setterCalled = true;
        }
    }
}



