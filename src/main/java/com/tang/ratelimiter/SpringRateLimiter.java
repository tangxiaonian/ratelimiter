package com.tang.ratelimiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @Classname SpringRateLimiter
 * @Description [ TODO ]
 * @Author Tang
 * @Date 2020/3/22 19:20
 * @Created by ASUS
 */
//@EnableAspectJAutoProxy
@SpringBootApplication
public class SpringRateLimiter {

    public static void main(String[] args) {

        SpringApplication.run(SpringRateLimiter.class, args);

    }

}