package com.scm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.http.HttpMethod;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许特定源
        config.addAllowedOrigin("http://127.0.0.1:8080");
        config.addAllowedOrigin("http://localhost:8080");
        
        // 允许跨域的请求头
        config.addAllowedHeader("*");
        
        // 允许跨域的请求方法
        config.addAllowedMethod(HttpMethod.GET.name());
        config.addAllowedMethod(HttpMethod.POST.name());
        config.addAllowedMethod(HttpMethod.PUT.name());
        config.addAllowedMethod(HttpMethod.DELETE.name());
        config.addAllowedMethod(HttpMethod.OPTIONS.name());
        
        // 允许携带cookie
        config.setAllowCredentials(true);
        
        // 暴露响应头
        config.addExposedHeader("*");
        
        // 设置预检请求的有效期
        config.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
} 