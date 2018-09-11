package com.base.concurrent.lock.annotation;

import java.lang.annotation.*;

/**
 * Created by   on 16-5-13.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UnReadLock {

}
