package com.override.recognizer_service.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

    @Value("${authorization-header.header-name}")
    private String headerName;

    @Value("${authorization-header.header-value}")
    private String headerValue;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(headerName, headerValue);
        };
    }
}
