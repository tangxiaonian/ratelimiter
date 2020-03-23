package com.tang.ratelimiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Classname RateLimiter
 * @Description [ TODO ]
 * @Author Tang
 * @Date 2020/3/22 20:57
 * @Created by ASUS
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RateLimiter {

    double rate() default 5;

    long timeout() default 500;

}
