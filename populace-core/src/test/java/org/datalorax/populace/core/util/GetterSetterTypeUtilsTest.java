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

package org.datalorax.populace.core.util;

import org.datalorax.populace.core.util.testtypes.TypeWithGetterSetter;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.fail;

@SuppressWarnings("UnusedDeclaration")
public class GetterSetterTypeUtilsTest {
    @Test
    public void shouldNotThrowForValidGetterAndSetter() throws Exception {
        // When:
        GetterSetterTypeUtils.validateGetterSetterPair(
            getMethod(TypeWithGetterSetter.class, "getThing"),
            getMethod(TypeWithGetterSetter.class, "setThing"));

        // Then:
        // It didn't throw.
    }

    @Test
    public void shouldNotThrowForValidGetterAndSetterOnInnerClass() throws Exception {
        // Given:
        class SomeType {
            int getIt() {
                return 0;
            }

            void setIt(int value) {
            }
        }

        // When:
        GetterSetterTypeUtils.validateGetterSetterPair(
            getMethod(SomeType.class, "getIt"),
            getMethod(SomeType.class, "setIt"));

        // Then:
        // It didn't throw.
    }

    @Test
    public void shouldThrowIfGetterAndSetterAreFromUnrelatedClasses() throws Exception {
        // Given:
        class TypeOne {
            int getIt() {
                return 0;
            }
        }
        class TypeTwo {
            void setIt(int value) {
            }
        }

        try {
            // When:
            GetterSetterTypeUtils.validateGetterSetterPair(getMethod(TypeOne.class, "getIt"), getMethod(TypeTwo.class, "setIt"));

            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // Then:
            assertThat(e.getMessage(), containsString("must be on related classes"));
        }
    }

    @Test
    public void shouldIncludeGetterAndSetterDetailsInException() throws Exception {
        // Given:
        class TypeOne {
            int getIt() {
                return 0;
            }
        }
        class TypeTwo {
            void setIt(int value) {
            }
        }

        try {
            // When:
            GetterSetterTypeUtils.validateGetterSetterPair(getMethod(TypeOne.class, "getIt"), getMethod(TypeTwo.class, "setIt"));

            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // Then:
            assertThat(e.getMessage(), containsString(TypeOne.class.getName()));
            assertThat(e.getMessage(), containsString(TypeTwo.class.getName()));
            assertThat(e.getMessage(), containsString("getIt"));
            assertThat(e.getMessage(), containsString("setIt"));
        }
    }

    @Test
    public void shouldThrowIfGetterIsStatic() throws Exception {
        try {
            // When:
            GetterSetterTypeUtils.validateGetterSetterPair(
                getMethod(getClass(), "getStaticGetter"),
                getMethod(getClass(), "setStaticGetter"));

            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // Then:
            assertThat(e.getMessage(), containsString("Getter can not be static"));
        }
    }

    @Test
    public void shouldThrowIfGetterReturnsVoid() throws Exception {
        // Given:
        class SomeType {
            void getIt() {
            }

            void setIt(int v) {
            }
        }

        try {
            // When:
            GetterSetterTypeUtils.validateGetterSetterPair(
                getMethod(SomeType.class, "getIt"),
                getMethod(SomeType.class, "setIt"));

            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // Then:
            assertThat(e.getMessage(), containsString("Getter must return a value"));
        }
    }

    @Test
    public void shouldThrowIfGetterTakesParameters() throws Exception {
        // Given:
        class SomeType {
            int getIt(int v) {
                return 0;
            }

            void setIt(int v) {
            }
        }

        try {
            // When:
            GetterSetterTypeUtils.validateGetterSetterPair(
                getMethod(SomeType.class, "getIt"),
                getMethod(SomeType.class, "setIt"));

            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // Then:
            assertThat(e.getMessage(), containsString("Getter must not take parameters"));
        }
    }

    @Test
    public void shouldThrowIfSetterIsStatic() throws Exception {
        try {
            // When:
            GetterSetterTypeUtils.validateGetterSetterPair(
                getMethod(getClass(), "getStaticSetter"),
                getMethod(getClass(), "setStaticSetter"));

            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // Then:
            assertThat(e.getMessage(), containsString("Setter can not be static"));
        }
    }

