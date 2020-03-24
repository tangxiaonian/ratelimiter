package com.tang.ratelimiter.aop;

import com.google.common.collect.Lists;
import com.tang.ratelimiter.annotation.RedisRateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * @Classname RateLimiterAop
 * @Description [ TODO ]
 * @Author Tang
 * @Date 2020/3/22 20:49
 * @Created by ASUS
 */
@Slf4j
@Aspect
@Component
public class RedisRateLimiterAop {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private DefaultRedisScript<Number> defaultRedisScript = null;

    @Pointcut("execution(* com.tang.ratelimiter.controller..*.*(..))")
    public void pointcut() {}

    @Around("pointcut()")
    public Object aroundMethod(JoinPoint joinPoint) {

        boolean flage = false;

        Class aClass = joinPoint.getSignature().getDeclaringType();

        // 获取方法签名
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        // 获取方法上的注解
        RedisRateLimiter annotation = methodSignature.getMethod().getAnnotation(RedisRateLimiter.class);

        log.info("切入点执行{}类....",aClass.getName());

        // 方法是否需要限流
        if (annotation != null) {

            log.info("{}方法需要限流...",aClass.getName());

            Integer value = annotation.value();


            String key = "redis:limiter:" + (System.currentTimeMillis() / 1000);

            //  原子操作:  执行lua脚本   返回脚本结果                            传入脚本的 key  , value
            Number number = stringRedisTemplate.execute(this.defaultRedisScript, Lists.newArrayList(key), value + "");

            if (number != null && number.intValue() != 0) {

                log.info("限流时间段内访问第:{}次",number);

                flage = true;

            }else {

                log.info("限流次数已到达,对{}方法进行熔断...",aClass.getName());

                fallbackMethod();
            }

        }else {

            flage = true;

            log.info("{}方法不需要限流...",joinPoint.getSignature().getName());
        }

        if (flage) {

            // 执行业务
            ProceedingJoinPoint proceedingJoinPoint = (ProceedingJoinPoint)joinPoint;

            try {
                return proceedingJoinPoint.proceed(joinPoint.getArgs());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        }
        return null;
    }

    @PostConstruct
    public void init() {

        DefaultRedisScript<Number> redisScript = new DefaultRedisScript<>();

        //读取 lua 脚本
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("ratelimiter.lua")));
        // 返回结果类型
        redisScript.setResultType(Number.class);

        this.defaultRedisScript = redisScript;
    }

    /**
     * 熔断方法
     */
    private void fallbackMethod() {

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes)RequestContextHolder
                .currentRequestAttributes();

        HttpServletResponse response = servletRequestAttributes.getResponse();

        assert response != null;

        response.setHeader("Content-Type","text/html;charset=utf-8");

        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();
            printWriter.println("网络有点拥挤哦！请刷新重试....");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            printWriter.close();
        }

    }

}