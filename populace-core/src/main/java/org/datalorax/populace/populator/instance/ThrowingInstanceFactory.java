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

package org.datalorax.populace.populator.instance;

/**
 * Instance factory that throws an UnsupportedOperationException.
 * @author Andrew Coates - 02/03/2015.
 */
public class ThrowingInstanceFactory implements InstanceFactory {
    public static final ThrowingInstanceFactory INSTANCE = new ThrowingInstanceFactory();

    @Override
    public <T> T createInstance(Class<? extends T> rawType, final Object parent, final InstanceFactories instanceFactories) {
        throw new UnsupportedOperationException("Unsupported type: " + rawType);
    }

    @Override
    public boolean equals(final Object that) {
        return this == that || (that != null && getClass() == that.getClass());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}

// Todo(ac): Add loggingNullObjectStrategy : extends NullObjectStratgey : extends InstanceFactory