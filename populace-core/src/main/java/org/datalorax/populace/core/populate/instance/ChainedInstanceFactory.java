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

/**
 * Instance factory that chains to other factories together. The second factory will only be called should the first
 * return null.
 *
 * @author Andrew Coates - 03/03/2015.
 */
public final class ChainedInstanceFactory implements InstanceFactory {
    private final InstanceFactory first;
    private final InstanceFactory second;

    public ChainedInstanceFactory(final InstanceFactory first, final InstanceFactory second) {
        Validate.notNull(first, "first null");
        Validate.notNull(second, "second null");
        this.first = first;
        this.second = second;
    }

    public static InstanceFactory chain(final InstanceFactory first, final InstanceFactory second, final InstanceFactory... additional) {
        ChainedInstanceFactory chain = new ChainedInstanceFactory(first, second);
        for (InstanceFactory factory : additional) {
            chain = new ChainedInstanceFactory(chain, factory);
        }
        return chain;
    }

    @Override
    public <T> T createInstance(final Class<? extends T> rawType, final Object parent, final InstanceFactories instanceFactories) {
        final T instance = first.createInstance(rawType, parent, instanceFactories);
        return instance == null ? second.createInstance(rawType, parent, instanceFactories) : instance;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ChainedInstanceFactory that = (ChainedInstanceFactory) o;
        return first.equals(that.first) && second.equals(that.second);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ChainedInstanceFactory{" +
            "first=" + first +
            ", second=" + second +
            '}';
    }
}

// Todo(ac): If factories can return null, to support chaining, then how about specific factory returning null and falling through to super or package, or defautl...?
