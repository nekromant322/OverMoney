package com.override.recognizer_service.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FeignConfiguration {

    private final String HEADER_NAME = "X-INTERNAL-KEY";

    @Value("${authorization-header.header-value}")
    private String headerValue;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(HEADER_NAME, headerValue);
        };
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}