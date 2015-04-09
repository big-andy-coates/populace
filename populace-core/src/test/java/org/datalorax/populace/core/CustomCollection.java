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

package org.datalorax.populace.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Andrew Coates - 30/03/2015.
 */
@SuppressWarnings("NullableProblems")
public class CustomCollection<ET> implements Collection<ET> {
    private final List<ET> elements = new ArrayList<>();

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return elements.contains(o);
    }

    @Override
    public Iterator<ET> iterator() {
        return elements.iterator();
    }

    @Override
    public Object[] toArray() {
        return elements.toArray();
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    public <T> T[] toArray(final T[] a) {
        return elements.toArray(a);
    }

    @Override
    public boolean add(final ET ET) {
        return elements.add(ET);
    }

    @Override
    public boolean remove(final Object o) {
        return elements.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return elements.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends ET> c) {
        return elements.addAll(c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return elements.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return elements.retainAll(c);
    }

    @Override
    public void clear() {
        elements.clear();
    }
}
