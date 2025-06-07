package com.override.payment_service.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YooKassaFeignConfig {
    @Value("${yookassa.shop-id}")
    private String shopId;

    @Value("${yookassa.secret-key}")
    private String secretKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String credentials = shopId + ":" + secretKey;
            String encodedCredentials = java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
            requestTemplate.header("Authorization", "Basic " + encodedCredentials);
            requestTemplate.header("Content-Type", "application/json");
        };
    }
}