package com.tang.ratelimiter.controller;

import com.tang.ratelimiter.annotation.AccessLimit;
import com.tang.ratelimiter.annotation.RateLimiter;
import com.tang.ratelimiter.annotation.RedisRateLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname RateLimiterController
 * @Description [ TODO ]
 * @Author Tang
 * @Date 2020/3/22 19:21
 * @Created by ASUS
 */
@RestController
@RequestMapping("/rate")
public class RateLimiterController {

    @GetMapping("/start")
    public String testRateLimiter() {

        System.out.println("走业务逻辑...");

        return "拿到数据了...";
    }

    @RateLimiter(rate = 2)
    @GetMapping("/limiterV1")
    public String testRateLimiterV1() {

        System.out.println("走业务逻辑...");

        return "拿到数据了...";
    }

    @AccessLimit(limit = 5,sec = 5)
    @GetMapping("/limiterV2")
    public String testRateLimiterV2() {

        System.out.println("走业务逻辑...");

        return "拿到数据了...";
    }


    @RedisRateLimiter(value = 5)
    @GetMapping("/limiterV3")
    public String testRateLimiterV3() {

        System.out.println("走业务逻辑...");

        return "拿到数据了...";
    }


}