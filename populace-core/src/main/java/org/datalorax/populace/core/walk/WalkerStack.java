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

package org.datalorax.populace.core.walk;

import com.google.common.reflect.TypeToken;
import org.datalorax.populace.core.util.TypeResolver;
import org.datalorax.populace.core.walk.field.PathProvider;
import org.datalorax.populace.core.walk.field.RawField;
import org.datalorax.populace.core.walk.field.TypeTable;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Andrew Coates - 04/03/2015.
 */
public abstract class WalkerStack implements PathProvider, TypeTable {
    private final WalkerStack parent;
    private final int depth;

    private WalkerStack() {
        this.parent = null;
        this.depth = 0;
    }

    private WalkerStack(final WalkerStack parent) {
        this.parent = parent;
        this.depth = parent.getDepth() + 1;
    }

    public static WalkerStack newStack(final Object root) {
        return new RootFrame(root);
    }

    public WalkerStack push(final RawField field) {
        return new FieldFrame(this, field);
    }

    public WalkerStack push(final Object element, final int index) {
        return new ElementFrame(this, element, index);
    }

    public String getPath() {
        final StringBuilder builder = new StringBuilder();
        getFrames().forEach(frame -> builder.append(frame.getToken()));
        return builder.toString();
    }

    @Override
    public int getDepth() {
        return depth;
    }

    public abstract Type resolveTypeVariable(final TypeVariable variable);

    public TypeResolver getTypeResolver() {
        return new TypeResolver(this);
    }

    protected WalkerStack getParent() { return parent; }

    protected abstract String getToken();

    private List<WalkerStack> getFrames() {
        final List<WalkerStack> frames = new ArrayList<>();
        forEachParent(frame -> frames.add(0, frame));
        return frames;
    }

    private void forEachParent(Consumer<WalkerStack> action) {
        for (WalkerStack frame = this; frame != null; frame = frame.parent) {
            action.accept(frame);
        }
    }

    private static final class RootFrame extends WalkerStack {
        private final Object root;

        public RootFrame(final Object root) {
            this.root = root;
        }

        @Override
        public Type resolveTypeVariable(final TypeVariable variable) {
            return variable;    // Not enough type information to resolve
        }

        @Override
        protected String getToken() {
            return root.getClass().getSimpleName();
        }
    }

    private static final class FieldFrame extends WalkerStack {
        private final RawField field;
        private final TypeToken<?> type;

        public FieldFrame(final WalkerStack parent, final RawField field) {
            super(parent);
            this.field = field;
            this.type = TypeToken.of(field.getGenericType());
        }

        @Override
        public Type resolveTypeVariable(final TypeVariable variable) {
            final TypeToken<?> typeToken = type.resolveType(variable);
            if (typeToken.getType().equals(variable)) {
                return getParent().resolveTypeVariable(variable);
            }

            return typeToken.getType();
        }

        @Override
        protected String getToken() {
            return '.' + field.getName();
        }
    }

    private static final class ElementFrame extends WalkerStack {
        private final int index;

        @SuppressWarnings("UnusedParameters")   // For future use
        public ElementFrame(final WalkerStack parent, final Object element, final int index) {
            super(parent);
            this.index = index;
        }

        @Override
        public Type resolveTypeVariable(final TypeVariable variable) {
            return getParent().resolveTypeVariable(variable);
        }

        @Override
        protected String getToken() {
            return "[" + index + "]";
        }
    }
}
