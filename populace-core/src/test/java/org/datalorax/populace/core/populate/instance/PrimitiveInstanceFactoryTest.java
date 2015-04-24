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

package org.datalorax.populace.core.populate.instance;

import com.google.common.testing.EqualsTester;
import org.datalorax.populace.core.util.TypeUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

public class PrimitiveInstanceFactoryTest {
    @Test(dataProvider = "primitive")
    public void shouldSupportPrimitiveType(Class<?> type) throws Exception {
        // Given:
        final Class<?> boxed = TypeUtils.getBoxedTypeForPrimitive(type);
        // When:
        final Object result = PrimitiveInstanceFactory.INSTANCE.createInstance(type, null, null);

        // Then:
        assertThat(result, is(notNullValue()));
        assertThat(result, is(instanceOf(boxed)));
    }

    @Test(dataProvider = "boxed")
    public void shouldSupportBoxedPrimitiveType(Class<?> type) throws Exception {
        // When:
        final Object result = PrimitiveInstanceFactory.INSTANCE.createInstance(type, null, null);

        // Then:
        assertThat(result, is(notNullValue()));
        assertThat(result, is(instanceOf(type)));
    }

    @Test
    public void shouldReturnNullForNonPrimitiveTypes() throws Exception {
        assertThat(PrimitiveInstanceFactory.INSTANCE.createInstance(String.class, null, null), is(nullValue()));
    }

    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        new EqualsTester()
            .addEqualityGroup(
                PrimitiveInstanceFactory.INSTANCE,
                new PrimitiveInstanceFactory())
            .addEqualityGroup(
                mock(InstanceFactory.class))
            .testEquals();
    }

    @DataProvider(name = "primitive")
    public Object[][] getPrimitives() {
        return asObjectArray(TypeUtils.getPrimitiveTypes());
    }

    @DataProvider(name = "boxed")
    public Object[][] getBoxedPrimitives() {
        return asObjectArray(TypeUtils.getBoxedPrimitiveTypes());
    }

    private static Object[][] asObjectArray(final List<Class<?>> types) {
        final Object[][] data = new Object[types.size()][];
        int i = 0;
        for (Class<?> type : types) {
            data[i++] = new Object[]{type};
        }
        return data;
    }
}