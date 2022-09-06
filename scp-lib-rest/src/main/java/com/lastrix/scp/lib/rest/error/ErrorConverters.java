package com.lastrix.scp.lib.rest.error;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public final class ErrorConverters {
    private static final Map<Class<?>, ErrorConverter<Throwable>> CONVERTERS = createConverters();

    @SuppressWarnings("unchecked")
    private static Map<Class<?>, ErrorConverter<Throwable>> createConverters() {
        HashMap<Class<?>, ErrorConverter<Throwable>> map = new HashMap<>();
        ServiceLoader.load(ErrorConverter.class)
                .forEach(c -> map.put(resolveExceptionType(c), c));
        return map;
    }

    private static Class<?> resolveExceptionType(ErrorConverter<?> c) {
        for (Type type : c.getClass().getGenericInterfaces()) {
            if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType().equals(ErrorConverter.class)) {
                return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
            }
        }
        throw new IllegalStateException("Unable to determine handled error type for class: " + c.getClass().getTypeName());
    }

    public static ErrorObject asErrorObject(Throwable e) {
        return tryConvertRecursive(e, e.getClass());
    }

    private static ErrorObject tryConvertRecursive(Throwable e, Class<?> aClass) {
        var c = CONVERTERS.get(aClass);
        if (c != null) {
            var o = c.toErrorObject(e);
            if (o != null) {
                return o;
            }
        }
        return tryConvertRecursive(e, aClass.getSuperclass());
    }
}
