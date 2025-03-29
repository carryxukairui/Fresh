package com.debug.fresh.config;

import com.debug.fresh.interceptor.AuthInterceptor;
import com.debug.fresh.interceptor.IsLoginInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Resource
    private final AuthInterceptor authInterceptor;

    // 通过构造函数注入 AuthInterceptor
    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns("/user/register")
            .excludePathPatterns("/user/loginByCode")
            .excludePathPatterns("/user/loginByPassword")
            .excludePathPatterns("/user/code");

    }


}
