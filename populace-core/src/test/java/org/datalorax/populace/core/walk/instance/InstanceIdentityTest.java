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

package org.datalorax.populace.core.walk.instance;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;
import org.testng.annotations.Test;


public class InstanceIdentityTest {
    @Test
    public void shouldTestEqualsAndHashCode() throws Exception {
        // Given:
        final Object object = new SomeType("value");
        new EqualsTester()
            .addEqualityGroup(
                new SomeType("value"),
                new SomeType("value"))
            .addEqualityGroup(new SomeType("other value"))
            .testEquals();

        // Then:
        new EqualsTester()
            .addEqualityGroup(
                new InstanceIdentity(object),
                new InstanceIdentity(object))
            .addEqualityGroup(
                new InstanceIdentity(new SomeType("value")))
            .testEquals();
    }

    @Test
    public void shouldThrowNPEsOnConstructorParams() throws Exception {
        new NullPointerTester()
            .testAllPublicConstructors(InstanceIdentity.class);
    }

    private static final class SomeType {
        private final String field;

        private SomeType(final String value) {
            field = value;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final SomeType someType = (SomeType) o;
            return field.equals(someType.field);
        }

        @Override
        public int hashCode() {
            return field.hashCode();
        }

        @Override
        public String toString() {
            return "SomeType{" +
                "field='" + field + '\'' +
                '}';
        }
    }
}