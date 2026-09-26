package com.cshlands.config;


import com.cshlands.interceptors.LoginInterceptor;
import com.cshlands.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 放行登录和注册接口
        registry.addInterceptor(loginInterceptor).excludePathPatterns("/auth/login", "/auth/register");
    }
}
