package com.tang.ratelimiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Classname RedisRateLimiter
 * @Description [ TODO ]
 * @Author Tang
 * @Date 2020/3/23 20:47
 * @Created by ASUS
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RedisRateLimiter {

    /*
        每秒允许的请求数
     */
    int value() default 5;
}
