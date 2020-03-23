package com.tang.ratelimiter.config;

import com.tang.ratelimiter.annotation.AccessLimit;
import com.tang.ratelimiter.utils.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @Classname RateLimiterInterceptor
 * @Description [ TODO ]
 * @Author Tang
 * @Date 2020/3/23 13:46
 * @Created by ASUS
 */
public class RateLimiterInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 是否向下执行
    private static Boolean result = false;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println( "同一主机多次请求,尝试限流..." );

        // 是不是执行controller的方法
        if (handler instanceof HandlerMethod) {

            HandlerMethod handlerMethod = (HandlerMethod) handler;

            Optional<AccessLimit> annotation = Optional.ofNullable(handlerMethod.getMethod().getAnnotation(AccessLimit.class));

            annotation.ifPresent((item) -> {

                ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();

                double limit = item.limit();

                long sec = item.sec();

                // 获取IP
                String remoteIp = IPUtils.getRemoteIp(request);

                // IP + 请求路径
                String key = remoteIp + request.getRequestURI();

                // 当前请求了多少次
                String currentCountRequestNumber = stringValueOperations.get(key);

                // key 存在
                if (currentCountRequestNumber != null) {

                    if (Integer.valueOf(currentCountRequestNumber) >= limit) {

                        System.out.println( "单位时间内次数已用尽...,限流开始..." );

                        // 限流
                        this.fallbackMethod();

                        result = false;

                    } else {
                        // 自增量
                        stringValueOperations.increment(key);

                        result = true;
                    }

                } else {

                    stringValueOperations.set(key, "1", sec, TimeUnit.SECONDS);

                    result = true;
                }


            });

        }
        return result;
    }

    /**
     * 熔断方法
     */
    private void fallbackMethod() {

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
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


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}