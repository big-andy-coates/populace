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

package org.datalorax.populace.core.walk.inspector;

import com.google.common.testing.EqualsTester;
import org.datalorax.populace.core.walk.field.RawField;
import org.datalorax.populace.core.walk.inspector.annotation.AnnotationInspector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.datalorax.populace.core.walk.field.filter.RawFieldMatcher.rawField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"UnusedDeclaration", "unchecked"})
public class FieldInspectorTest {
    private Inspector inspector;
    private Inspectors inspectors;

    @BeforeMethod
    public void setUp() throws Exception {
        inspectors = mock(Inspectors.class);
        inspector = FieldInspector.INSTANCE;

        final AnnotationInspector annotationInspector = mock(AnnotationInspector.class);
        when(inspectors.getAnnotationInspector()).thenReturn(annotationInspector);
    }

    @Test
    public void shouldReturnFields() throws Exception {
        // Given:
        class SomeType {
            public String fieldOne;
            public Long fieldTwo;
        }

        // When:
        final Iterable<RawField> fields = inspector.getFields(SomeType.class, inspectors);

        // Then:
        assertThat(fields, hasItems(
            rawField(SomeType.class, "fieldOne"),
            rawField(SomeType.class, "fieldTwo")
        ));
    }

    @Test
    public void shouldReturnPrivateFields() throws Exception {
        // Given:
        class SomeType {
            private String privateField;
        }

        // When:
        final Iterable<RawField> fields = inspector.getFields(SomeType.class, inspectors);

        // Then:
        assertThat(fields, hasItem(rawField(SomeType.class, "privateField")));
    }

    @Test
    public void shouldReturnAllSuperTypeFields() throws Exception {
        // Given:
        class SuperSuperType {
            public String superSuperField;
        }

        class SuperType extends SuperSuperType {
            public double superField;
        }

        class SomeType extends SuperType {
            public long field;
        }

        when(inspectors.get(SuperSuperType.class)).thenReturn(inspector);
        when(inspectors.get(SuperType.class)).thenReturn(inspector);

        // When:
        final Iterable<RawField> fields = inspector.getFields(SomeType.class, inspectors);

        // Then:
        assertThat(fields, hasItems(
            rawField(SuperSuperType.class, "superSuperField"),
            rawField(SuperType.class, "superField"),
            rawField(SomeType.class, "field")
        ));
    }

    @Test
    public void shouldExcludeSyntheticFields() throws Exception {
        // Given:
        class TypeWithSyntheticFieldToOuterClass {
        }

        // When:
        final Iterable<RawField> fields = inspector.getFields(TypeWithSyntheticFieldToOuterClass.class, inspectors);

        // Then:
        assertThat("should not have field containing reference to outer class", fields.iterator().hasNext(), is(false));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                FieldInspector.INSTANCE,
                new FieldInspector())
            .addEqualityGroup(
                mock(Inspector.class))
            .testEquals();
    }
}