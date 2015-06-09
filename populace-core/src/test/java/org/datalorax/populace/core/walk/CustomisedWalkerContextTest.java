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

package org.datalorax.populace.core.walk;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;
import org.datalorax.populace.core.walk.element.ElementInfo;
import org.datalorax.populace.core.walk.field.FieldInfo;
import org.datalorax.populace.core.walk.inspector.Inspector;
import org.datalorax.populace.core.walk.inspector.Inspectors;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CustomisedWalkerContextTest {
    @Mock(name = "main")
    private Predicate<FieldInfo> additionalFieldFilter;
    @Mock(name = "main")
    private Predicate<ElementInfo> additionalElementFilter;
    @Mock
    private FieldInfo field;
    @Mock
    private ElementInfo element;
    @Mock
    private WalkerContext baseContext;
    @Mock
    private GraphWalker.Customisations customisations;

    private CustomisedWalkerContext context;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(customisations.getAdditionalFieldFilter()).thenReturn(Optional.empty());
        when(customisations.getAdditionalElementFilter()).thenReturn(Optional.empty());

        context = new CustomisedWalkerContext(baseContext, customisations);
    }

    @Test
    public void shouldExcludeFieldIfBaseExcludes() throws Exception {
        // Given:
        when(baseContext.isExcludedField(field)).thenReturn(true);

        // Then:
        assertThat(context.isExcludedField(field), is(true));
    }

    @Test
    public void shouldExcludeFieldIfBaseExcludesAndAdditionalInstalled() throws Exception {
        // Given:
        givenAdditionalFieldFilterInstalled();
        when(baseContext.isExcludedField(field)).thenReturn(true);

        // Then:
        assertThat(context.isExcludedField(field), is(true));
    }

    @Test
    public void shouldExcludeFieldIfAdditionalExcludes() throws Exception {
        // Given:
        givenAdditionalFieldFilterInstalled();
        when(additionalFieldFilter.test(field)).thenReturn(false);

        // Then:
        assertThat(context.isExcludedField(field), is(true));
    }

    @Test
    public void shouldIncludeFieldOnlyIfBothInclude() throws Exception {
        // Given:
        givenAdditionalFieldFilterInstalled();
        when(baseContext.isExcludedField(field)).thenReturn(false);
        when(additionalElementFilter.test(element)).thenReturn(true);

        // Then:
        assertThat(context.isExcludedElement(element), is(false));
    }

    @Test
    public void shouldExcludeElementIfBaseExcludes() throws Exception {
        // Given:
        when(baseContext.isExcludedElement(element)).thenReturn(true);

        // Then:
        assertThat(context.isExcludedElement(element), is(true));
    }

    @Test
    public void shouldExcludeElementIfBaseExcludesAndAdditionalIsInstalled() throws Exception {
        // Given:
        givenAdditionalElementFilterInstalled();
        when(baseContext.isExcludedElement(element)).thenReturn(true);

        // Then:
        assertThat(context.isExcludedElement(element), is(true));
    }

    @Test
    public void shouldExcludeElementIfAdditionalExcludes() throws Exception {
        // Given:
        givenAdditionalElementFilterInstalled();
        when(additionalElementFilter.test(element)).thenReturn(false);

        // Then:
        assertThat(context.isExcludedElement(element), is(true));
    }

    @Test
    public void shouldIncludeElementOnlyIfBothInclude() throws Exception {
        // Given:
        givenAdditionalElementFilterInstalled();
        when(baseContext.isExcludedElement(element)).thenReturn(false);
        when(additionalElementFilter.test(element)).thenReturn(true);

        // Then:
        assertThat(context.isExcludedElement(element), is(false));
    }

    @Test
    public void shouldGetInspectorFromBase() throws Exception {
        // Given:
        final Inspector inspector = mock(Inspector.class);
        when(baseContext.getInspector(String.class)).thenReturn(inspector);

        // Then:
        assertThat(context.getInspector(String.class), is(inspector));
    }

    @Test
    public void shouldGetInspectorsFromBase() throws Exception {
        // Given:
        final Inspectors inspectors = mock(Inspectors.class);
        when(baseContext.getInspectors()).thenReturn(inspectors);

        // Then:
        assertThat(context.getInspectors(), is(inspectors));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                new CustomisedWalkerContext(baseContext, customisations),
                new CustomisedWalkerContext(baseContext, customisations))
            .addEqualityGroup(
                new CustomisedWalkerContext(mock(WalkerContext.class, "other"), customisations))
            .addEqualityGroup(
                new CustomisedWalkerContext(baseContext, mock(GraphWalker.Customisations.class, "other")))
            .testEquals();
    }

    @Test
    public void shouldThrowNPEsOnConstructorParams() throws Exception {
        new NullPointerTester()
            .setDefault(WalkerContext.class, baseContext)
            .setDefault(GraphWalker.Customisations.class, customisations)
            .testAllPublicConstructors(CustomisedWalkerContext.class);
    }

    private void givenAdditionalFieldFilterInstalled() {
        when(customisations.getAdditionalFieldFilter()).thenReturn(Optional.of(additionalFieldFilter));
    }

    private void givenAdditionalElementFilterInstalled() {
        when(customisations.getAdditionalElementFilter()).thenReturn(Optional.of(additionalElementFilter));
    }
}