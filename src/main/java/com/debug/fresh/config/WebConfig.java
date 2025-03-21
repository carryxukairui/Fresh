package com.debug.fresh.config;

import com.debug.fresh.interceptor.IsLoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new IsLoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(List.of("/user/**"));// 排除登录和注册接口（不需要 Token 校验）
    }


}