    @Test
    public void shouldThrowIfSetterTakesNoParameters() throws Exception {
        // Given:
        class SomeType {
            int getIt() {
                return 0;
            }

            void setIt() {
            }
        }

        try {
            // When:
            GetterSetterTypeUtils.validateGetterSetterPair(
                getMethod(SomeType.class, "getIt"),
                getMethod(SomeType.class, "setIt"));

            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // Then:
            assertThat(e.getMessage(), containsString("Setter must take only a single parameter"));
        }
    }

    @Test
    public void shouldThrowIfSetterTakesMoreThanOneParameter() throws Exception {
        // Given:
        class SomeType {
            int getIt() {
                return 0;
            }

            void setIt(int v, int v2) {
            }
        }

        try {
            // When:
            GetterSetterTypeUtils.validateGetterSetterPair(
                getMethod(SomeType.class, "getIt"),
                getMethod(SomeType.class, "setIt"));

            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // Then:
            assertThat(e.getMessage(), containsString("Setter must take only a single parameter"));
        }
    }

    @Test
    public void shouldThrowIfGetterReturnValueDoesNotMatchSetterParam() throws Exception {
        // Given:
        class SomeType {
            int getIt() {
                return 0;
            }

            void setIt(Integer v) {
            }
        }

        try {
            // When:
            GetterSetterTypeUtils.validateGetterSetterPair(
                getMethod(SomeType.class, "getIt"),
                getMethod(SomeType.class, "setIt"));

            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // Then:
            assertThat(e.getMessage(), containsString("getter return type must match setter param type"));
        }
    }

    @Test
    public void shouldThrowIfGetterGenericReturnTypeDoesNotMatchSetterGenericParam() throws Exception {
        // Given:
        class SomeType {
            List<Integer> getIt() {
                return null;
            }

            void setIt(List<Number> v) {
            }
        }

        try {
            // When:
            GetterSetterTypeUtils.validateGetterSetterPair(
                getMethod(SomeType.class, "getIt"),
                getMethod(SomeType.class, "setIt"));

            fail("exception expected");
        } catch (IllegalArgumentException e) {
            // Then:
            assertThat(e.getMessage(), containsString("getter return type must match setter param type"));
        }
    }

    @Test
    public void shouldDetectValidAndInvalidGetter() throws Exception {
        // Given:
        class SomeType {
            int getValid() {
                return 0;
            }

            void getInvalid() {
            }
        }

        // Then:
        assertThat(GetterSetterTypeUtils.isValidGetter(getMethod(SomeType.class, "getValid")), is(true));
        assertThat(GetterSetterTypeUtils.isValidGetter(getMethod(SomeType.class, "getInvalid")), is(false));
    }

    @Test
    public void shouldDetectValidAndInvalidSetter() throws Exception {
        // Given:
        class SomeType {
            void setInvalid() {
            }

            void setValid(int v) {
            }
        }

        // Then:
        assertThat(GetterSetterTypeUtils.isValidSetter(getMethod(SomeType.class, "setValid")), is(true));
        assertThat(GetterSetterTypeUtils.isValidSetter(getMethod(SomeType.class, "setInvalid")), is(false));
    }

    @Test
    public void shouldNotThrowOnValidGetter() throws Exception {
        // Given:
        class SomeType {
            int getIt() {
                return 0;
            }
        }

        // When:
        GetterSetterTypeUtils.validateGetter(getMethod(SomeType.class, "getIt"));

        // Then:
        // It didn't throw
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnInvalidGetter() throws Exception {
        // Given:
        class SomeType {
            void getIt() {
            }
        }

        // When:
        GetterSetterTypeUtils.validateGetter(getMethod(SomeType.class, "getIt"));
    }

    @Test
    public void shouldNotThrowOnValidSetter() throws Exception {
        // Given:
        class SomeType {
            void setIt(int v) {
            }
        }

        // When:
        GetterSetterTypeUtils.validateSetter(getMethod(SomeType.class, "setIt"));

        // Then:
        // It didn't throw
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnInvalidSetter() throws Exception {
        // Given:
        class SomeType {
            void setIt() {
            }
        }

        // When:
        GetterSetterTypeUtils.validateSetter(getMethod(SomeType.class, "setIt"));
    }

    // Getter & Setters used in tests:
    public void setStaticGetter() {
    }

    public long getStaticSetter() {
        return 0;
    }

    private Method getMethod(final Class<?> type, final String methodName) {
        return Arrays.stream(type.getDeclaredMethods())
            .filter(m -> m.getName().equals(methodName))
            .findAny().get();
    }

    private static long getStaticGetter() {
        return 0;
    }

    private static void setStaticSetter() {
    }
}