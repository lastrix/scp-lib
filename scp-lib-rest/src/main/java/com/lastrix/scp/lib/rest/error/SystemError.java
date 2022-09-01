package com.lastrix.scp.lib.rest.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ErrorEnum
@Getter
@RequiredArgsConstructor
public enum SystemError implements ServiceError {
    GENERIC("SYS_0000", "Generic error"),
    UNSUPPORTED("SYS_0001", "Unsupported"),
    NOT_ALLOWED("SYS_0002", "Not allowed"),

    NOT_FOUND("SYS_1000", "Not found"),
    NO_ACCESS("SYS_1001", "No access"),

    BAD_REQUEST("SYS_2000", "Bad request"),
    EVENT_SEND_FAILED("SYS_3000", "Failed to send event to kafka"),

    CONSTRAINT_VIOLATION("SYS_4000", "Constraint violation"),
    COULD_NOT_EXECUTE_STATEMENT("SYS_4001", "Could not execute statement"),

    NOT_VALID("SYS_5000", "Validation failed"),
    NO_DATA("SYS_5001", "No data"),
    MALFORMED("SYS_5002", "Malformed data sent"),

    AUTH_FAILED("SYS_6000", "Authorization failed"),

    NO_DB_CONNECTION("SYS_9000", "No database connection"),
    HTTP_FAILURE("SYS_9001", "Http failure"),
    ;

    private final String id;
    private final String description;
}
