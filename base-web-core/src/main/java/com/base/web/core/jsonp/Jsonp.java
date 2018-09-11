package com.base.web.core.jsonp;

import java.lang.annotation.*;

/**
 * Created by   on 16-5-26.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Jsonp {
    String value() default "callback";
}
