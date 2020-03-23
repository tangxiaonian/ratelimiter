package com.tang.ratelimiter.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @Classname IPUtils
 * @Description [ TODO ]
 * @Author Tang
 * @Date 2020/3/23 14:23
 * @Created by ASUS
 */
public class IPUtils {

    public static String getRemoteIp(HttpServletRequest request) {

        String ip = request.getHeader("x-real-ip");

        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        // 过滤反向代理的IP
        String[] stemps = ip.split(",");

        if (stemps.length >= 1) {
            // 得到第一个IP，即客户端真实IP
            ip = stemps[0];
        }

        ip = ip.trim();
        if (ip.length() > 23) {
            ip = ip.substring(0, 23);
        }

        return ip;
    }

}