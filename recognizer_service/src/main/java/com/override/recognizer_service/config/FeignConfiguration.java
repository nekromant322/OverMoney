package com.override.recognizer_service.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

    private final String HEADER_NAME = "X-INTERNAL-KEY";

    @Value("${authorization-header.header-value}")
    private String headerValue;

    @Value("${deepseek.auth-token}")
    private String authToken;

    @Bean
    public feign.Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(HEADER_NAME, headerValue);
            requestTemplate.header("Authorization", "Bearer " + authToken);
        };
    }
}