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

package org.datalorax.populace.jaxb.util;

import org.datalorax.populace.core.walk.inspector.InspectionException;
import org.testng.annotations.Test;

import javax.xml.bind.annotation.XmlElement;
import java.lang.reflect.Method;
import java.util.Map;

public class JaxbUtilsTest {
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
        for (Method declaredMethod : type.getDeclaredMethods()) {
            if (declaredMethod.getName().equals(methodName)) {
                return declaredMethod;
            }
        }
        throw new NoSuchMethodException(methodName);
    }

    @Test(expectedExceptions = InspectionException.class)
    public void shouldThrowOnInvalidGetterName() throws Exception {
        JaxbUtils.validateGetterAndSetter(getMethod("invalidGetValid"), setMethod("setValid"));
    }

    @Test(expectedExceptions = InspectionException.class)
    public void shouldThrowOnInvalidSetterName() throws Exception {
        JaxbUtils.validateGetterAndSetter(getMethod("getValid"), setMethod("invalidSetValid"));
    }

    @Test
    public void shouldRecogniseIsGetterNotation() throws Exception {
        JaxbUtils.validateGetterAndSetter(getMethod("isSomething"), setMethod("setSomething"));
    }

    @Test
    public void shouldSupportGetterOnSuperTypeOfSetter() throws Exception {
        JaxbUtils.validateGetterAndSetter(getMethod(Base.class, "getBase1"), setMethod("setBase1"));
    }

    @Test
    public void shouldSupportGetterOnSubtypeOfSetter() throws Exception {
        JaxbUtils.validateGetterAndSetter(getMethod("getBase2"), setMethod(Base.class, "setBase2"));
    }

    @Test
    public void shouldWorkWithInaccessibleSetter() throws Exception {
        JaxbUtils.validateGetterAndSetter(getMethod("getPrivateSetter"), setMethod("setPrivateSetter"));
    }

    @Test
    public void shouldWorkWithInaccessibleGetter() throws Exception {
        JaxbUtils.validateGetterAndSetter(getMethod("getPrivateGetter"), setMethod("setPrivateGetter"));
    }

    @Test(expectedExceptions = InspectionException.class)
    public void shouldThrowIfSetterHasNoArgs() throws Exception {
        JaxbUtils.validateGetterAndSetter(getMethod("getNoArgSetter"), setMethod("setNoArgSetter"));
    }

    @Test(expectedExceptions = InspectionException.class)
    public void shouldThrowIfSetterHasTooManyArgs() throws Exception {
        JaxbUtils.validateGetterAndSetter(getMethod("getTooManyArgSetter"), setMethod("setTooManyArgSetter"));
    }

    @Test(expectedExceptions = InspectionException.class)
    public void shouldThrowIfAccessorsHaveUnrelatedType() throws Exception {
        JaxbUtils.validateGetterAndSetter(getMethod("getWithDifferentType"), setMethod("setWithDifferentType"));
    }

    @Test(expectedExceptions = InspectionException.class)
    public void shouldThrowIfAccessorOnUnrelatedTypes() throws Exception {
        JaxbUtils.validateGetterAndSetter(getMethod(Unrelated.class, "getValid"), setMethod("setValid"));
    }

    // Todo(aC): handle array setter taking index.
    @Test
    public void shouldNotUseAccessorsForFieldMarkedWithXmlElement() throws Exception {
        // Todo(ac):...
    }

    @Test(expectedExceptions = InspectionException.class)
    public void shouldThrowOnSetterWithWrongGenerics() throws Exception {
        JaxbUtils.validateGetterAndSetter(getMethod("getSetterWithWrongGenerics"), setMethod("setSetterWithWrongGenerics"));
    }

    @Test(expectedExceptions = InspectionException.class)
    public void shouldThrowOnSetterWithRawGenerics() throws Exception {
        JaxbUtils.validateGetterAndSetter(getMethod("getSetterWithRawGenerics"), setMethod("setSetterWithRawGenerics"));
    }

    @Test(expectedExceptions = InspectionException.class)
    public void shouldThrowOnSetterWithSubType() throws Exception {
        JaxbUtils.validateGetterAndSetter(getMethod("getSetterWithSubType"), setMethod("setSetterWithSubType"));
    }

    @Test(expectedExceptions = InspectionException.class)
    public void shouldThrowOnSetterWithSuperType() throws Exception {
        JaxbUtils.validateGetterAndSetter(getMethod("getSetterWithSuperType"), setMethod("setSetterWithSuperType"));
    }

    @Test(expectedExceptions = InspectionException.class)
    public void shouldThrowIfXmlElementOnBothAccessors() throws Exception {
        JaxbUtils.validateGetterAndSetter(getMethod("getWithXmlElementBothAccessors"), setMethod("setWithXmlElementBothAccessors"));
    }

    @Test(expectedExceptions = InspectionException.class)
    public void shouldThrowIfNoXmlElementOnGetterOrSetter() throws Exception {
        JaxbUtils.validateGetterAndSetter(getMethod("getWithNoXmlElement"), setMethod("setWithNoXmlElement"));
    }

    @Test(expectedExceptions = InspectionException.class)
    public void shouldThrowIfFieldHasNoXmlElementAnnotation() throws Exception {

    }

    @Test(expectedExceptions = InspectionException.class)
    public void shouldThrowIsFieldIsStatic() throws Exception {

    }

    @Test(expectedExceptions = InspectionException.class)
    public void shouldThrowOnTransientField() throws Exception {
        // Todo(ac): check... what does JaxB do?
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class Base {
        @XmlElement
        public long getBase1() {
            return 2;
        }

        public void setBase2(long value) {
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class Bean extends Base {
        @XmlElement
        public long getValid() {
            return 1;
        }

        public void setValid(long valid) {

        }

        @XmlElement
        public long invalidGetValid() {
            return 1;
        }

        public void invalidSetValid(long v) {

        }

        @XmlElement
        public boolean isSomething() {
            return true;
        }

        public void setSomething(boolean v) {

        }

        public void setBase1(long value) {
        }

        @XmlElement
        public long getBase2() {
            return 1;
        }

        @XmlElement
        public String getPrivateSetter() {
            return "";
        }

        private void setPrivateSetter(String value) {
        }

        @XmlElement
        private String getPrivateGetter() {
            return "";
        }

        public void setPrivateGetter(String value) {
        }

        @XmlElement
        public Long getNoArgSetter() {
            return null;
        }

        public void setNoArgSetter() {

        }

        @XmlElement
        public Long getTooManyArgSetter() {
            return null;
        }

        public void setTooManyArgSetter(Long arg1, Long arg2) {

        }

        @XmlElement
        public Long getWithDifferentType() {
            return null;
        }

        public void setWithDifferentType(String value) {

        }

        @XmlElement
        public Map<Integer, String> getSetterWithWrongGenerics() {
            return null;
        }

        public void setSetterWithWrongGenerics(final Map<String, Integer> v) {
        }

        @XmlElement
        public Map<Integer, String> getSetterWithRawGenerics() {
            return null;
        }

        public void setSetterWithRawGenerics(final Map v) {
        }

        @XmlElement
        public Base getSetterWithSubType() {
            return null;
        }

        public void setSetterWithSubType(final Bean v) {
        }

        @XmlElement
        public Bean getSetterWithSuperType() {
            return null;
        }

        public void setSetterWithSuperType(final Base v) {
        }

        @XmlElement
        public String getWithXmlElementBothAccessors() {
            return "";
        }

        @XmlElement
        public void setWithXmlElementBothAccessors(final String v) {
        }

        public String getWithNoXmlElement() {
            return "";
        }

        public void setWithNoXmlElement(final String v) {
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    private static class Unrelated {
        public long getValid() {
            return 2;
        }
    }
}