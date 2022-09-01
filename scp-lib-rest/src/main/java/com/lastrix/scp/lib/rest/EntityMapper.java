package com.lastrix.scp.lib.rest;

public interface EntityMapper<E, D> {
    E fromDto(D dto);

    D toDto(E entity);
}
