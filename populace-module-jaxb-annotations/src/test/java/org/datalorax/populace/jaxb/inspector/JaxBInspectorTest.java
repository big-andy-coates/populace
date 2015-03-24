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

package org.datalorax.populace.jaxb.inspector;

import org.datalorax.populace.core.walk.field.RawField;
import org.datalorax.populace.core.walk.inspector.InspectionException;
import org.datalorax.populace.core.walk.inspector.Inspectors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Map;

import static org.datalorax.populace.core.walk.field.filter.RawFieldMatcher.rawField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

public class JaxBInspectorTest {
    private JaxBInspector inspector;
    private Inspectors inspectors;

    @BeforeMethod
    public void setUp() throws Exception {
        inspectors = mock(Inspectors.class);
        inspector = JaxBInspector.INSTANCE;
    }

    @Test
    public void shouldFindFieldsOnSubType() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class TypeWithFieldElement {
            @XmlElement
            public String elementField;
        }

        // When:
        final Iterable<RawField> fields = inspector.getFields(TypeWithFieldElement.class, inspectors);

        // Then:
        assertThat(fields, contains(rawField(TypeWithFieldElement.class, "elementField")));
    }

    @Test
    public void shouldIgnoreFieldsNotMarkedWithXmlElement() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class TypeWithNonElementField {
            public String nonElementField;
        }

        // When:
        final Iterable<RawField> fields = inspector.getFields(TypeWithNonElementField.class, inspectors);

        // Then:
        assertThat(fields, not(hasItem(rawField(TypeWithNonElementField.class, "nonElementField"))));
    }

    @Test
    public void shouldFindFieldsOnSuperTypes() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class SomeSuperType {
            @XmlElement
            long superElementField;
        }

        class SomeSubType extends SomeSuperType {
        }

        // When:
        final Iterable<RawField> fields = inspector.getFields(SomeSubType.class, inspectors);

        // Then:
        assertThat(fields, contains(rawField(SomeSuperType.class, "superElementField")));
    }

    @Test
    public void shouldFindFieldsPrivateFields() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class TypeWithPrivateField {
            @XmlElement
            private Map privateField;
        }

        // When:
        final Iterable<RawField> fields = inspector.getFields(TypeWithPrivateField.class, inspectors);

        // Then:
        assertThat(fields, hasItem(rawField(TypeWithPrivateField.class, "privateField")));
    }

    // Todo(ac): XmlTransient should NOT affect RawField isTransient. It shoudl only affect what inspector returns. JaxBUtils should throw on XmlTransient

    @Test
    public void shouldFindXmlElementGetterAndSettersOnSubType() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class SomeType {
            @XmlElement
            public long getGetterElement() {
                return 1;
            }

            public void setGetterElement(long v) {
            }

            public long getSetterElement() {
                return 1;
            }

            @XmlElement
            public void setSetterElement(long v) {
            }
        }

        // When:
        final Iterable<RawField> fields = inspector.getFields(SomeType.class, inspectors);

        // Then:
        assertThat(fields, containsInAnyOrder(
            rawField(SomeType.class, "getterElement"),
            rawField(SomeType.class, "setterElement")
        ));
    }

    @Test
    public void shouldIgnoreGetterSetterNotMarkedWithXmlElement() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class SomeType {
            public long getNonElement() {
                return 1;
            }

            public void setNonElement(long v) {
            }
        }

        // When:
        final Iterable<RawField> fields = inspector.getFields(SomeType.class, inspectors);

        // Then:
        assertThat(fields, not(hasItem(rawField(SomeType.class, "nonElement"))));
    }

    @Test
    public void shouldIgnoreIncompleteGetterSetters() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class SomeType {

            @XmlElement
            public long getIgnoredGetter() {
                return 1;
            }

            @XmlElement
            public void setIgnoredSetter() {
            }
        }

        // When:
        final Iterable<RawField> fields = inspector.getFields(SomeType.class, inspectors);

        // Then:
        assertThat(fields, not(hasItem(rawField(SomeType.class, "ignoredGetter"))));
        assertThat(fields, not(hasItem(rawField(SomeType.class, "ignoredSetter"))));
    }

    @Test
    public void shouldFindGetterSetterOnSuperType() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class SomeSuperType {
            @XmlElement
            public String getSuperGetter() {
                return "";
            }

            public void setSuperGetter(String v) {
            }

            public String getSuperSetter() {
                return "";
            }

            @XmlElement
            public void setSuperSetter(String v) {
            }
        }

        class SomeType extends SomeSuperType {
        }

        // When:
        final Iterable<RawField> fields = inspector.getFields(SomeType.class, inspectors);

        // Then:
        assertThat(fields, containsInAnyOrder(
            rawField(SomeSuperType.class, "superGetter"),
            rawField(SomeSuperType.class, "superSetter")));
    }

    @Test
    public void shouldFindPrivateGetterSetter() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class SomeType {

            @XmlElement
            private long getPrivateGetter() {
                return 1;
            }

            public void setPrivateGetter(long v) {
            }

            @XmlElement
            public long getPrivateSetter() {
                return 1;
            }

            private void setPrivateSetter(long v) {
            }
        }

        // When:
        final Iterable<RawField> fields = inspector.getFields(SomeType.class, inspectors);

        // Then:
        assertThat(fields, containsInAnyOrder(
            rawField(SomeType.class, "privateGetter"),
            rawField(SomeType.class, "privateSetter")
        ));
    }

    @Test
    public void shouldFindGetterSetterSpreadAcrossTypeHierarchy() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class SomeSuperType {
            @XmlElement
            public long getSubSetterSuperGetter() {
                return 1;
            }

            public void setSubGetterSuperSetter(long v) {
            }
        }

        @SuppressWarnings("UnusedDeclaration")
        class SomeSubType extends SomeSuperType {
            @XmlElement
            public long getSubGetterSuperSetter() {
                return 1;
            }

            public void setSubSetterSuperGetter(long v) {
            }
        }

        // When:
        final Iterable<RawField> fields = inspector.getFields(SomeSubType.class, inspectors);

        // Then:
        assertThat(fields, containsInAnyOrder(
            rawField(SomeSubType.class, "subGetterSuperSetter"),
            rawField(SomeSubType.class, "subSetterSuperGetter")
        ));
    }

    @Test(expectedExceptions = InspectionException.class)
    public void shouldThrowOnInvalidElement() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class TypeWithInvalidAnnotations {
            @XmlElement
            public long getInvalidToHaveXmlElementOnBothAccessors() {
                return 1;
            }

            @XmlElement
            public void setInvalidToHaveXmlElementOnBothAccessors(long v) {
            }
        }

        // When:
        inspector.getFields(TypeWithInvalidAnnotations.class, inspectors);
    }

    @Test(expectedExceptions = InspectionException.class)
    public void shouldThrowOnDuplicateElement() throws Exception {
        // Given:
        @SuppressWarnings("UnusedDeclaration")
        class TypeWithDuplicateAnnotations {
            @XmlElement
            public long duplicate;

            @XmlElement
            public long getDuplicate() {
                return 1;
            }

            @XmlElement
            public void setDuplicate(long v) {
            }
        }

        // When:
        inspector.getFields(TypeWithDuplicateAnnotations.class, inspectors);
    }

    @Test
    public void shouldRespectXmlAccessorType() throws Exception {
        // Given:
        @XmlAccessorType(XmlAccessType.NONE)
        @SuppressWarnings("UnusedDeclaration")
        class SomeType {
            public String notAnElement;

            public String getNotAnElement() {
                return "";
            }

            public void setNotAnElement(String v) {
            }
        }

        // When:
        final Iterable<RawField> fields = inspector.getFields(SomeType.class, inspectors);

        // Then:
        assertThat(fields, not(hasItem(rawField(SomeType.class, "notAnElement"))));
    }


    // Todo(ac): what about abstract types and methods?
    // Todo(ac): What about array accessors?
}