package com.debug.fresh.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// com/example/project/annotation/SaCheckDeviceLimit.java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SaCheckDeviceLimit {
    int value() default 3; // 默认允许3个设备
}