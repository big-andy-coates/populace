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
 * Instance factory that chains to other factories together.
 *
 * @author Andrew Coates - 03/03/2015.
 */
public final class ChainedInstanceFactory {
    public static ChainableInstanceFactory chain(final ChainableInstanceFactory first, final ChainableInstanceFactory second){
        return new ChainableChainedInstanceFactory(first, second);
    }

    public static InstanceFactory chain(final ChainableInstanceFactory first, final InstanceFactory second){
        return new TeminatingChainedInstanceFactory(first, second);
    }

    public static class TeminatingChainedInstanceFactory implements InstanceFactory {
        private final ChainableInstanceFactory first;
        private final InstanceFactory second;

        public TeminatingChainedInstanceFactory(final ChainableInstanceFactory first, final InstanceFactory second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public <T> T createInstance(final Class<? extends T> rawType, final Object parent) {
            if (first.supportsType(rawType)) {
                return first.createInstance(rawType, parent);
            }
            return second.createInstance(rawType, parent);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final TeminatingChainedInstanceFactory that = (TeminatingChainedInstanceFactory) o;
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
            return "TeminatingChainedInstanceFactory{" +
                "first=" + first +
                ", second=" + second +
                '}';
        }
    }

    public static class ChainableChainedInstanceFactory implements ChainableInstanceFactory {
        private final ChainableInstanceFactory first;
        private final ChainableInstanceFactory second;

        public ChainableChainedInstanceFactory(final ChainableInstanceFactory first, final ChainableInstanceFactory second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean supportsType(final Class<?> rawType) {
            return first.supportsType(rawType) || second.supportsType(rawType);
        }

        @Override
        public <T> T createInstance(final Class<? extends T> rawType, final Object parent) {
            if (first.supportsType(rawType)) {
                return first.createInstance(rawType, parent);
            }
            return second.createInstance(rawType, parent);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final ChainableChainedInstanceFactory that = (ChainableChainedInstanceFactory) o;

            if (!first.equals(that.first)) return false;
            if (!second.equals(that.second)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = first.hashCode();
            result = 31 * result + second.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "ChainableChainedInstanceFactory{" +
                "first=" + first +
                ", second=" + second +
                '}';
        }
    }

    private ChainedInstanceFactory() {}
}
