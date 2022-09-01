package com.lastrix.scp.lib.rest.error;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class ServiceErrorException extends RuntimeException {

    public ServiceErrorException(ServiceError error, Map<String, String> params) {
        this.error = error;
        this.params = params == null ? new HashMap<>() : new HashMap<>(params);
    }

    public ServiceErrorException(String message, Map<String, String> params, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ServiceError error) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.error = error;
        this.params = params == null ? new HashMap<>() : new HashMap<>(params);
    }

    public ServiceErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ServiceError error) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.error = error;
        this.params = new HashMap<>();
    }

    private final ServiceError error;
    private final Map<String, String> params;

    public void param(String key, Object value) {
        if (params.containsKey(key)) {
            log.error("Unable to overwrite existing param: '{}', ignoring new value: '{}'", key, value);
        } else {
            params.put(key, String.valueOf(value));
        }
    }

    public ErrorObject asErrorObject() {
        return ErrorObject.builder()
                .id(UUID.randomUUID().toString())
                .code(error.getId())
                .description(error.getDescription())
                // print cause error message, since our own is generated
                .meta(getCause() == null ? null : getCause().getMessage())
                .stackTrace(ErrorObjectUtil.stackTrace2List(this))
                .params(params == null ? Collections.emptyMap() : new HashMap<>(params))
                .build();
    }

    public static Builder generic() {
        return forError(SystemError.GENERIC);
    }

    public static Builder forError(ServiceError error) {
        return new Builder(error);
    }

    public static ServiceErrorException notFound(String entityName, Object id) {
        return ServiceErrorException.forError(SystemError.NOT_FOUND)
                .param("entity", entityName)
                .param("id", id)
                .disableStackTrace()
                .build();
    }

    public static ServiceErrorException notSupported(String paramName, String value) {
        return ServiceErrorException.forError(SystemError.UNSUPPORTED)
                .param("param", paramName)
                .param("value", value)
                .disableStackTrace()
                .build();
    }

    public static Builder notFound(String entityName) {
        return ServiceErrorException.forError(SystemError.NOT_FOUND)
                .param("entity", entityName);
    }

    public static Builder badRequest() {
        return ServiceErrorException.forError(SystemError.BAD_REQUEST);
    }

    public static void throwNotFound(String entityName, Object id) {
        throw notFound(entityName, id);
    }

    public static final class Builder {
        public Builder(ServiceError error) {
            this.error = error;
        }

        private final ServiceError error;
        private Throwable cause;
        private boolean writableStackTrace = true;
        private boolean enableSuppression = false;
        private final Map<String, String> params = new HashMap<>();

        public Builder disableStackTrace() {
            writableStackTrace = false;
            return this;
        }

        public Builder enableSuppression() {
            enableSuppression = true;
            return this;
        }

        public Builder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        public Builder param(String key, Object value) {
            params.put(key, String.valueOf(value));
            return this;
        }

        public Builder params(Map<String, String> params) {
            if (params != null) {
                this.params.putAll(params);
            }
            return this;
        }

        public ServiceErrorException build() {
            var msg = error.getId() + ": " + error.getDescription() + (params.isEmpty() ? "" : System.lineSeparator() + params.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(System.lineSeparator())));
            return new ServiceErrorException(msg, params, cause, enableSuppression, writableStackTrace, error);
        }

        public void buildAndThrow() {
            throw build();
        }
    }
}
