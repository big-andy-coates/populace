package org.datalorax.populace.populator.typed;

import org.apache.commons.lang3.Validate;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * The builder of typed collection
 * @author datalorax - 28/02/2015.
 */
public class TypedCollectionBuilder<T> implements TypedCollection.Builder<T> {
    private T defaultArrayHandler = null;
    private T defaultHandler = null;
    private final Map<Type, T> specificHandlers = new HashMap<Type, T>();
    private final Map<Class<?>, T> baseHandlers = new HashMap<Class<?>, T>();

    @Override
    public TypedCollectionBuilder<T> withSpecificTypes(final Map<Type, ? extends T> handlers) {
        Validate.notNull(handlers, "handlers null");
        for (Map.Entry<Type, ? extends T> entry : handlers.entrySet()) {
            withSpecificType(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public TypedCollectionBuilder<T> withSpecificType(final Type type, final T handler) {
        Validate.notNull(type, "type null");
        Validate.notNull(handler, "handler null");
        specificHandlers.put(type, handler);
        return this;
    }

    @Override
    public TypedCollection.Builder<T> withSuperTypes(final Map<Class<?>, ? extends T> handlers) {
        Validate.notNull(handlers, "handlers null");
        for (Map.Entry<Class<?>, ? extends T> entry : handlers.entrySet()) {
            withSuperType(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public TypedCollectionBuilder<T> withSuperType(final Class<?> baseClass, final T handler) {
        Validate.notNull(baseClass, "baseClass null");
        Validate.notNull(handler, "handler null");
        baseHandlers.put(baseClass, handler);
        return this;
    }

    @Override
    public TypedCollectionBuilder<T> withDefaultArray(final T handler) {
        Validate.notNull(handler, "handler null");
        defaultArrayHandler = handler;
        return this;
    }

    @Override
    public TypedCollectionBuilder<T> withDefault(final T handler) {
        Validate.notNull(handler, "handler null");
        defaultHandler = handler;
        return this;
    }

    @Override
    public TypedCollection<T> build() {
        return new TypedCollection<T>(specificHandlers, baseHandlers, defaultHandler, defaultArrayHandler);
    }
}