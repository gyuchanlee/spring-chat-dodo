package com.dodo.spring_chat_dodo.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // 일단 다 열어둠
                .allowedMethods("*")
                .exposedHeaders("location") // 헤더에 어떤것을 보낼지 설정
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true);
    }
}
