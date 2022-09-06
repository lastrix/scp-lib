package com.lastrix.scp.lib.rest.error.impl;

import com.lastrix.scp.lib.rest.error.ErrorConverter;
import com.lastrix.scp.lib.rest.error.ErrorObject;
import com.lastrix.scp.lib.rest.error.ServiceErrorException;

public final class ServiceErrorExceptionConverter implements ErrorConverter<ServiceErrorException> {
    @Override
    public ErrorObject toErrorObject(ServiceErrorException ex) {
        return ex.asErrorObject();
    }
}
