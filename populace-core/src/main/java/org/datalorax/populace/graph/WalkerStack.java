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

package org.datalorax.populace.graph;

import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Andrew Coates - 04/03/2015.
 */
public abstract class WalkerStack {
    private final WalkerStack parent;

    public static WalkerStack newStack(final Object root) {
        return new RootFrame(root);
    }

    public WalkerStack push(final Field field) {
        return new FieldFrame(this, field);
    }

    public WalkerStack push(final Object element) {
        return new ElementFrame(this, element);
    }

    private WalkerStack() {
        this.parent = null;
    }

    private WalkerStack(final WalkerStack parent) {
        this.parent = parent;
    }

    public String getPath() {
        final StringBuilder builder = new StringBuilder();
        getFrames().forEach(frame -> builder.append(frame.getToken()));
        return builder.toString();
    }

    public Type resolveType(final Type genericType) {
        if (genericType instanceof Class) {
            return genericType;
        }

        if (genericType instanceof ParameterizedType) {
            final Type[] typeArgs = ((ParameterizedType) genericType).getActualTypeArguments();
            for (int i = 0; i != typeArgs.length; ++i) {
                typeArgs[i] = resolveType(typeArgs[i]);
            }

            return TypeUtils.parameterize((Class)((ParameterizedType) genericType).getRawType(), typeArgs);
        }

        if (genericType instanceof TypeVariable) {
            return resolveTypeVariable((TypeVariable)genericType);
        }

        return null;
    }

    protected WalkerStack getParent() { return parent; }

    protected abstract String getToken();

    protected abstract Type resolveTypeVariable(final TypeVariable variable);

    private List<WalkerStack> getFrames() {
        final List<WalkerStack> frames = new ArrayList<>();
        forEachParent(frame -> frames.add(0, frame));
        return frames;
    }

    private static final class RootFrame extends WalkerStack {
        private final Object root;

        public RootFrame(final Object root) {
            this.root = root;
        }

        @Override
        protected String getToken() {
            return "root";
        }

        @Override
        protected Type resolveTypeVariable(final TypeVariable variable) {
            return Object.class;    // Epic failure!
        }
    }

    private static final class FieldFrame extends WalkerStack {
        private final Field field;

        public FieldFrame(final WalkerStack parent, final Field field) {
            super(parent);
            this.field = field;
        }

        @Override
        protected String getToken() {
            return '.' + field.getName();
        }

        @Override
        protected Type resolveTypeVariable(final TypeVariable variable) {
            final Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType)genericType;
                final Type[] typeArgs = parameterizedType.getActualTypeArguments();
                final TypeVariable<? extends Class<?>>[] typeVars = ((Class<?>) parameterizedType.getRawType()).getTypeParameters();

                for (int i = 0; i != typeArgs.length; ++i) {
                    if (typeVars[i].equals(variable)) {
                        return resolveType(typeArgs[i]);
                    }
                }
            }
            return getParent().resolveTypeVariable(variable);
        }
    }

    private static final class ElementFrame extends WalkerStack {
        private final Object element;

        public ElementFrame(final WalkerStack parent, final Object element) {
            super(parent);
            this.element = element;
        }

        @Override
        protected String getToken() {
            return "[" + element.getClass().getSimpleName() + "]";
        }

        @Override
        protected Type resolveTypeVariable(final TypeVariable variable) {
            return getParent().resolveTypeVariable(variable);
        }
    }

    private void forEachParent(Consumer<WalkerStack> action) {
        for (WalkerStack frame = this; frame != null; frame = frame.parent) {
            action.accept(frame);
        }
    }
}
