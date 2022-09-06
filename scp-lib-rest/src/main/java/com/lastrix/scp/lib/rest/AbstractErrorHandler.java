package com.lastrix.scp.lib.rest;

import com.lastrix.scp.lib.rest.error.ErrorConverters;
import com.lastrix.scp.lib.rest.error.ErrorObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public abstract class AbstractErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(AbstractErrorHandler.class);

    @Value("${scp.debug:false}")
    private Boolean debugEnabled;

    @ExceptionHandler
    public ResponseEntity<Rest<Void>> handleThrowable(HttpServletRequest request, Throwable e) {
        log.error("from '{}' -> {} {}",
                StringUtils.isEmpty(request.getRemoteHost()) ? request.getRemoteAddr() : request.getRemoteHost(),
                request.getMethod(), request.getRequestURL(), e);
        ErrorObject error = ErrorConverters.asErrorObject(e);
        if (!debugEnabled) {
            hideSensitive(error);
        }
        return Rest.error(error, HttpStatus.OK);
    }

    private void hideSensitive(ErrorObject error) {
        if (error.getMeta() != null) {
            error.setMeta("<hidden>");
        }
        if (error.getStackTrace() != null && !error.getStackTrace().isEmpty()) {
            error.setStackTrace(List.of("<hidden>"));
        }
    }
}
