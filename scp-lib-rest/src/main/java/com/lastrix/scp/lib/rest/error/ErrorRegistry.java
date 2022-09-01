package com.lastrix.scp.lib.rest.error;

import com.lastrix.scp.lib.reflect.ClassRegistry;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class ErrorRegistry {
    private static final ErrorRegistry INSTANCE = new ErrorRegistry();

    public static ServiceError find(Class<?> enumClass, String code) {
        return INSTANCE.errors.getOrDefault(enumClass, Collections.emptyMap())
                .get(code);
    }

    private final Map<Class<?>, Map<String, ServiceError>> errors = new HashMap<>();

    public ErrorRegistry() {
        ClassRegistry.getAnnotatedClasses(ErrorEnum.class)
                .forEach(this::processErrorEnumClass);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void processErrorEnumClass(Class<?> aClass) {
        try {
            Enum[] values = getEnumValues((Class<Enum>) aClass);
            var m = errors.computeIfAbsent(aClass, ignored -> new HashMap<>());
            for (Enum value : values) {
                if (!(value instanceof ServiceError)) {
                    throw new IllegalStateException("Not an ServiceError: " + value);
                }
                ServiceError e = (ServiceError) value;
                m.put(e.getId(), e);
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            log.error("Failed to read values from class: {}", aClass.getTypeName());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <E extends Enum> E[] getEnumValues(Class<E> enumClass)
            throws NoSuchFieldException, IllegalAccessException {
        Field f = enumClass.getDeclaredField("$VALUES");
        f.setAccessible(true);
        Object o = f.get(null);
        return (E[]) o;
    }
}
