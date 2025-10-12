package com.dashboard.config;

import com.dashboard.interceptor.ApiLoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ApiLoggingInterceptor apiLoggingInterceptor;

    public WebConfig(ApiLoggingInterceptor apiLoggingInterceptor) {
        this.apiLoggingInterceptor = apiLoggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiLoggingInterceptor)
                .addPathPatterns("/**") // Only log /* endpoints
                .excludePathPatterns("/api/health", "/api/actuator/**"); // Exclude health checks
    }
}