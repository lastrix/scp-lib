package com.lastrix.scp.lib.rest.error;

@FunctionalInterface
public interface ErrorConverter<E extends Throwable> {
    ErrorObject toErrorObject(E e);
}
