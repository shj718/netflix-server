package com.example.demo.src;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {
    private static final long MAX_AGE_SECS = 3600L;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // 허용할 Origin 추가
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // 사용할 METHOD 추가
                .allowedHeaders("*") // 모든 헤더 추가
                .maxAge(MAX_AGE_SECS);
    }
}
