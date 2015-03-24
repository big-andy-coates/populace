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

import org.apache.commons.lang3.Validate;

import java.math.BigDecimal;

/**
 * Instance factory for {@link java.math.BigDecimal BigDecimals}.
 *
 * The main issue with BigDecimals tends to be systems losing precision when serialising an de-serialising them. Hence
 * the {@link BigDecimalInstanceFactory#LARGE_INSTANCE} instance of the factory stores a decimal that can't be stored
 * in a {@code double} without losing precision
 *
 * @author Andrew Coates - 09/03/2015.
 */
public class BigDecimalInstanceFactory implements InstanceFactory {
    public static final BigDecimal BIG_DECIMAL_TO_LARGE_FOR_DOUBLE = new BigDecimal("1")
        .setScale(10, BigDecimal.ROUND_HALF_EVEN)
        .divide(new BigDecimal("3"), BigDecimal.ROUND_HALF_EVEN);

    public static final InstanceFactory LARGE_INSTANCE = new BigDecimalInstanceFactory(BIG_DECIMAL_TO_LARGE_FOR_DOUBLE);

    private final BigDecimal defaultValue;

    public BigDecimalInstanceFactory(final BigDecimal defaultValue) {
        Validate.notNull(defaultValue, "defaultValue null");
        this.defaultValue = defaultValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T createInstance(Class<? extends T> rawType, final Object parent, final InstanceFactories instanceFactories) {
        Validate.isTrue(rawType.equals(BigDecimal.class), "BigDecimal type expected. Got: %s", rawType);
        return (T) defaultValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final BigDecimalInstanceFactory that = (BigDecimalInstanceFactory) o;
        return defaultValue.equals(that.defaultValue);
    }

    @Override
    public int hashCode() {
        return defaultValue.hashCode();
    }

    @Override
    public String toString() {
        return "BigDecimalInstanceFactory{" +
            "defaultValue=" + defaultValue +
            '}';
    }
}
