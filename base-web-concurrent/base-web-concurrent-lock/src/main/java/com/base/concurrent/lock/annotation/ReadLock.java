package com.base.concurrent.lock.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by   on 16-5-13.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReadLock {
    String value() default "";

    String condition() default "";

    long waitTime() default 5;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    boolean autoUnLock() default true;
}
