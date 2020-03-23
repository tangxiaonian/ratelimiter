package com.tang.ratelimiter.aop;

import com.tang.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * @Classname RateLimiterAop
 * @Description [ TODO ]
 * @Author Tang
 * @Date 2020/3/22 20:49
 * @Created by ASUS
 */
@Slf4j
//@Aspect
//@Component
public class RateLimiterAop {

    private com.google.common.util.concurrent.RateLimiter rateLimiter =
            com.google.common.util.concurrent.RateLimiter.create(5);

    @Pointcut("execution(* com.tang.ratelimiter.controller..*.*(..))")
    public void pointcut() {}

    /**
     * 方法执行前
     */
    @Before(value = "pointcut()")
    public void beforeMethod() {

    }

    @Around("pointcut()")
    public Object aroundMethod(JoinPoint joinPoint) {

        boolean flage = false;

        Class aClass = joinPoint.getSignature().getDeclaringType();

        // 获取方法签名
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        // 获取方法上的注解
        RateLimiter annotation = methodSignature.getMethod().getAnnotation(RateLimiter.class);

        log.info("切入点执行{}类....",aClass.getName());

        // 方法是否需要限流
        if (annotation != null) {

            log.info("{}方法需要限流...\n",aClass.getName());

            double rate = annotation.rate();

            long timeout = annotation.timeout();

            this.rateLimiter.setRate(rate);
            // 令牌获取成功
            if (this.rateLimiter.tryAcquire(timeout, TimeUnit.MICROSECONDS)) {

                flage = true;

            }else {

                log.info("{}方法熔断...\n",joinPoint.getSignature().getName());

                // 令牌获取失败
                this.fallbackMethod();

            }

        }else {
            flage = true;
            log.info("{}方法不需要限流...\n",joinPoint.getSignature().getName());
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