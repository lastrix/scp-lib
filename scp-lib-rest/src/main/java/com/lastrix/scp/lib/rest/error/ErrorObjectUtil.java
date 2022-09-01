package com.lastrix.scp.lib.rest.error;

import com.lastrix.scp.lib.reflect.ClassRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorObjectUtil {
    public static ServiceErrorException buildFor(ErrorObject eo) {
        Set<Class<?>> enums = ClassRegistry.getAnnotatedClasses(ErrorEnum.class);
        for (Class<?> anEnum : enums) {
            ServiceError error = getError(anEnum, eo.getCode());
            if (error == null) {
                continue;
            }
            return ServiceErrorException.forError(error)
                    .params(eo.getParams())
                    .disableStackTrace()
                    .build();
        }
        return null;
    }

    private static ServiceError getError(Class<?> anEnum, String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        return ErrorRegistry.find(anEnum, code);
    }

    public static String formatStackTrace(Throwable throwable) {
        return Arrays.stream(throwable.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    public static List<String> stackTrace2List(Throwable throwable) {
        return Arrays.stream(throwable.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
    }
}
