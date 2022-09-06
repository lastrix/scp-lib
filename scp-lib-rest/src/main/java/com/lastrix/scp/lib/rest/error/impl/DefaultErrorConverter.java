package com.lastrix.scp.lib.rest.error.impl;

import com.lastrix.scp.lib.rest.error.ErrorConverter;
import com.lastrix.scp.lib.rest.error.ErrorObject;
import com.lastrix.scp.lib.rest.error.ErrorObjectUtil;
import com.lastrix.scp.lib.rest.error.SystemError;

import java.util.UUID;

public class DefaultErrorConverter implements ErrorConverter<Throwable> {
    @Override
    public ErrorObject toErrorObject(Throwable ex) {
        return ErrorObject.builder()
                .id(UUID.randomUUID().toString())
                .code(SystemError.GENERIC.getId())
                .description(SystemError.GENERIC.getDescription())
                .meta(ex.getMessage())
                .stackTrace(ErrorObjectUtil.stackTrace2List(ex))
                .build();
    }
}
