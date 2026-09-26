package com.cshlands.interceptors;

import com.cshlands.exception.BusinessException;
import com.cshlands.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String token = request.getHeader("Authorization");
            Map<String, Object> parsed = jwtUtil.parseToken(token);
            return true;
        } catch (Exception e) {
            throw BusinessException.unauthorized("未授权");
        }
    }
}
