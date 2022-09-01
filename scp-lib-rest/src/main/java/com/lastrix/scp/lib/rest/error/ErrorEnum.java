package com.lastrix.scp.lib.rest.error;

import com.lastrix.scp.lib.reflect.Reflected;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Reflected
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ErrorEnum {
}
