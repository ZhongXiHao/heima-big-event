package com.cshlands.interceptors;

import com.cshlands.exception.BusinessException;
import com.cshlands.utils.JwtUtil;
import com.cshlands.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    public LoginInterceptor(JwtUtil jwtUtil, StringRedisTemplate stringRedisTemplate) {
        this.jwtUtil = jwtUtil;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                throw new RuntimeException();
            }
            String token = header.substring(7);

            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String redisToken = operations.get(token);
            if (redisToken == null) {
                throw new RuntimeException();
            }

            Map<String, Object> parsed = jwtUtil.parseToken(token);
            ThreadLocalUtil.set(parsed);
            return true;
        } catch (Exception e) {
            throw BusinessException.unauthorized("未授权");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ThreadLocalUtil.remove();
    }
}
