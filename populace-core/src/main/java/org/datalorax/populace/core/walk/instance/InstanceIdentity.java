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

import org.apache.commons.lang3.Validate;

/**
 * Wrapper around any object with an implementation of {@link #equals} and {@link #hashCode()} that work in the same way
 * as the {@link java.lang.Object Object class}'s versions do i.e. using reference equality and the hash of the object
 * reference.
 * <p>
 * This class can be used to store objects within a {@link java.util.Set}, or similar, using reference equality not
 * object equality. i.e. two distinct instances where {@code instance1.equals(instance2)} returns true, can be stored in
 * the set as separate elements.
 *
 * @author Andrew Coates - 29/04/2015.
 */
public class InstanceIdentity {
    private final Object instance;

    public InstanceIdentity(final Object instance) {
        Validate.notNull(instance, "instance null");
        this.instance = instance;
    }

    public Object get() {
        return instance;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final InstanceIdentity that = (InstanceIdentity) o;
        return instance == that.instance;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(instance);
    }

    @Override
    public String toString() {
        return instance.toString();
    }
}
