package com.tang.ratelimiter.annotation;

import java.lang.annotation.*;

/**
 * @Classname AccessLimit
 * @Description [ TODO ]
 * @Author Tang
 * @Date 2020/3/23 14:27
 * @Created by ASUS
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AccessLimit {

    /**
     *
     * 默认 1秒内 可以接受5个请求
     *
     */

    // 每秒接受的请求数
    int limit() default 5;

    // 秒数
    int sec() default 1;

}
